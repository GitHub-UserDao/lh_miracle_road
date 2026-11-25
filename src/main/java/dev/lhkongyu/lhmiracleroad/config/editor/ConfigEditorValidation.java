package dev.lhkongyu.lhmiracleroad.config.editor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

final class ConfigEditorValidation {
    static final List<String> WEAPON_TYPES = List.of("melee", "ranged", "magic_staff");
    static final List<String> TROPHY_TYPES = List.of("common", "exquisite", "rare", "epic", "legend");
    static final Map<String, List<String>> STRENGTHEN_FIELDS = Map.of(
            "weapon", List.of("attack", "attack_speed"),
            "ranged_weapon", List.of("ranged_attack"),
            "magic_staffs", List.of("magic_damage"),
            "tool", List.of("mining_speed"),
            "armor", List.of("armor", "armor_toughness"));
    private static final Set<String> OPERATIONS = Set.of("addition", "multiply_base", "multiply_total");

    private ConfigEditorValidation() {}

    record Rules(Predicate<String> knownItem, Predicate<String> knownAttribute,
                 Set<String> occupationAttributes, Predicate<String> validNbt) {
        Rules(Predicate<String> knownItem, Predicate<String> knownAttribute, Set<String> occupationAttributes) {
            this(knownItem, knownAttribute, occupationAttributes, text -> true);
        }
    }

    static void validate(ConfigSection section, JsonObject original, JsonObject edited, Rules rules) {
        switch (section) {
            case OCCUPATION -> occupations(original, edited, rules);
            case STRENGTHEN -> strengthen(original, edited);
            case WEAPON_TYPES -> weaponTypes(original, edited, rules);
            case EQUIPMENT -> equipment(original, edited, rules);
            case TROPHY -> trophies(original, edited, rules);
            case COMMON -> CommonConfigEditor.validate(edited);
        }
    }

    private static void occupations(JsonObject original, JsonObject edited, Rules rules) {
        require(edited.size() > 0, "At least one occupation is required");
        for (var entry : edited.entrySet()) {
            String id = entry.getKey();
            JsonElement previous = original.get(id);
            if (entry.getValue().equals(previous)) continue;
            identifier(id, "Occupation ID");
            JsonObject job = object(entry.getValue(), id);
            require(id.equals(text(job, "id")), "Occupation ID differs from entry key: " + id);
            require(!text(job, "name").isBlank(), id + ": empty name");
            optionalText(job, "description");
            JsonArray attributes = array(job, "occupationAttributes");
            Set<String> types = new HashSet<>();
            for (JsonElement element : attributes) {
                JsonObject attribute = object(element, id + " attribute");
                String type = text(attribute, "type_id");
                identifier(type, "Occupation attribute ID");
                require(types.add(type), id + ": duplicate attribute " + type);
                if (contains(previous, "occupationAttributes", attribute)) continue;
                optionalText(attribute, "name");
                int maximum = integer(attribute, "max_lv", 0, 100000);
                integer(attribute, "init_lv", 0, maximum);
                JsonArray rewards = array(attribute, "pointsRewards");
                Set<String> targets = new HashSet<>();
                for (JsonElement rewardElement : rewards) {
                    JsonObject reward = object(rewardElement, type + " reward");
                    String attributeId = text(reward, "attribute_id");
                    String namespace = optionalText(reward, "mod_id");
                    String target = attributeId.contains(":") ? attributeId :
                            (namespace.isBlank() ? "minecraft" : namespace) + ":" + attributeId;
                    require(targets.add(target), type + ": duplicate reward " + target);
                    if (contains(previous, "occupationAttributes", "pointsRewards", reward)) continue;
                    if (!rules.knownAttribute().test(target)) {
                        boolean knownLegacyReward = original.entrySet().stream().anyMatch(source ->
                                contains(source.getValue(), "occupationAttributes", "pointsRewards", reward));
                        require(knownLegacyReward || hasLegacyRewardTarget(previous, type, reward),
                                "Unknown player attribute: " + target);
                    }
                    operation(reward);
                    number(reward, "value", -1000000, 1000000);
                    number(reward, "min", -1000000, 1000000);
                    number(reward, "lv_promote_value", -1000000, 1000000);
                    integer(reward, "lv_promote", 0, 100000);
                }
            }
        }
    }

