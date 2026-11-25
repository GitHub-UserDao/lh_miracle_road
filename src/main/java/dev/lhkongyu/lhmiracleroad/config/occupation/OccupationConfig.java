package dev.lhkongyu.lhmiracleroad.config.occupation;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.config.initItem.InitItemConfig;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class OccupationConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson RESET_GSON = GSON.newBuilder().serializeNulls().create();
    private static final Type OCCUPATION_LIST_TYPE = new TypeToken<List<Occupation>>(){}.getType();
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve(LHMiracleRoad.MODID).resolve("Occupation");

    private static final Map<String, Occupation> OCCUPATION_BY_ID = new HashMap<>();
    private static JsonObject outcastTemplate;

    public static void load() {
        try {
            Files.createDirectories(PATH);

            List<Path> files = findJsonFiles(PATH);
            if (files.isEmpty()) {
                for (Occupation occupation : createDefaults()) {
                    if (occupation == null || occupation.id == null || occupation.id.isBlank()) {
                        continue;
                    }
                    writeFile(PATH.resolve(occupation.id + ".json"), occupation);
                }
            }

            reloadFromEditor();
        } catch (Exception e) {
            LHMiracleRoad.LOGGER.error("Failed load occupation config", e);
        }
    }

    public static void reloadFromEditor() throws IOException {
        var loaded = new HashMap<String, Occupation>();
        JsonObject template = null;
        for (Path file : findJsonFiles(PATH)) {
            for (Occupation occupation : readFile(file)) {
                if (occupation == null || occupation.id == null || occupation.id.isBlank()) continue;
                if ("outcast".equals(occupation.id)) template = GSON.toJsonTree(occupation).getAsJsonObject();
                filterUnavailableRewards(occupation);
                loaded.put(occupation.id, occupation);
            }
        }
        OCCUPATION_BY_ID.clear();
        OCCUPATION_BY_ID.putAll(loaded);
        outcastTemplate = template;
    }

    private static void filterUnavailableRewards(Occupation occupation) {
        if (occupation.occupationAttributes == null) return;

        ModList mods = ModList.get();
        occupation.occupationAttributes.removeIf(attribute -> {
            if (attribute == null || attribute.pointsRewards == null) return true;
            if (attribute.pointsRewards.isEmpty()) return false;

            attribute.pointsRewards.removeIf(reward -> {
                if (reward == null) return true;
                String modId = reward.getMod_id();
                return modId != null && !modId.isBlank() && !mods.isLoaded(modId);
            });
            return attribute.pointsRewards.isEmpty();
        });
    }

    private static List<Path> findJsonFiles(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        }
    }

    private static JsonElement readJson(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file)) {
            return GSON.fromJson(reader, JsonElement.class);
        }
    }

    private static List<Occupation> readFile(Path file) throws IOException {
        JsonElement element = readJson(file);
        if (element == null || element.isJsonNull()) return Collections.emptyList();
        if (element.isJsonArray()) {
            List<Occupation> occupations = GSON.fromJson(element, OCCUPATION_LIST_TYPE);
            return occupations == null ? Collections.emptyList() : occupations;
        }
        if (element.isJsonObject()) return List.of(GSON.fromJson(element, Occupation.class));
        throw new JsonParseException("Occupation config must be a json object or array: " + file);
    }

    private static void writeFile(Path file, Occupation occupation) throws IOException {
        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(occupation, writer);
        }
    }

    public static Occupation createFromOutcast(String id) throws IOException {
        if (id == null || !id.matches("[a-zA-Z0-9_-][a-zA-Z0-9_.-]*")) {
            throw new IllegalArgumentException("Invalid occupation ID");
        }
        Path target = PATH.resolve(id + ".json");
        if (OCCUPATION_BY_ID.containsKey(id) || Files.exists(target)) {
            throw new FileAlreadyExistsException(target.toString());
        }
        JsonObject template = outcastTemplate == null
                ? GSON.toJsonTree(defaultOutcast()).getAsJsonObject() : outcastTemplate.deepCopy();
        template.addProperty("id", id);
        template.addProperty("name", id);
        Occupation created = GSON.fromJson(template, Occupation.class);
        filterUnavailableRewards(created);

        Files.createDirectories(PATH);
        Path temp = Files.createTempFile(PATH, "occupation-", ".tmp");
        try {
            Files.writeString(temp, GSON.toJson(template));
            Files.move(temp, target);
            try {
                InitItemConfig.copyOutcastItemsIfAbsent(id);
            } catch (IOException e) {
                Files.delete(target);
                throw e;
            }
        } finally {
            Files.deleteIfExists(temp);
        }
        OCCUPATION_BY_ID.put(id, created);
        return created;
    }

    public static List<Occupation> resetDefaults() throws IOException {
        Map<String, JsonObject> templates = new LinkedHashMap<>();
        for (Occupation occupation : createDefaults()) {
            templates.put(occupation.id, GSON.toJsonTree(occupation).getAsJsonObject());
        }
        List<Occupation> defaults = templates.values().stream().map(template -> {
            Occupation occupation = GSON.fromJson(template, Occupation.class);
            filterUnavailableRewards(occupation);
            return occupation;
        }).toList();

        resetDefaultFiles(PATH, templates);
        defaults.forEach(occupation -> OCCUPATION_BY_ID.put(occupation.id, occupation));
        outcastTemplate = templates.get("outcast");
        return defaults;
    }

    private static void resetDefaultFiles(Path directory, Map<String, JsonObject> templates) throws IOException {
        Files.createDirectories(directory);
        Map<Path, JsonElement> updates = new LinkedHashMap<>();
        Set<String> foundIds = new HashSet<>();

        // Inspect all files before writing, including legacy arrays that mix default and custom occupations.
        for (Path file : findJsonFiles(directory)) {
            JsonElement original;
            try {
                original = readJson(file);
            } catch (JsonParseException ex) {
                throw new IOException("Invalid occupation config: " + file, ex);
            }
            if (original == null || original.isJsonNull()) continue;
            JsonElement updated;
            if (original.isJsonObject()) {
                updated = resetEntry(original, templates, foundIds);
            } else if (original.isJsonArray()) {
                JsonArray occupations = new JsonArray();
                for (JsonElement entry : original.getAsJsonArray()) {
                    occupations.add(resetEntry(entry, templates, foundIds));
                }
                updated = occupations;
            } else {
                throw new IOException("Occupation config must be a JSON object or array: " + file);
            }
            if (!updated.equals(original)) {
                if (Files.isSymbolicLink(file)) throw new IOException("Cannot replace linked occupation config: " + file);
                updates.put(file, updated);
            }
        }

        for (var update : updates.entrySet()) {
            writeResetFile(update.getKey(), update.getValue(), true);
        }
        for (var entry : templates.entrySet()) {
            String id = entry.getKey();
            if (foundIds.contains(id)) continue;
            Path file = directory.resolve(id + ".json");
            for (int suffix = 1; Files.exists(file, LinkOption.NOFOLLOW_LINKS); suffix++) {
                file = directory.resolve(id + "-default-" + suffix + ".json");
            }
            writeResetFile(file, entry.getValue(), false);
        }
    }

    private static JsonElement resetEntry(JsonElement entry, Map<String, JsonObject> templates, Set<String> foundIds) {
        if (!entry.isJsonObject()) return entry;
        JsonElement id = entry.getAsJsonObject().get("id");
        if (id == null || !id.isJsonPrimitive() || !id.getAsJsonPrimitive().isString()) return entry;
        String name = id.getAsString();
        JsonObject template = templates.get(name);
        if (template == null) return entry;
        foundIds.add(name);
        return template;
    }

    private static void writeResetFile(Path file, JsonElement data, boolean replace) throws IOException {
        Path temp = Files.createTempFile(file.getParent(), "occupation-", ".tmp");
        try {
            Files.writeString(temp, RESET_GSON.toJson(data) + "\n");
            if (replace) {
                try {
                    Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException ex) {
                    Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                Files.move(temp, file);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private static List<Occupation> createDefaults() {
        return List.of(
                createOccupation("assassin", "assassin",
                        attribute("hp", 9),
                        attribute("focus", 8),
                        attribute("power", 13),
                        attribute("sturdy", 9),
                        attribute("brain", 11),
                        attribute("agile", 18),
                        attribute("luck", 11),
                        attribute("reaction", 14)
                ),
                createOccupation("knight", "knight",
                        attribute("hp", 18),
                        attribute("focus", 9),
                        attribute("power", 16),
                        attribute("sturdy", 16),
                        attribute("brain", 8),
                        attribute("agile", 13),
                        attribute("luck", 9),
                        attribute("reaction", 9)
                ),
                createOccupation("mage", "mage",
                        attribute("hp", 8),
                        attribute("focus", 17),
                        attribute("power", 9),
                        attribute("sturdy", 8),
                        attribute("brain", 19),
                        attribute("agile", 12),
                        attribute("luck", 10),
                        attribute("reaction", 9)
                ),
                defaultOutcast(),
                createOccupation("samurai", "samurai",
                        attribute("hp", 14),
                        attribute("focus", 8),
                        attribute("power", 14),
                        attribute("sturdy", 13),
                        attribute("brain", 9),
                        attribute("agile", 14),
                        attribute("luck", 8),
                        attribute("reaction", 13)
                ),
                createOccupation("ranger", "ranger",
                        attribute("hp", 11),
                        attribute("focus", 8),
                        attribute("power", 17),
                        attribute("sturdy", 9),
                        attribute("brain", 9),
                        attribute("agile", 17),
                        attribute("luck", 9),
                        attribute("reaction", 12)
                )
        );
    }

    private static Occupation createOccupation(String id, String name, OccupationAttribute... attributes) {
        Occupation occupation = new Occupation();
        occupation.id = id;
        occupation.name = name;
        occupation.occupationAttributes = List.of(attributes);
        return occupation;
    }

    private static Occupation defaultOutcast() {
        return createOccupation("outcast", "outcast", attribute("hp", 10), attribute("focus", 10),
                attribute("power", 10), attribute("sturdy", 10), attribute("brain", 10),
                attribute("agile", 10), attribute("luck", 10), attribute("reaction", 10));
    }

    private static OccupationAttribute attribute(String typeId, int initLv) {
        OccupationAttribute attribute = new OccupationAttribute();
        attribute.type_id = typeId;
        attribute.init_lv = initLv;
        attribute.max_lv = 99;
        attribute.pointsRewards = createPointsRewards(typeId);
        return attribute;
    }

    private static List<PointsReward> createPointsRewards(String typeId) {
        return switch (typeId) {
            case "hp" -> List.of(
                    reward("minecraft", "max_health", 2, 11, -1, "addition", 1),
                    reward("lhmiracleroad", "healing", 0.02, 20, -0.5, "multiply_base", 0.005)
            );
            case "focus" -> List.of(
                    reward("irons_spellbooks", "max_mana", 20, 0, 0, "addition", 0),
                    reward("ars_nouveau", "ars_nouveau.perk.max_mana", 20, 0, 0, "addition", 0),
                    reward("irons_spellbooks", "mana_regen", 0.015, 0, 0, "multiply_base", 0),
                    reward("ars_nouveau", "ars_nouveau.perk.mana_regen", 0.1, 0, 0, "addition", 0),
                    reward("irons_spellbooks", "cooldown_reduction", 0.01, 20, -0.005, "multiply_base", 0.005)
            );
            case "power" -> List.of(
                    reward("minecraft", "attack_damage", 0.02, 20, -0.005, "multiply_base", 0.005),
                    reward("lhmiracleroad", "ranged_damage", 0.03, 20, -0.01, "multiply_base", 0.01),
                    reward("lhmiracleroad", "mining_speed", 0.03, 0, 0, "multiply_base", 0)
            );
            case "sturdy" -> List.of(
                    reward("lhmiracleroad", "burden", 2, 20, -1, "addition", 1),
                    reward("lhmiracleroad", "hunger", 0.01, 0, 0, "multiply_base", 0),
                    reward("minecraft", "armor", 0.5, 20, -0.25, "addition", 0.25),
                    reward("minecraft", "armor_toughness", 0.25, 0, 0, "addition", 0),
                    reward("epicfight", "staminar", 1, 0, 0, "addition", 0)
            );
            case "brain" -> List.of(
                    reward("irons_spellbooks", "spell_power", 0.03, 25, -0.005, "multiply_base", 0.005),
                    reward("lhmiracleroad", "magic_damage_addition", 0.03, 25, -0.005, "multiply_base", 0.005),
                    reward("ars_nouveau", "ars_nouveau.perk.spell_damage", 1, 20, -0.25, "addition", 0.25)
            );
            case "agile" -> List.of(
                    reward("minecraft", "attack_damage", 0.2, 15, -0.05, "addition", 0.05),
                    reward("minecraft", "attack_speed", 0.015, 25, -0.005, "multiply_base", 0.0025),
                    reward("minecraft", "movement_speed", 0.003, 30, -0.0005, "multiply_base", 0.001),
                    reward("irons_spellbooks", "cast_time_reduction", 0.01, 30, -0.005, "multiply_base", 0.005)
            );
            case "luck" -> List.of(
                    reward("minecraft", "luck", 0.1, 0, 0, "addition", 0),
                    reward("lhmiracleroad", "critical_hit_rate", 2, 8, -0.5, "addition", 1),
                    reward("lhmiracleroad", "critical_hit_damage", 0.015, 20, -0.005, "addition", 0.005)
            );
            case "reaction" -> List.of(
                    reward("lhmiracleroad", "abnormal_damage", 0.01, 30, -0.0025, "multiply_base", 0.0025),
                    reward("lhmiracleroad", "abnormal_buildup", 0.02, 30, -0.0025, "multiply_base", 0.005)
            );
            default -> Collections.emptyList();
        };
    }

    private static PointsReward reward(
            String mod_id,
            String attribute_id,
            double value,
            int lv_promote,
            double lv_promote_value,
            String operation,
            double min
    ) {
        PointsReward reward = new PointsReward();
        reward.mod_id = mod_id;
        reward.attribute_id = attribute_id;
        reward.value = value;
        reward.lv_promote = lv_promote;
        reward.lv_promote_value = lv_promote_value;
        reward.operation = operation;
        reward.min = min;
        return reward;
    }

    public static Occupation getOccupation(String id) {
        return OCCUPATION_BY_ID.get(id);
    }

    public static List<String> getAttributeTypeIds() {
        return OCCUPATION_BY_ID.values().stream()
                .map(Occupation::getOccupationAttributes)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(OccupationAttribute::getType_id)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    public static List<Occupation> getOccupations() {
        return OCCUPATION_BY_ID.values().stream()
                .sorted(Comparator.comparing(Occupation::getId))
                .toList();
    }
}
