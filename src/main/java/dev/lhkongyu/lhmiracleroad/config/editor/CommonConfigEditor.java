package dev.lhkongyu.lhmiracleroad.config.editor;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.config.ConfigTracker;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class CommonConfigEditor {
    static final String FILE_NAME = "lhmiracleroad-common.toml";
    private static final Gson GSON = new Gson();
    private static final List<Option> OPTIONS = options();

    private CommonConfigEditor() {}

    static JsonObject snapshot(byte[] original) throws IOException {
        return snapshot(read(original));
    }

    private static JsonObject snapshot(CommentedConfig data) {
        JsonObject document = new JsonObject();
        for (Option option : OPTIONS) {
            Object stored = data.get(option.path());
            var spec = option.spec();
            Object value = stored != null && spec.test(stored) ? stored : spec.getDefault();
            if (!document.has(option.group())) document.add(option.group(), new JsonObject());
            document.getAsJsonObject(option.group()).add(option.key(), GSON.toJsonTree(value));
        }
        return document;
    }

    static void validate(JsonObject document) {
        Set<String> expectedGroups = new HashSet<>();
        Set<List<String>> expectedPaths = new HashSet<>();
        for (Option option : OPTIONS) {
            expectedGroups.add(option.group());
            expectedPaths.add(option.path());
            value(document, option);
        }
        if (!document.keySet().equals(expectedGroups)) throw new IllegalArgumentException("Unknown common configuration group");
        document.entrySet().forEach(group -> {
            for (String key : group.getValue().getAsJsonObject().keySet()) {
                if (!expectedPaths.contains(List.of(group.getKey(), key))) {
                    throw new IllegalArgumentException("Unknown common configuration key: " + key);
                }
            }
        });
    }

    static byte[] write(byte[] original, JsonObject document) throws IOException {
        validate(document);
        CommentedConfig config = read(original);
        JsonObject previous = snapshot(config);
        if (original != null && previous.equals(document)) return original;
        validateChangedFormulas(previous.getAsJsonObject("base"), document.getAsJsonObject("base"));

        for (Option option : OPTIONS) {
            config.set(option.path(), value(document, option));
            if (config.getComment(option.path()) == null && option.spec().getComment() != null) {
                config.setComment(option.path(), option.spec().getComment());
            }
        }
        StringWriter writer = new StringWriter();
        new TomlWriter().write(config, writer);
        return writer.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static void validateChangedFormulas(JsonObject previous, JsonObject edited) {
        for (String key : List.of("empirical_calculation_formula", "experience_acquisition_formula")) {
            if (previous.get(key).equals(edited.get(key))) continue;
            String formula = edited.get(key).getAsString();
            try {
                if (key.equals("empirical_calculation_formula")) LHMiracleRoadTool.evaluateFormula(formula, 10);
                else LHMiracleRoadTool.evaluateFormula(formula, 15, 20, 4, 2, 1, 1);
            } catch (RuntimeException | StackOverflowError invalid) {
                throw new IllegalArgumentException("Invalid formula: " + key, invalid);
            }
        }
    }

    static void reload() throws IOException {
        var registered = ConfigTracker.INSTANCE.fileMap().get(FILE_NAME);
        if (registered == null || !(registered.getConfigData() instanceof CommentedFileConfig live)) {
            throw new IOException("Forge has not loaded " + FILE_NAME);
        }
        // NightConfig only watches file modifications, so an atomic replacement might not notify it.
        live.load();
        LHMiracleRoadConfig.COMMON_SPEC.afterReload();
    }

    private static Object value(JsonObject document, Option option) {
        JsonElement group = document.get(option.group());
        JsonElement element = group instanceof JsonObject object ? object.get(option.key()) : null;
        Object parsed = null;
        Object defaultValue = option.spec().getDefault();
        try {
            if (element != null && element.isJsonPrimitive()) {
                var primitive = element.getAsJsonPrimitive();
                if (defaultValue instanceof Boolean && primitive.isBoolean()) parsed = primitive.getAsBoolean();
                if (defaultValue instanceof Integer && primitive.isNumber()) parsed = primitive.getAsBigDecimal().intValueExact();
                if (defaultValue instanceof Double && primitive.isNumber()) {
                    double number = primitive.getAsDouble();
                    if (Double.isFinite(number)) parsed = number;
                }
                if (defaultValue instanceof String && primitive.isString()) parsed = primitive.getAsString();
            } else if (defaultValue instanceof List<?> && element != null && element.isJsonArray()) {
                List<String> list = new ArrayList<>();
                for (JsonElement entry : element.getAsJsonArray()) {
                    if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()) throw invalid(option);
                    list.add(entry.getAsString());
                }
                parsed = list;
            }
        } catch (ArithmeticException | NumberFormatException failure) {
            throw invalid(option);
        }
        if (parsed == null || !option.spec().test(parsed)) throw invalid(option);
        return parsed;
    }

    private static IllegalArgumentException invalid(Option option) {
        var range = option.spec().getRange();
        String details = range == null ? "" : " (" + range + ")";
        return new IllegalArgumentException("Invalid value for " + option.group() + "." + option.key() + details);
    }

    private static CommentedConfig read(byte[] original) throws IOException {
        try {
            String text = original == null ? "" : StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(original)).toString();
            return new TomlParser().parse(new StringReader(text));
        } catch (com.electronwill.nightconfig.core.io.ParsingException failure) {
            throw new IOException("Invalid TOML in " + FILE_NAME, failure);
        }
    }

    private static List<Option> options() {
        List<Option> result = new ArrayList<>();
        LHMiracleRoadConfig.COMMON_SPEC.getSpec().entrySet().forEach(group -> {
            UnmodifiableConfig fields = group.getValue();
            fields.entrySet().forEach(field ->
                    result.add(new Option(group.getKey(), field.getKey(), field.getValue())));
        });
        return List.copyOf(result);
    }

    private record Option(String group, String key, ValueSpec spec) {
        List<String> path() {
            return List.of(group, key);
        }
    }
}
