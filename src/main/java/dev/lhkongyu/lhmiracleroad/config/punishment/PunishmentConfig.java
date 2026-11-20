package dev.lhkongyu.lhmiracleroad.config.punishment;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class PunishmentConfig {
    private static final Gson GSON = new Gson();
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve(LHMiracleRoad.MODID).resolve("punishment");
    private static final String PRESETS_FILE = "punishment.json";
    private static volatile Map<String, Map<String, JsonObject>> equipment = Map.of();
    private static volatile Map<String, JsonArray> presets = Map.of();

    private PunishmentConfig() {}

    public static void createFromIronSword(ResourceLocation itemId) throws IOException {
        if (!ForgeRegistries.ITEMS.containsKey(itemId) || ForgeRegistries.ITEMS.getValue(itemId) == Items.AIR) {
            throw new IllegalArgumentException("Unknown item: " + itemId);
        }
        String namespace = itemId.getNamespace();
        if (namespace.equals(".") || namespace.equals("..")) {
            throw new IllegalArgumentException("Invalid item namespace: " + namespace);
        }
        Path target = FileUtil.createPathToResource(PATH.resolve(namespace), itemId.getPath(), ".json");
        Files.createDirectories(target.getParent());
        Files.copy(defaultsPath().resolve("minecraft").resolve("iron_sword.json"), target);
    }

    public static void load() {
        try {
            Files.createDirectories(PATH);
            Path defaults = defaultsPath();
            Path presetsFile = PATH.resolve(PRESETS_FILE);
            if (Files.notExists(presetsFile)) Files.copy(defaults.resolve(PRESETS_FILE), presetsFile);

            List<Path> files = equipmentFiles(PATH);
            if (files.isEmpty()) {
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
        Item item = ForgeRegistries.ITEMS.getValue(id);
        String description = item.getDescriptionId();
        int split = description.lastIndexOf('.');
        if (split < 0) return;

        try (Reader reader = Files.newBufferedReader(file)) {
            JsonObject data = GSON.fromJson(reader, JsonObject.class);
            if (data != null) {
                loaded.computeIfAbsent(description.substring(0, split), ignored -> new HashMap<>())
                        .put(description.substring(split + 1), data);
            }
        } catch (IOException | JsonParseException ex) {
            LHMiracleRoad.LOGGER.warn("Failed to read equipment punishment config {}", file, ex);
        }
    }

    private static Map<String, JsonArray> loadPresets(Path file) {
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonArray entries = GSON.fromJson(reader, JsonArray.class);
            Map<String, JsonArray> result = new HashMap<>();
            if (entries == null) return result;
            for (JsonElement entry : entries) {
                if (!entry.isJsonObject()) continue;
                JsonObject data = entry.getAsJsonObject();
                JsonElement id = data.get("id");
                JsonElement effects = data.get("punishments");
                if (id != null && id.isJsonPrimitive() && id.getAsJsonPrimitive().isString()
                        && effects != null && effects.isJsonArray()) {
                    result.put(id.getAsString(), effects.getAsJsonArray());
                }
            }
            return Map.copyOf(result);
        } catch (IOException | JsonParseException ex) {
            LHMiracleRoad.LOGGER.warn("Failed to read punishment presets {}", file, ex);
            return Map.of();
        }
    }

    public static Map<String, Map<String, JsonObject>> getEquipment() {
        return equipment;
    }

    public static JsonArray getPunishment(String id) {
        return presets.get(id);
    }
}
