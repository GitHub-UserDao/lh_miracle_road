package dev.lhkongyu.lhmiracleroad.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ShowGuiAttributeConfig {
    private static final String DEFAULT_RESOURCE = "/data/lhmiracleroad/showGuiAttribute/attribute.json";
    private static volatile List<Entry> entries = List.of();

    private ShowGuiAttributeConfig() {}

    public static void load() {
        Path file = FMLPaths.CONFIGDIR.get().resolve("lhmiracleroad").resolve("show_gui_attribute.json");
        try {
            if (Files.notExists(file)) {
                Files.createDirectories(file.getParent());
                try (InputStream defaults = openDefaults()) {
                    Files.copy(defaults, file);
                }
            }
            try (Reader reader = Files.newBufferedReader(file)) {
                entries = parse(reader, file.toString());
            }
        } catch (IOException | JsonParseException ex) {
            LHMiracleRoad.LOGGER.warn("Could not read GUI attribute config {}. Using bundled defaults.", file, ex);
            try (Reader reader = new InputStreamReader(openDefaults(), StandardCharsets.UTF_8)) {
                entries = parse(reader, DEFAULT_RESOURCE);
            } catch (IOException | JsonParseException defaultsError) {
                LHMiracleRoad.LOGGER.error("Could not read bundled GUI attribute defaults.", defaultsError);
                entries = List.of();
            }
        }
    }

    public static List<Entry> entries() {
        return entries;
    }

    private static InputStream openDefaults() throws IOException {
        InputStream stream = ShowGuiAttributeConfig.class.getResourceAsStream(DEFAULT_RESOURCE);
        if (stream == null) throw new IOException("Missing resource: " + DEFAULT_RESOURCE);
        return stream;
    }

    static List<Entry> parse(Reader reader, String source) {
        JsonElement root = JsonParser.parseReader(reader);
        if (!root.isJsonArray()) throw new JsonParseException("Expected an array of GUI attributes");

        List<Entry> result = new ArrayList<>();
        int index = 0;
        for (JsonElement element : root.getAsJsonArray()) {
            index++;
            if (!element.isJsonObject()) {
                LHMiracleRoad.LOGGER.warn("Ignoring non-object GUI attribute entry {} in {}", index, source);
                continue;
            }
            JsonObject object = element.getAsJsonObject();
            String name = readString(object, "attribute");
            ResourceLocation id = ResourceLocation.tryParse(name);
            if (id == null || id.getPath().isEmpty()) {
                LHMiracleRoad.LOGGER.warn("Ignoring invalid GUI attribute ID '{}' in entry {} in {}", name, index, source);
                continue;
            }
            String mode = readString(object, "show_value_type");
            try {
                ValueType type = ValueType.valueOf(mode.toUpperCase(Locale.ROOT));
                result.add(new Entry(name, type));
            } catch (IllegalArgumentException ex) {
                LHMiracleRoad.LOGGER.warn("Ignoring invalid GUI attribute display type '{}' in entry {} in {}", mode, index, source);
            }
        }
        return List.copyOf(result);
    }

    private static String readString(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()
                ? element.getAsString().trim() : "";
    }

    public record Entry(String attribute, ValueType valueType) {}

    public enum ValueType {
        BASE(false, false),
        BASE_PERCENTAGE(true, false),
        EXTRA_BASE(false, true),
        EXTRA_PERCENTAGE(true, true);

        private final boolean subtractDefault;
        private final boolean percentage;

        ValueType(boolean subtractDefault, boolean percentage) {
            this.subtractDefault = subtractDefault;
            this.percentage = percentage;
        }

        public String format(double total, double defaultValue) {
            String suffix = percentage ? "%" : "";
            if (!Double.isFinite(total) || (subtractDefault && !Double.isFinite(defaultValue))) {
                return Double.toString(subtractDefault ? total - defaultValue : total) + suffix;
            }
            BigDecimal value = BigDecimal.valueOf(total);
            if (subtractDefault) value = value.subtract(BigDecimal.valueOf(defaultValue));
            if (percentage) value = value.movePointRight(2);
            BigDecimal rounded = value.setScale(2, RoundingMode.HALF_UP);
            String prefix = subtractDefault && rounded.signum() >= 0 ? "+" : "";
            BigDecimal trimmed = rounded.stripTrailingZeros();
            return prefix + (trimmed.scale() <= 0 ? trimmed : rounded).toPlainString() + suffix;
        }
    }
}
