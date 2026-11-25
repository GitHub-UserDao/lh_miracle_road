package dev.lhkongyu.lhmiracleroad.client.screen.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.lhkongyu.lhmiracleroad.config.editor.ConfigSection;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

final class ConfigEditorPages {
    private static final Gson GSON = new Gson();
    private static final List<String> OPERATIONS = List.of("addition", "multiply_base", "multiply_total");
    private static final List<String> OCCUPATION_TYPES = List.of("hp", "focus", "power", "sturdy", "brain", "agile", "luck", "reaction");
    private static final Set<String> WEAPON_LISTS = Set.of("melee", "ranged", "magic_staff");
    private static final String MOB_LOOT = "mob loot";
    private static final Map<String, String> MOB_LIST_DEFAULTS = Map.of(
            "ordinary_mob", "zombie",
            "elite_mob", "evoker",
            "special_elite_mob", "warden",
            "liege_mob", "wither");

    private ConfigEditorPages() {}

    enum Kind { DEFAULT, ITEM, RESOURCE, ENUM, READ_ONLY }

    record Field(Component label, Kind kind, List<String> choices) {}

    record PreviewInput(String id, Component label, String initialValue) {}

    static Field field(ConfigSection section, List<String> path, JsonElement value) {
        String key = last(path);
        if (section == ConfigSection.COMMON && path.size() == 3 && isMobList(path.subList(0, 2))) {
            return new Field(local("实体路径", "Entity path"), Kind.RESOURCE, entityPaths());
        }
        if (section == ConfigSection.STRENGTHEN && "lv".equals(key)) {
            return new Field(label(key), Kind.READ_ONLY, List.of());
        }
        if ((section == ConfigSection.WEAPON_TYPES && path.size() == 2) || (section == ConfigSection.TROPHY && "item".equals(key))) {
            return new Field(label("item"), Kind.ITEM, List.of());
        }
        if ("operation".equals(key)) return new Field(label(key), Kind.ENUM, OPERATIONS);
        if ("type_id".equals(key) || "attribute_id".equals(key) && section == ConfigSection.EQUIPMENT) {
            return new Field(label("type_id"), Kind.RESOURCE, occupationTypes());
        }
        if ("attribute_id".equals(key) || "attribute".equals(key)) {
            return new Field(label("attribute"), Kind.RESOURCE,
                    ForgeRegistries.ATTRIBUTES.getKeys().stream().sorted().map(ResourceLocation::toString).toList());
        }
        if ("punishment_id".equals(key)) return new Field(label(key), Kind.RESOURCE, List.of());
        if (section == ConfigSection.TROPHY && ("min".equals(key) || "max".equals(key))) {
            Component quantity = "min".equals(key) ? local("最少物品数量", "Minimum quantity") : local("最多物品数量", "Maximum quantity");
            return new Field(quantity, Kind.DEFAULT, List.of());
        }
        return new Field(label(key), Kind.DEFAULT, List.of());
    }

    private static List<String> occupationTypes() {
        Set<String> available = new TreeSet<>(OCCUPATION_TYPES);
        ClientData.OCCUPATION_CATALOG.values().forEach(occupation -> {
            if (occupation.getOccupationAttributes() != null) {
                occupation.getOccupationAttributes().forEach(attribute -> {
                    if (attribute != null && attribute.getType_id() != null) available.add(attribute.getType_id());
                });
            }
        });
        return List.copyOf(available);
    }

    static boolean canAddEntry(ConfigSection section, List<String> path) {
        String key = last(path);
        return switch (section) {
            case OCCUPATION, STRENGTHEN -> false;
            case WEAPON_TYPES -> path.size() == 1 && WEAPON_LISTS.contains(key);
            case EQUIPMENT -> Set.of("items", "presets", "attribute_need", "punishments").contains(key);
            case TROPHY -> path.size() == 1 || "entries".equals(key);
            case COMMON -> isMobList(path);
        };
    }

