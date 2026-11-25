package dev.lhkongyu.lhmiracleroad.config.punishment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class PunishmentConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve(LHMiracleRoad.MODID).resolve("punishment");
    private static final String PRESETS_FILE = "punishment.json";
    private static volatile Map<String, Map<String, JsonObject>> equipment = Map.of();
    private static volatile Map<String, Preset> presets = Map.of();

    private PunishmentConfig() {}

    public static void createFromIronSword(ResourceLocation itemId, String punishmentId, int heavy) throws IOException {
        if (!ForgeRegistries.ITEMS.containsKey(itemId) || ForgeRegistries.ITEMS.getValue(itemId) == Items.AIR) {
            throw new IllegalArgumentException("Unknown item: " + itemId);
        }
        String namespace = itemId.getNamespace();
        if (namespace.equals(".") || namespace.equals("..")) {
            throw new IllegalArgumentException("Invalid item namespace: " + namespace);
        }
        Path target = FileUtil.createPathToResource(PATH.resolve(namespace), itemId.getPath(), ".json");
        Path template = defaultsPath().resolve("minecraft").resolve("iron_sword.json");
        JsonObject data;
        try (Reader reader = Files.newBufferedReader(template)) {
            data = GSON.fromJson(reader, JsonObject.class);
        }
        applyPreset(data, punishmentId, heavy);
        writeJson(target, data);
    }

    private static void applyPreset(JsonObject data, String punishmentId, int heavy) {
        data.addProperty("heavy", heavy);
        Preset preset = presets.get(punishmentId);
        for (JsonElement requirement : data.getAsJsonArray("attribute_need")) {
            JsonObject need = requirement.getAsJsonObject();
            need.addProperty("punishment_id", punishmentId);
            if (preset != null && preset.needPoints() != null) {
                need.addProperty("need_points", preset.needPoints());
            }
        }
    }

    public static synchronized boolean addPreset(String id, double value, int needPoints, Collection<String> attributes) throws IOException {
        Path target = PATH.resolve(PRESETS_FILE);
        JsonArray entries = readPresets(Files.notExists(target) ? defaultsPath().resolve(PRESETS_FILE) : target);
        for (JsonElement entry : entries) {
            if (id.equals(presetId(entry))) return false;
        }

        JsonArray effects = new JsonArray();
        for (String attribute : attributes) {
            JsonObject effect = new JsonObject();
            effect.addProperty("attribute", attribute);
            effect.addProperty("value", value);
            effect.addProperty("operation", "multiply_total");
            effects.add(effect);
        }
        JsonObject entry = new JsonObject();
        entry.addProperty("id", id);
        entry.addProperty("need_points", needPoints);
        entry.add("punishments", effects);
        entries.add(entry);

        writeJson(target, entries);
        presets = indexPresets(entries);
        return true;
    }

    private static void writeJson(Path target, JsonElement data) throws IOException {
        Files.createDirectories(target.getParent());
        Path temp = Files.createTempFile(target.getParent(), "punishment-", ".tmp");
        try {
            Files.writeString(temp, GSON.toJson(data) + "\n");
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    public static void load() {
        try {
            Files.createDirectories(PATH);
            Path defaults = defaultsPath();
            Path presetsFile = PATH.resolve(PRESETS_FILE);
            if (Files.notExists(presetsFile)) Files.copy(defaults.resolve(PRESETS_FILE), presetsFile);

            List<Path> files = equipmentFiles(PATH);
            if (files.isEmpty() && !Files.exists(PATH.resolve(".editor-managed"))) {
                for (Path source : equipmentFiles(defaults)) {
                    Path target = PATH.resolve(defaults.relativize(source).toString());
                    Files.createDirectories(target.getParent());
                    if (Files.notExists(target)) Files.copy(source, target);
                }
                files = equipmentFiles(PATH);
            }

            Map<String, Map<String, JsonObject>> loadedEquipment = new HashMap<>();
            for (Path file : files) loadEquipment(file, loadedEquipment);
            equipment = Map.copyOf(loadedEquipment);
            presets = loadPresets(presetsFile);
        } catch (IOException ex) {
            LHMiracleRoad.LOGGER.error("Failed to initialize punishment config at {}", PATH, ex);
        }
    }

    public static void reloadFromEditor() throws IOException {
        Path presetsFile = PATH.resolve(PRESETS_FILE);
        var loadedPresets = indexPresets(readPresets(Files.exists(presetsFile) ? presetsFile : defaultsPath().resolve(PRESETS_FILE)));
        Map<String, Map<String, JsonObject>> loadedEquipment = new HashMap<>();
        for (Path file : equipmentFiles(PATH)) loadEquipment(file, loadedEquipment);
        equipment = Map.copyOf(loadedEquipment);
        presets = loadedPresets;
    }

    private static Path defaultsPath() {
        return ModList.get().getModFileById(LHMiracleRoad.MODID).getFile()
                .findResource("defaults", LHMiracleRoad.MODID, "punishment");
    }

    private static List<Path> equipmentFiles(Path root) throws IOException {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(Files::isRegularFile)
                    .filter(file -> root.relativize(file).getNameCount() > 1)
                    .filter(file -> file.getFileName().toString().endsWith(".json"))
                    .sorted().toList();
        }
    }

    private static void loadEquipment(Path file, Map<String, Map<String, JsonObject>> loaded) {
        Path relative = PATH.relativize(file);
        String namespace = relative.getName(0).toString();
        String name = relative.subpath(1, relative.getNameCount()).toString().replace('\\', '/');
        ResourceLocation id = ResourceLocation.tryBuild(namespace, name.substring(0, name.length() - ".json".length()));
        if (id == null) {
            LHMiracleRoad.LOGGER.warn("Invalid item ID for punishment config {}", file);
            return;
        }
        if (!ForgeRegistries.ITEMS.containsKey(id)) return;

        try (Reader reader = Files.newBufferedReader(file)) {
            JsonObject data = GSON.fromJson(reader, JsonObject.class);
            if (data != null) {
                loaded.computeIfAbsent(id.getNamespace(), ignored -> new HashMap<>()).put(id.getPath(), data);
            }
        } catch (IOException | JsonParseException ex) {
            LHMiracleRoad.LOGGER.warn("Failed to read equipment punishment config {}", file, ex);
        }
    }

    private static Map<String, Preset> loadPresets(Path file) {
        try {
            return indexPresets(readPresets(file));
        } catch (IOException ex) {
            LHMiracleRoad.LOGGER.warn("Failed to read punishment presets {}", file, ex);
            return Map.of();
        }
    }

    private static JsonArray readPresets(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonArray entries = GSON.fromJson(reader, JsonArray.class);
            if (entries == null) throw new IOException("Expected an array of punishment presets in " + file);
            return entries;
        } catch (JsonParseException ex) {
            throw new IOException("Invalid punishment presets in " + file, ex);
        }
    }

    private static Map<String, Preset> indexPresets(JsonArray entries) {
        Map<String, Preset> result = new HashMap<>();
        for (JsonElement entry : entries) {
            String id = presetId(entry);
            if (id == null) continue;
            JsonObject data = entry.getAsJsonObject();
            JsonElement effects = data.get("punishments");
            if (effects != null && effects.isJsonArray()) {
                result.put(id, new Preset(effects.getAsJsonArray(), readNeedPoints(data.get("need_points"))));
            }
        }
        return Map.copyOf(result);
    }

    private static String presetId(JsonElement entry) {
        if (!entry.isJsonObject()) return null;
        JsonElement id = entry.getAsJsonObject().get("id");
        return id != null && id.isJsonPrimitive() && id.getAsJsonPrimitive().isString() ? id.getAsString() : null;
    }

    private static Integer readNeedPoints(JsonElement value) {
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) return null;
        try {
            int points = value.getAsBigDecimal().intValueExact();
            return points < 0 ? null : points;
        } catch (ArithmeticException | NumberFormatException ex) {
            return null;
        }
    }

    public static Map<String, Map<String, JsonObject>> getEquipment() {
        return equipment;
    }

    public static JsonArray getPunishment(String id) {
        Preset preset = presets.get(id);
        return preset == null ? null : preset.effects();
    }

    public static Set<String> getPunishmentIds() {
        return presets.keySet();
    }

    private record Preset(JsonArray effects, Integer needPoints) {}
}