    private static boolean hasLegacyRewardTarget(JsonElement occupation, String typeId, JsonObject reward) {
        if (!(occupation instanceof JsonObject job) || !(job.get("occupationAttributes") instanceof JsonArray types)) return false;
        for (JsonElement element : types) {
            if (!(element instanceof JsonObject type) || !typeId.equals(optionalText(type, "type_id"))
                    || !(type.get("pointsRewards") instanceof JsonArray rewards)) continue;
            for (JsonElement old : rewards) {
                if (old instanceof JsonObject candidate
                        && java.util.Objects.equals(candidate.get("mod_id"), reward.get("mod_id"))
                        && java.util.Objects.equals(candidate.get("attribute_id"), reward.get("attribute_id"))) return true;
            }
        }
        return false;
    }

    private static void strengthen(JsonObject original, JsonObject edited) {
        require(edited.keySet().equals(STRENGTHEN_FIELDS.keySet()), "Missing or unknown strengthen category");
        STRENGTHEN_FIELDS.forEach((key, fields) -> {
            JsonArray levels = array(edited, key);
            if (levels.equals(original.get(key))) return;
            require(levels.size() == 10, key + ": expected exactly levels +1 to +10");
            Set<Integer> seen = new HashSet<>();
            for (JsonElement entry : levels) {
                JsonObject level = object(entry, key + " level");
                require(seen.add(integer(level, "lv", 1, 10)), key + ": duplicate level");
                number(level, "durability_magnification", 0.000001, 1000);
                for (String field : fields) number(level, field, -1000000, 1000000);
            }
        });
    }

    private static void weaponTypes(JsonObject original, JsonObject edited, Rules rules) {
        for (String type : WEAPON_TYPES) {
            JsonArray ids = array(edited, type);
            if (ids.equals(original.get(type))) continue;
            Set<String> seen = new HashSet<>();
            for (JsonElement entry : ids) {
                String id = text(entry, type);
                require(seen.add(id), type + ": duplicate item " + id);
                require(contains(original, type, entry) || rules.knownItem().test(id), "Unknown item: " + id);
            }
        }
    }

    private static void equipment(JsonObject original, JsonObject edited, Rules rules) {
        JsonArray presets = array(edited, "presets");
        Set<String> presetIds = new HashSet<>();
        for (JsonElement element : presets) {
            JsonObject preset = object(element, "Penalty preset");
            String id = text(preset, "id");
            identifier(id, "Penalty preset ID");
            require(presetIds.add(id), "Duplicate penalty preset: " + id);
            if (contains(original, "presets", preset)) continue;
            if (preset.has("need_points")) integer(preset, "need_points", 0, 100000);
            JsonArray effects = array(preset, "punishments");
            Set<String> attributes = new HashSet<>();
            for (JsonElement effectElement : effects) {
                JsonObject effect = object(effectElement, id + " penalty");
                String target = text(effect, "attribute");
                require(attributes.add(target), id + ": duplicate penalty for " + target);
                if (contains(original, "presets", "punishments", effect)) continue;
                require(rules.knownAttribute().test(target), "Unknown player attribute: " + target);
                operation(effect);
                number(effect, "value", -1000000, 1000000);
            }
        }
        JsonObject previousItems = original.has("items") ? object(original.get("items"), "Items") : new JsonObject();
        JsonObject items = object(edited.get("items"), "Items");
        for (var entry : items.entrySet()) {
            String id = entry.getKey();
            JsonObject profile = object(entry.getValue(), id);
            JsonElement previous = previousItems.get(id);
            boolean changed = !profile.equals(previous);
            if (changed) {
                require(previous != null || rules.knownItem().test(id), "Unknown equipment: " + id);
                integer(profile, "heavy", 0, 1000000);
            }
            if (!profile.has("attribute_need")) continue;
            for (JsonElement needElement : array(profile, "attribute_need")) {
                JsonObject need = object(needElement, id + " requirement");
                String type = text(need, "attribute_id");
                String preset = optionalText(need, "punishment_id");
                if (preset.isBlank()) preset = "default";
                if (contains(previous, "attribute_need", need)) {
                    require(presetIds.contains(preset) || !hasPreset(original, preset), id + ": still uses " + preset);
                    continue;
                }
                require(rules.occupationAttributes().contains(type), id + ": unknown occupation attribute " + type);
                require(presetIds.contains(preset), id + ": unknown penalty preset " + preset);
                integer(need, "need_points", 0, 100000);
            }
        }
    }