    static boolean canRemoveField(ConfigSection section, List<String> parentPath, String key) {
        if (optionalFields(section, parentPath).containsKey(key)) return true;
        if (section == ConfigSection.EQUIPMENT && parentPath.equals(List.of("items"))) return true;
        return canAddEntry(section, parentPath) && key.chars().allMatch(Character::isDigit);
    }

    static Map<String, JsonElement> optionalFields(ConfigSection section, List<String> objectPath) {
        return switch (section) {
            case OCCUPATION, STRENGTHEN, WEAPON_TYPES, COMMON -> Map.of();
            case EQUIPMENT -> {
                if (objectPath.size() == 2 && "items".equals(objectPath.get(0))) {
                    yield Map.of("attribute_need", new JsonArray());
                }
                if (objectPath.size() == 4 && "attribute_need".equals(objectPath.get(2))) {
                    yield Map.of("punishment_id", new JsonPrimitive("default"));
                }
                yield Map.of();
            }
            case TROPHY -> {
                if (objectPath.size() == 2) {
                    yield Map.of("luckWeight", new JsonPrimitive(0.0), "allowRepeat", new JsonPrimitive(true));
                }
                if (objectPath.size() == 4 && "entries".equals(objectPath.get(2))) {
                    yield Map.of("luckWeight", new JsonPrimitive(0.0), "nbt", new JsonPrimitive(""));
                }
                yield Map.of();
            }
        };
    }

    static JsonElement newElement(ConfigSection section, List<String> arrayPath, JsonArray array) {
        if (!canAddEntry(section, arrayPath)) return null;
        String key = last(arrayPath);
        return switch (section) {
            case WEAPON_TYPES -> new JsonPrimitive(switch (key) {
                case "ranged" -> "minecraft:bow";
                case "magic_staff" -> "minecraft:stick";
                default -> "minecraft:iron_sword";
            });
            case EQUIPMENT -> switch (key) {
                case "presets" -> object("id", nextName(array, "id", "preset_"), "punishments", new JsonArray());
                case "attribute_need" -> object("attribute_id", "power", "need_points", 10, "punishment_id", "default");
                case "punishments" -> object("attribute", "attack_damage", "value", -0.7, "operation", "multiply_total");
                default -> null;
            };
            case TROPHY -> "entries".equals(key)
                    ? object("item", "minecraft:iron_ingot", "weight", 1, "min", 1, "max", 1, "nbt", "", "luckWeight", 0.0)
                    : object("name", nextName(array, "name", "group_"), "weight", 1, "luckWeight", 0.0,
                    "roll_count_min", 1, "roll_count_max", 1, "allowRepeat", true, "entries", new JsonArray());
            case OCCUPATION, STRENGTHEN -> null;
            case COMMON -> newMobEntry(key, array);
        };
    }

    private static boolean isMobList(List<String> path) {
        return path.size() == 2 && MOB_LOOT.equals(path.get(0)) && MOB_LIST_DEFAULTS.containsKey(path.get(1));
    }

    private static List<String> entityPaths() {
        return ForgeRegistries.ENTITY_TYPES.getKeys().stream().map(ResourceLocation::getPath).distinct().sorted().toList();
    }

    private static JsonPrimitive newMobEntry(String key, JsonArray array) {
        JsonPrimitive example = new JsonPrimitive(MOB_LIST_DEFAULTS.get(key));
        if (!array.contains(example)) return example;
        for (String path : entityPaths()) {
            JsonPrimitive value = new JsonPrimitive(path);
            if (!array.contains(value)) return value;
        }
        return new JsonPrimitive("");
    }

    static JsonElement newObjectEntry(ConfigSection section, List<String> mapPath, String key) {
        return section == ConfigSection.EQUIPMENT && mapPath.equals(List.of("items")) && ResourceLocation.tryParse(key) != null
                ? object("heavy", 0, "attribute_need", new JsonArray()) : null;
    }

