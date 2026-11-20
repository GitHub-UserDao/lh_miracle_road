package dev.lhkongyu.lhmiracleroad.config.occupation;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileAlreadyExistsException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class OccupationConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type OCCUPATION_LIST_TYPE = new TypeToken<List<Occupation>>(){}.getType();
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve(LHMiracleRoad.MODID).resolve("Occupation");

    private static final Map<String, Occupation> OCCUPATION_BY_ID = new HashMap<>();
    private static JsonObject outcastTemplate;

    public static void load() {
        try {
            Files.createDirectories(PATH);

            List<Path> files = findJsonFiles();
            if (files.isEmpty()) {
                for (Occupation occupation : createDefaults()) {
                    if (occupation == null || occupation.id == null || occupation.id.isBlank()) {
                        continue;
                    }
                    writeFile(PATH.resolve(occupation.id + ".json"), occupation);
                }
                files = findJsonFiles();
            }

            OCCUPATION_BY_ID.clear();
            outcastTemplate = null;

            for (Path file : files) {
                for (Occupation occupation : readFile(file)) {
                    if (occupation == null || occupation.id == null || occupation.id.isBlank()) {
                        continue;
                    }
                    if ("outcast".equals(occupation.id)) outcastTemplate = GSON.toJsonTree(occupation).getAsJsonObject();
                    filterUnavailableRewards(occupation);
                    OCCUPATION_BY_ID.put(occupation.id, occupation);
                }
            }
        } catch (Exception e) {
            LHMiracleRoad.LOGGER.error("Failed load occupation config", e);
        }
    }

    private static void filterUnavailableRewards(Occupation occupation) {
        if (occupation.occupationAttributes == null) return;

        ModList mods = ModList.get();
        occupation.occupationAttributes.removeIf(attribute -> {
            if (attribute == null || attribute.pointsRewards == null) return true;

            attribute.pointsRewards.removeIf(reward -> {
                if (reward == null) return true;
                String modId = reward.getMod_id();
                return modId != null && !modId.isBlank() && !mods.isLoaded(modId);
            });
            return attribute.pointsRewards.isEmpty();
        });
    }

    private static List<Path> findJsonFiles() throws IOException {
        try (Stream<Path> paths = Files.walk(PATH)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        }
    }

    private static List<Occupation> readFile(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonElement element = GSON.fromJson(reader, JsonElement.class);
            if (element == null || element.isJsonNull()) {
                return Collections.emptyList();
            }
            if (element.isJsonArray()) {
                List<Occupation> occupations = GSON.fromJson(element, OCCUPATION_LIST_TYPE);
                return occupations == null ? Collections.emptyList() : occupations;
            }
            if (element.isJsonObject()) {
                Occupation occupation = GSON.fromJson(element, Occupation.class);
                return occupation == null ? Collections.emptyList() : List.of(occupation);
            }
            throw new JsonParseException("Occupation config must be a json object or array: " + file);
        }
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
                    reward("minecraft", "max_health", 1, 20, -0.5, "addition", 0.25),
                    reward("lhmiracleroad", "healing", 0.01, 35, -0.5, "multiply_base", 0.005)
            );
            case "focus" -> List.of(
                    reward("irons_spellbooks", "max_mana", 10, 0, 0, "addition", 0),
                    reward("ars_nouveau", "ars_nouveau.perk.max_mana", 10, 0, 0, "addition", 0),
                    reward("irons_spellbooks", "mana_regen", 0.01, 0, 0, "multiply_base", 0),
                    reward("ars_nouveau", "ars_nouveau.perk.mana_regen", 0.1, 0, 0, "addition", 0),
                    reward("irons_spellbooks", "cooldown_reduction", 0.004, 20, -0.001, "multiply_base", 0.001)
            );
            case "power" -> List.of(
                    reward("minecraft", "attack_damage", 0.01, 20, -0.0025, "multiply_base", 0.005),
                    reward("lhmiracleroad", "ranged_damage", 0.02, 20, -0.005, "multiply_base", 0.005),
                    reward("lhmiracleroad", "mining_speed", 0.02, 0, 0, "multiply_base", 0)
            );
            case "sturdy" -> List.of(
                    reward("lhmiracleroad", "burden", 2, 20, -1, "addition", 1),
                    reward("lhmiracleroad", "hunger", 0.01, 0, 0, "multiply_base", 0),
                    reward("minecraft", "armor", 0.25, 20, -0.05, "addition", 0.1),
                    reward("minecraft", "armor_toughness", 0.2, 20, -0.05, "addition", 0.1),
                    reward("epicfight", "staminar", 0.5, 0, 0, "addition", 0)
            );
            case "brain" -> List.of(
                    reward("irons_spellbooks", "blood_spell_power", 0.02, 30, -0.0025, "multiply_base", 0.001),
                    reward("irons_spellbooks", "ender_spell_power", 0.01, 20, -0.0025, "multiply_base", 0.01),
                    reward("irons_spellbooks", "fire_spell_power", 0.02, 30, -0.0025, "multiply_base", 0.01),
                    reward("irons_spellbooks", "ice_spell_power", 0.02, 30, -0.0025, "multiply_base", 0.01),
                    reward("irons_spellbooks", "lightning_spell_power", 0.02, 30, -0.0025, "multiply_base", 0.01),
                    reward("irons_spellbooks", "evocation_spell_power", 0.02, 30, -0.0025, "multiply_base", 0.01),
                    reward("irons_spellbooks", "nature_spell_power", 0.01, 20, -0.0025, "multiply_base", 0.0025),
                    reward("irons_spellbooks", "holy_spell_power", 0.01, 20, -0.0025, "multiply_base", 0.0025),
                    reward("irons_spellbooks", "eldritch_spell_power", 0.02, 30, -0.0025, "multiply_base", 0.02),
                    reward("traveloptics", "aqua_spell_power", 0.02, 30, -0.0025, "multiply_base", 0.01),
                    reward("cataclysm_spellbooks", "technomancy_spell_power", 0.02, 30, -0.0025, "multiply_base", 0.01),
                    reward("cataclysm_spellbooks", "abyssal_spell_power", 0.02, 30, -0.0025, "multiply_base", 0.01),
                    reward("lhmiracleroad", "magic_damage_addition", 0.02, 15, -0.0025, "multiply_base", 0.01),
                    reward("ars_nouveau", "ars_nouveau.perk.spell_damage", 0.5, 30, -0.1, "addition", 0.1)
            );
            case "agile" -> List.of(
                    reward("minecraft", "attack_damage", 0.1, 10, -0.025, "addition", 0.025),
                    reward("minecraft", "attack_speed", 0.01, 30, -0.005, "multiply_base", 0.0025),
                    reward("minecraft", "movement_speed", 0.003, 30, -0.0005, "multiply_base", 0.001),
                    reward("irons_spellbooks", "cast_time_reduction", 0.005, 20, -0.0001, "multiply_base", 0.001)
            );
            case "luck" -> List.of(
                    reward("minecraft", "luck", 0.1, 0, 0, "addition", 0),
                    reward("lhmiracleroad", "critical_hit_rate", 2, 8, -0.5, "addition", 1)
            );
            case "reaction" -> List.of(
                    reward("lhmiracleroad", "abnormal_damage", 0.005, 35, -0.0015, "multiply_base", 0.0015),
                    reward("lhmiracleroad", "abnormal_buildup", 0.015, 35, -0.0025, "multiply_base", 0.005)
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