    private static boolean hasPreset(JsonObject document, String id) {
        if (!(document.get("presets") instanceof JsonArray entries)) return false;
        for (JsonElement entry : entries) {
            if (entry instanceof JsonObject object && id.equals(optionalText(object, "id"))) return true;
        }
        return false;
    }

    private static void trophies(JsonObject original, JsonObject edited, Rules rules) {
        require(edited.keySet().equals(Set.copyOf(TROPHY_TYPES)), "Missing or unknown trophy category");
        for (String type : TROPHY_TYPES) {
            JsonArray groups = array(edited, type);
            if (groups.equals(original.get(type))) continue;
            long groupWeight = 0;
            for (JsonElement element : groups) {
                JsonObject group = object(element, type + " group");
                require(!text(group, "name").isBlank(), type + ": empty group name");
                int weight = integer(group, "weight", 0, 1000000);
                groupWeight += weight;
                optionalNumber(group, "luckWeight", 0, 10000);
                int maximum = integer(group, "roll_count_max", 0, 256);
                integer(group, "roll_count_min", 0, maximum);
                if (group.has("allowRepeat")) {
                    require(group.get("allowRepeat").isJsonPrimitive() &&
                            group.getAsJsonPrimitive("allowRepeat").isBoolean(), "allowRepeat must be true or false");
                }
                JsonArray items = array(group, "entries");
                long itemWeight = 0;
                for (JsonElement reward : items) {
                    JsonObject item = object(reward, type + " reward");
                    String id = text(item, "item");
                    itemWeight += integer(item, "weight", 0, 1000000);
                    if (contains(original, type, "entries", item)) continue;
                    require(rules.knownItem().test(id), "Unknown trophy item: " + id);
                    require(rules.validNbt().test(optionalText(item, "nbt")), "Invalid item SNBT: " + id);
                    optionalNumber(item, "luckWeight", 0, 10000);
                    int max = integer(item, "max", 1, 4096);
                    integer(item, "min", 1, max);
                }
                require(itemWeight <= 100000000, type + ": total item weight is too large");
            }
            require(groupWeight <= 100000000, type + ": total group weight is too large");
        }
    }

    private static boolean contains(JsonElement parent, String field, JsonElement needle) {
        if (!(parent instanceof JsonObject object) || !(object.get(field) instanceof JsonArray entries)) return false;
        for (JsonElement entry : entries) if (entry.equals(needle)) return true;
        return false;
    }

    private static boolean contains(JsonElement parent, String field, String child, JsonElement needle) {
        if (!(parent instanceof JsonObject object) || !(object.get(field) instanceof JsonArray entries)) return false;
        for (JsonElement entry : entries) if (contains(entry, child, needle)) return true;
        return false;
    }

    private static void identifier(String value, String label) {
        require(value.matches("[a-zA-Z0-9_-][a-zA-Z0-9_.-]{0,127}"), label + ": " + value);
    }

    private static void operation(JsonObject entry) {
        require(OPERATIONS.contains(text(entry, "operation")), "Unknown attribute operation");
    }

    private static JsonObject object(JsonElement entry, String field) {
        if (entry instanceof JsonObject object) return object;
        throw new IllegalArgumentException(field + " must be an object");
    }

    private static JsonArray array(JsonObject object, String field) {
        if (object.get(field) instanceof JsonArray entries) return entries;
        throw new IllegalArgumentException(field + " must be a list");
    }

    private static String text(JsonObject record, String field) {
        return text(record.get(field), field);
    }

    private static String text(JsonElement element, String field) {
        require(element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString(),
                field + " must be text");
        String value = element.getAsString();
        require(value.length() <= 16384, field + " is too long");
        return value;
    }

    private static String optionalText(JsonObject record, String field) {
        return record.has(field) && !record.get(field).isJsonNull() ? text(record, field) : "";
    }

    private static int integer(JsonObject record, String field, int minimum, int maximum) {
        double value = number(record, field, minimum, maximum);
        require(value == Math.rint(value), field + " must be an integer");
        return (int) value;
    }

    private static double number(JsonObject record, String field, double minimum, double maximum) {
        JsonElement element = record.get(field);
        require(element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber(),
                field + " must be a number");
        double value = element.getAsDouble();
        require(Double.isFinite(value) && value >= minimum && value <= maximum,
                field + " must be between " + minimum + " and " + maximum);
        return value;
    }

    private static void optionalNumber(JsonObject record, String field, double minimum, double maximum) {
        if (record.has(field)) number(record, field, minimum, maximum);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
}