    static JsonObject newOccupation(String id, String name) {
        if (!id.matches("[a-zA-Z0-9_-][a-zA-Z0-9_.-]{0,127}")) return null;

        var outcast = ClientData.OCCUPATION_CATALOG.get("outcast");
        JsonArray attributes;
        if (outcast != null && outcast.getOccupationAttributes() != null) {
            attributes = GSON.toJsonTree(outcast.getOccupationAttributes()).getAsJsonArray();
        } else {
            attributes = new JsonArray();
            for (String type : OCCUPATION_TYPES) attributes.add(occupationAttribute(type));
        }
        return object("id", id, "name", name, "occupationAttributes", attributes);
    }

    static JsonObject newOccupationAttribute(JsonArray existing) {
        return occupationAttribute(firstUnusedType(existing));
    }

    private static JsonObject occupationAttribute(String type) {
        return object("type_id", type, "init_lv", 10, "max_lv", 100, "pointsRewards", new JsonArray());
    }

    static JsonObject newOccupationReward(ResourceLocation id) {
        return object("mod_id", id.getNamespace(), "attribute_id", id.getPath(), "value", 0.0,
                "lv_promote", 0, "lv_promote_value", 0.0, "operation", "addition", "min", 0.0);
    }

    private static String firstUnusedType(JsonArray array) {
        for (String type : occupationTypes()) {
            if (!containsText(array, "type_id", type)) return type;
        }
        return nextName(array, "type_id", "attribute_");
    }

    private static String nextName(JsonArray array, String key, String prefix) {
        int index = 1;
        while (containsText(array, key, prefix + index)) index++;
        return prefix + index;
    }

    private static boolean containsText(JsonArray values, String key, String target) {
        for (JsonElement value : values) {
            if (value instanceof JsonObject object && target.equals(ConfigEditorPagePreview.string(object, key, ""))) return true;
        }
        return false;
    }

    private static JsonObject object(Object... fields) {
        JsonObject result = new JsonObject();
        for (int index = 0; index < fields.length; index += 2) {
            String key = (String) fields[index];
            Object value = fields[index + 1];
            if (value instanceof JsonElement element) result.add(key, element);
            else if (value instanceof Number number) result.addProperty(key, number);
            else if (value instanceof Boolean flag) result.addProperty(key, flag);
            else result.addProperty(key, (String) value);
        }
        return result;
    }

    static Component displayName(ConfigSection section, List<String> path, JsonElement value) {
        String key = last(path);
        if (section == ConfigSection.EQUIPMENT && path.size() == 2 && "items".equals(path.get(0))) return itemLabel(key);
        if (section == ConfigSection.WEAPON_TYPES && path.size() == 2 && value.isJsonPrimitive()) return itemLabel(value.getAsString());
        if (!(value instanceof JsonObject object)) return null;
        if (section == ConfigSection.STRENGTHEN && path.size() == 2) {
            return Component.literal("+" + ConfigEditorPagePreview.string(object, "lv", key));
        }
        if (section == ConfigSection.TROPHY && path.size() == 4) {
            return itemLabel(ConfigEditorPagePreview.string(object, "item", ""));
        }
        for (String field : List.of("name", "type_id", "id", "attribute_id", "attribute")) {
            String name = ConfigEditorPagePreview.string(object, field, "");
            if (!name.isBlank()) return Component.literal(name);
        }
        return null;
    }

    static Component itemLabel(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        Item item = location == null ? null : ForgeRegistries.ITEMS.getValue(location);
        if (item == null || item == Items.AIR) return Component.literal(id);
        return Component.translatable(item.getDescriptionId()).append(" · ").append(id);
    }

    static List<PreviewInput> previewInputs(ConfigSection section) {
        return switch (section) {
            case STRENGTHEN -> List.of(input("durability", "+0 耐久（留空读取主手）", "+0 durability (blank reads held item)", ""));
            case WEAPON_TYPES -> List.of(input("item", "物品 ID（留空读取主手）", "Item ID (blank reads held item)", ""));
            case EQUIPMENT -> List.of(input("base", "基础等级 · 数字或 power=10,agile=12", "Base levels · number or power=10,agile=12", ""),
                    input("curio", "饰品加成 · 同上", "Curio bonuses · same format", ""));
            case TROPHY -> List.of(input("luck", "幸运（留空使用当前玩家）", "Luck (blank uses current player)", ""),
                    input("seed", "试抽随机种子", "Sample seed", "42"));
            case OCCUPATION, COMMON -> List.of();
        };
    }

    private static PreviewInput input(String id, String chinese, String english, String initialValue) {
        return new PreviewInput(id, local(chinese, english), initialValue);
    }

    static List<Component> preview(ConfigSection section, JsonObject document, List<String> path, Minecraft minecraft,
                                   Map<ConfigSection, JsonObject> documents, Map<String, String> inputs) {
        return ConfigEditorPagePreview.preview(section, document, path, minecraft, documents, inputs);
    }

    static Component label(String key) {
        return switch (key) {
            case "id" -> local("标识符", "ID");
            case "name" -> local("名称", "Name");
            case "type_id" -> local("职业属性 ID", "Occupation attribute ID");
            case "attribute_id", "attribute" -> local("目标游戏属性", "Target game attribute");
            case "operation" -> local("运算方式", "Modifier operation");
            case "value" -> local("数值", "Value");
            case "weapon", "melee" -> local("近战武器", "Melee weapons");
            case "ranged_weapon", "ranged" -> local("远程武器", "Ranged weapons");
            case "magic_staffs", "magic_staff" -> local("法杖", "Magic staffs");
            case "tool" -> local("工具", "Tools");
            case "armor" -> local("护甲", "Armor");
            case "lv" -> local("强化等级", "Upgrade level");
            case "durability_magnification" -> local("本级耐久倍率", "Durability multiplier");
            case "attack" -> local("攻击伤害", "Attack damage");
            case "attack_speed" -> local("攻击速度", "Attack speed");
            case "ranged_attack" -> local("远程伤害倍率加成", "Ranged damage bonus");
            case "magic_damage" -> local("魔法伤害倍率加成", "Magic damage bonus");
            case "mining_speed" -> local("挖掘速度倍率加成", "Mining speed bonus");
            case "armor_toughness" -> local("盔甲韧性", "Armor toughness");
            case "item" -> local("物品", "Item");
            case "items" -> local("装备配置", "Equipment profiles");
            case "heavy" -> local("重量", "Weight");
            case "attribute_need" -> local("属性需求（按顺序）", "Attribute requirements (ordered)");
            case "need_points" -> local("所需等级", "Required level");
            case "punishment_id" -> local("惩罚预设 ID", "Penalty preset ID");
            case "presets" -> local("共享惩罚预设", "Shared penalty presets");
            case "punishments" -> local("属性惩罚", "Attribute penalties");
            case "common" -> local("普通战利品袋", "Common trophy bag");
            case "exquisite" -> local("精致战利品袋", "Exquisite trophy bag");
            case "rare" -> local("稀有战利品袋", "Rare trophy bag");
            case "epic" -> local("史诗战利品袋", "Epic trophy bag");
            case "legend" -> local("传说战利品袋", "Legendary trophy bag");
            case "roll_count_min" -> local("最少抽取次数", "Minimum draws");
            case "roll_count_max" -> local("最多抽取次数", "Maximum draws");
            case "allowRepeat" -> local("同组允许重复物品", "Allow repeat items within group");
            case "weight" -> local("基础权重", "Base draw weight");
            case "luckWeight" -> local("幸运权重系数", "Luck weight coefficient");
            case "min" -> local("最小值", "Minimum");
            case "max" -> local("最大值", "Maximum");
            case "entries" -> local("组内奖励", "Group rewards");
            case "nbt" -> local("物品 NBT（SNBT 格式）", "Item NBT (SNBT format)");
            case "base" -> local("基础设置", "Base settings");
            case "items loot probability" -> local("宝箱掉落概率", "Chest loot probabilities");
            case "mob loot" -> local("生物掉落", "Mob loot");
            case "init_burden" -> local("初始负重上限", "Initial carrying capacity");
            case "level_base" -> local("职业成长等级基准", "Occupation growth baseline");
            case "enable_equipment_weight" -> local("装备重量", "Equipment weight");
            case "is_offhand_calculate_heavy" -> local("副手盾牌计入重量", "Count offhand shield weight");
            case "punishment_light" -> local("轻负重移速修正", "Light load speed modifier");
            case "punishment_normal" -> local("普通负重移速修正", "Normal load speed modifier");
            case "punishment_biased_weight" -> local("偏重负重移速修正", "Heavy load speed modifier");
            case "punishment_overweight" -> local("超重移速修正", "Overweight speed modifier");
            case "empirical_calculation_formula" -> local("升级消耗公式", "Level-up cost formula");
            case "experience_acquisition_formula" -> local("击杀灵魂获取公式", "Kill soul gain formula");
            case "soul_loss_count" -> local("死亡时灵魂保留比例", "Souls retained on death");
            case "is_skill_points_restrict" -> local("装备属性需求惩罚", "Equipment requirement penalties");
            case "attribute_max_level" -> local("默认属性等级上限", "Default attribute level cap");
            case "max_level" -> local("玩家总等级上限", "Player level cap");
            case "dark_soul" -> local("再次死亡清除旧灵魂残影", "Erase previous soul remnant on death");
            case "is_exp_sharing" -> local("附近玩家灵魂共享", "Share souls with nearby players");
            case "exp_sharing_percentage" -> local("灵魂共享比例", "Shared soul fraction");
            case "forget_water_probability" -> local("遗忘之水宝箱概率", "Forget Water chance");
            case "death_soul_probability" -> local("死神灵魂宝箱概率", "Death Soul chance");
            case "king_soul_probability" -> local("王的灵魂宝箱概率", "King Soul chance");
            case "large_block_soul" -> local("大块灵魂宝箱概率", "Large Block Soul chance");
            case "stray_large_block_soul" -> local("游魂大块灵魂宝箱概率", "Stray Large Block Soul chance");
            case "adventurer_large_block_soul" -> local("冒险者大块灵魂宝箱概率", "Adventurer Large Block Soul chance");
            case "unknown_soldier_soul" -> local("无名战士灵魂宝箱概率", "Unknown Soldier Soul chance");
            case "unknown_soldier_large_block_soul" -> local("无名战士大块灵魂宝箱概率", "Unknown Soldier Large Block Soul chance");
            case "exhausted_knight_soul" -> local("精疲力尽骑士灵魂宝箱概率", "Exhausted Knight Soul chance");
            case "ordinary_mob" -> local("普通生物名单", "Ordinary mob list");
            case "ordinary_loot_soon_elapse_soul_odds" -> local("普通生物 · 即将消逝的灵魂概率", "Ordinary mobs · Soon Elapse Soul chance");
            case "ordinary_loot_incomplete_soul_odds" -> local("普通生物 · 不完整灵魂概率", "Ordinary mobs · Incomplete Soul chance");
            case "elite_mob" -> local("精英生物名单", "Elite mob list");
            case "elite_loot_soul_odds" -> local("精英生物 · 灵魂概率", "Elite mobs · Soul chance");
            case "special_elite_mob" -> local("特殊精英生物名单", "Special elite mob list");
            case "special_elite_loot_soul_odds" -> local("特殊精英生物 · 灵魂概率", "Special elite mobs · Soul chance");
            case "liege_mob" -> local("领主生物名单", "Liege mob list");
            case "liege_loot_soul_odds" -> local("领主生物 · 灵魂概率", "Liege mobs · Soul chance");
            case "ender_dragon_forget_water_odds" -> local("末影龙 · 遗忘之水概率", "Ender Dragon · Forget Water chance");
            default -> Component.literal(key);
        };
    }

    private static String last(List<String> path) {
        return path.isEmpty() ? "" : path.get(path.size() - 1);
    }

    static Component local(String chinese, String english) {
        String language = Minecraft.getInstance().getLanguageManager().getSelected();
        return Component.literal(language.startsWith("zh") ? chinese : english);
    }
}
