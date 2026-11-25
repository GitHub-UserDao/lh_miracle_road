package dev.lhkongyu.lhmiracleroad.client.screen.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.editor.ConfigSection;
import dev.lhkongyu.lhmiracleroad.config.strengthen.WeaponTypeConfig;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import dev.lhkongyu.lhmiracleroad.items.trophy.TrophyData;
import dev.lhkongyu.lhmiracleroad.items.trophy.TrophyEntry;
import dev.lhkongyu.lhmiracleroad.tool.ItemPunishmentTool;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.registries.ForgeRegistries;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static dev.lhkongyu.lhmiracleroad.client.screen.config.ConfigEditorPages.itemLabel;
import static dev.lhkongyu.lhmiracleroad.client.screen.config.ConfigEditorPages.label;
import static dev.lhkongyu.lhmiracleroad.client.screen.config.ConfigEditorPages.local;

final class ConfigEditorPagePreview {
    private static final Gson GSON = new Gson();
    private static final Map<String, List<String>> STRENGTHEN_ATTRIBUTES = Map.of(
            "weapon", List.of("attack", "attack_speed"),
            "ranged_weapon", List.of("ranged_attack"),
            "magic_staffs", List.of("magic_damage"),
            "tool", List.of("mining_speed"),
            "armor", List.of("armor", "armor_toughness"));

    private ConfigEditorPagePreview() {}

    static List<Component> preview(ConfigSection section, JsonObject document, List<String> path, Minecraft minecraft,
                                   Map<ConfigSection, JsonObject> documents, Map<String, String> inputs) {
        ItemStack held = minecraft.player == null ? ItemStack.EMPTY : minecraft.player.getMainHandItem();
        return switch (section) {
            case OCCUPATION -> List.of();
            case STRENGTHEN -> strengthen(document, path, held, documents.get(ConfigSection.WEAPON_TYPES), inputs);
            case WEAPON_TYPES -> weapons(document, path, held, inputs);
            case EQUIPMENT -> equipment(document, path, held, minecraft, inputs);
            case TROPHY -> trophies(document, path, minecraft, inputs);
            case COMMON -> common();
        };
    }

    private static List<Component> common() {
        return List.of(
                local("文件 · config/lhmiracleroad-common.toml", "File · config/lhmiracleroad-common.toml"),
                local("装备重量和属性需求惩罚有 Forge 重启标记。通过配置台保存时，服务器会刷新在线玩家的相应状态。",
                        "Forge marks weight and requirement penalties for restart. Saving in this editor refreshes those rules for online players."),
                local("默认属性等级上限适用于新角色或未记录上限的角色，已有的正数上限会保留。",
                        "The default cap applies to new characters or those without a saved cap. Existing positive caps are preserved."),
                local("宝箱概率与生物掉落设置保存后，执行 /reload 或重启以重新加载掉落表。",
                        "After saving loot changes, run /reload or restart to reload loot tables."),
                local("生物名单按实体路径匹配，例如 zombie。", "Mob lists match entity paths such as zombie."),
                local("概率和比例使用 0 到 1，例如 0.25 代表 25%。", "Use 0 to 1 for probabilities and fractions. For example, 0.25 is 25%."));
    }

    private static List<Component> strengthen(JsonObject document, List<String> path, ItemStack held,
                                              JsonObject weaponTypes, Map<String, String> inputs) {
        if (path.isEmpty()) return List.of(local("选择强化类别查看 +1 到 +10 的加成。", "Select a category for the +1 through +10 bonuses."));
        String category = path.get(0);
        if (!STRENGTHEN_ATTRIBUTES.containsKey(category)) return List.of();
        JsonArray levels = array(document.get(category));
        JsonObject selected = path.size() >= 2 ? object(element(levels, path.get(1))) : new JsonObject();
        int selectedLevel = integer(selected, "lv", 0);
        Map<Integer, JsonObject> byLevel = new HashMap<>();
        for (JsonObject row : objects(levels)) byLevel.put(integer(row, "lv", 0), row);

        int baseDurability = held.isEmpty() ? 0 : new ItemStack(held.getItem()).getMaxDamage();
        int durability = integer(inputs.get("durability"), baseDurability, 0, Integer.MAX_VALUE);
        boolean customDurability = !inputs.getOrDefault("durability", "").isBlank();
        var classification = classify(held, weaponTypes);
        List<Component> lines = new ArrayList<>();
        lines.add(local("耐久计算从 +0 开始，每级乘完取整数。", "Durability starts at +0 and truncates after each multiplier."));
        lines.add(local("已写入物品的耐久会保留，直到该物品再次强化。", "Stored durability stays with the item until its next upgrade."));
        if (!customDurability && durability == 0) {
            lines.add(local("填入 +0 耐久或手持有耐久的物品。", "Enter +0 durability or hold a damageable item."));
        } else {
            lines.add(local("试算起始耐久 · ", "Starting durability · ").copy().append(Integer.toString(durability)));
        }
        if (!held.isEmpty() && !classification.durabilityCategory().isEmpty() && !category.equals(classification.durabilityCategory())) {
            lines.add(local("主手物品实际使用耐久表 · ", "Held item uses durability from · ").copy().append(label(classification.durabilityCategory())));
        }
        if ("magic_staffs".equals(category)) {
            lines.add(local("法术强度加成需要 Iron's Spells ’n Spellbooks 的属性。", "Spell power bonuses require the Iron's Spells ’n Spellbooks attribute."));
        }
        for (int level = 1; level <= 10; level++) {
            if (selectedLevel != 0 && selectedLevel != level) continue;
            JsonObject current = strengthenAt(byLevel, level);
            double multiplier = decimal(current, "durability_magnification", 1);
            int previous = ConfigEditorPageMath.maximumDurability(durability, 0, level - 1,
                    rank -> decimal(strengthenAt(byLevel, rank), "durability_magnification", 1));
            int totalDurability = ConfigEditorPageMath.maximumDurability(previous, level - 1, level, ignored -> multiplier);
            lines.add(Component.literal("+" + level + " · ").append(label("durability_magnification"))
                    .append(" ×" + number(multiplier) + " · " + previous + " → " + totalDurability));
            boolean percentCategory = "ranged_weapon".equals(category) || "magic_staffs".equals(category) || "tool".equals(category);
            for (String attribute : STRENGTHEN_ATTRIBUTES.get(category)) {
                double total = 0;
                for (int rank = level; rank >= 1; rank--) total += decimal(strengthenAt(byLevel, rank), attribute, 0);
                double currentBonus = decimal(current, attribute, 0);
                String operation = percentCategory ? "multiply_total" : "addition";
                double applied = "armor".equals(category) ? total : Math.max(total, 0);
                lines.add(Component.literal("  ").append(label(attribute)).append(" · ")
                        .append(local("本级 ", "This level ")).append(signedModifier(currentBonus, operation))
                        .append(local(" · 累计 ", " · Cumulative ")).append(signedModifier(total, operation))
                        .append(applied == total ? Component.empty() : local(" · 实际加成 0", " · Applied bonus 0")));
            }
        }
        return lines;
    }

    private static JsonObject strengthenAt(Map<Integer, JsonObject> byLevel, int level) {
        return byLevel.containsKey(level) ? byLevel.get(level) : object(byLevel.get(1));
    }

    private static List<Component> weapons(JsonObject document, List<String> path, ItemStack held, Map<String, String> inputs) {
        String requested = inputs.getOrDefault("item", "").trim();
        if (requested.isEmpty() && path.size() >= 2) {
            JsonElement selected = element(array(document.get(path.get(0))), path.get(1));
            if (selected != null && selected.isJsonPrimitive()) requested = selected.getAsString();
        }
        if (requested.isEmpty()) requested = id(held);
        List<Component> lines = new ArrayList<>();
        lines.add(local("物品类型取内置识别与配置条目的并集。", "Recognition combines built-in classes and configured entries."));
        lines.add(local("内置近战包括剑、斧、三叉戟和对应剑斧标签；远程包括投射武器。",
                "Built-in melee includes swords, axes, tridents, and sword/axe tags. Projectile weapons count as ranged."));
        lines.add(local("法杖使用配置名单。斧可同时获得近战与工具强化加成。",
                "Staffs use the configured list. Axes can receive both melee and tool bonuses."));
        lines.add(local("保存后更新识别。物品已有强化等级和质变记录会保留。",
                "Saving refreshes recognition. Items retain their stored upgrade and infusion data."));
        if (requested.isEmpty()) return lines;
        ItemStack stack = registeredStack(requested);
        if (stack.isEmpty()) {
            lines.add(local("未注册物品 · ", "Item is unavailable · ").copy().append(requested));
            return lines;
        }
        Classification result = classify(stack, document);
        lines.add(itemLabel(requested));
        lines.add(recognitionLine("melee", result.builtInMelee(), result.configuredMelee()));
        lines.add(recognitionLine("ranged", result.builtInRanged(), result.configuredRanged()));
        lines.add(recognitionLine("magic_staff", false, result.staff()));
        lines.add(recognitionLine("tool", result.tool(), false));
        lines.add(recognitionLine("armor", result.armor(), false));
        lines.add(local("强化资格 · ", "Eligible for strengthening · ").copy().append(yes(result.strengthen())));
        lines.add(local("宝石质变资格 · ", "Eligible for infusion · ").copy().append(yes(result.melee() || result.ranged())));
        if (!result.durabilityCategory().isEmpty()) {
            lines.add(local("新增强化等级的耐久使用 · ", "Next upgrade uses durability from · ").copy().append(label(result.durabilityCategory())));
        } else if (result.shield()) {
            lines.add(local("只有盾牌分类时，强化的耐久倍率为 1。", "A shield without another classification uses a durability multiplier of 1."));
        }
        return lines;
    }

    private static Component recognitionLine(String category, boolean builtIn, boolean configured) {
        return label(category).copy().append(" · ").append(yes(builtIn || configured)).append(" · ")
                .append(local("内置 ", "Built-in ")).append(yes(builtIn))
                .append(local(" · 配置 ", " · Configured ")).append(yes(configured));
    }

    private record Classification(boolean builtInMelee, boolean configuredMelee, boolean builtInRanged,
                                  boolean configuredRanged, boolean staff, boolean tool, boolean armor, boolean shield) {
        boolean melee() { return builtInMelee || configuredMelee; }
        boolean ranged() { return builtInRanged || configuredRanged; }
        boolean strengthen() { return melee() || ranged() || armor || tool || staff || shield; }

        String durabilityCategory() {
            if (melee()) return "weapon";
            if (ranged()) return "ranged_weapon";
            if (armor) return "armor";
            if (tool) return "tool";
            return staff ? "magic_staffs" : "";
        }
    }

    private static Classification classify(ItemStack stack, JsonObject document) {
        Item item = stack.getItem();
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        var saved = document == null ? WeaponTypeConfig.forSide(true) : null;
        boolean melee = document == null ? saved.melee().contains(key) : containsId(document, "melee", key);
        boolean ranged = document == null ? saved.ranged().contains(key) : containsId(document, "ranged", key);
        boolean staff = document == null ? saved.magicStaff().contains(key) : containsId(document, "magic_staff", key);
        return new Classification(item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem
                || stack.is(ItemTags.SWORDS) || stack.is(ItemTags.AXES), melee,
                item instanceof ProjectileWeaponItem, ranged, staff, LHMiracleRoadTool.itemIsTool(stack),
                LHMiracleRoadTool.itemIsArmors(stack), LHMiracleRoadTool.itemIsShield(stack));
    }

    private static boolean containsId(JsonObject document, String category, ResourceLocation key) {
        if (key == null) return false;
        for (JsonElement entry : array(document.get(category))) {
            if (entry.isJsonPrimitive() && key.equals(ResourceLocation.tryParse(entry.getAsString()))) return true;
        }
        return false;
    }

    private static List<Component> equipment(JsonObject document, List<String> path, ItemStack held, Minecraft minecraft,
                                             Map<String, String> inputs) {
        List<Component> lines = new ArrayList<>();
        var synced = ClientData.getEquipmentRules();
        if (synced != null) {
            lines.add(local("服务端启用重量 · ", "Server enables weight · ").copy().append(yes(synced.weightEnabled()))
                    .append(local(" · 启用需求惩罚 · ", " · Enables requirement penalties · "))
                    .append(yes(synced.attributePenaltiesEnabled())));
        }
        JsonArray presets = array(document.get("presets"));
        if (!path.isEmpty() && "presets".equals(path.get(0))) {
            lines.add(local("预设由所有引用它的装备共用。", "Equipment profiles share each referenced preset."));
            if (path.size() >= 2) {
                JsonObject preset = object(element(presets, path.get(1)));
                lines.add(Component.literal(string(preset, "id", "")));
                appendEffects(lines, array(preset.get("punishments")));
            }
            return lines;
        }

        String id = path.size() >= 2 && "items".equals(path.get(0)) ? path.get(1) : id(held);
        if (id.isEmpty()) {
            lines.add(local("选择装备或将物品放在主手进行预览。", "Select equipment or hold an item to preview it."));
            return lines;
        }
        lines.add(itemLabel(id));
        JsonElement configured = object(document.get("items")).get(id);
        List<ConfigEditorPageMath.Requirement> requirements = new ArrayList<>();
        double heavy;
        if (configured instanceof JsonObject profile) {
            heavy = Math.max(0, integer(profile, "heavy", 0));
            for (JsonObject entry : objects(array(profile.get("attribute_need")))) {
                String type = string(entry, "attribute_id", "");
                if (type.isBlank()) continue;
                String preset = string(entry, "punishment_id", "default");
                requirements.add(new ConfigEditorPageMath.Requirement(type, integer(entry, "need_points", 0),
                        preset.isBlank() ? "default" : preset));
            }
            lines.add(local("配置重量 · ", "Configured weight · ").copy().append(number(heavy)));
        } else {
            ItemStack item = registeredStack(id);
            heavy = ItemPunishmentTool.defaultHeavy(item);
            for (ItemPunishmentTool.Requirement need : ItemPunishmentTool.defaultRequirements(item)) {
                requirements.add(new ConfigEditorPageMath.Requirement(need.attributeType(), need.minimumLevel(), need.preset()));
            }
            lines.add(local("自动推断重量 · ", "Fallback weight · ").copy().append(number(heavy)));
        }

        PlayerOccupationAttribute player = minecraft.player == null ? null : minecraft.player
                .getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).orElse(null);
        Map<String, Long> totals = new HashMap<>();
        Map<String, JsonArray> effects = new HashMap<>();
        for (JsonObject preset : objects(presets)) effects.put(string(preset, "id", ""), array(preset.get("punishments")));
        for (ConfigEditorPageMath.Requirement requirement : requirements) {
            String type = requirement.type();
            int base = levelOverride(inputs.get("base"), type, player == null ? 0 : player.getOccupationAttributeLevel().getOrDefault(type, 0));
            int curio = levelOverride(inputs.get("curio"), type, player == null ? 0 : player.getCurioAttributeLevelValue(type));
            long total = (long) base + curio;
            totals.put(type, total);
            lines.add(Component.literal("  " + type + " · " + base + " + (" + curio + ") = " + total + " / " + requirement.minimum() + " · ")
                    .append(total >= requirement.minimum() ? local("达到", "Met") : local("不足", "Unmet"))
                    .append(" · " + requirement.preset()));
        }
        ConfigEditorPageMath.Requirement active = ConfigEditorPageMath.selectPenalty(requirements, totals::get,
                preset -> effects.containsKey(preset) && !effects.get(preset).isEmpty());
        if (active == null) lines.add(local("模拟结果 · 无惩罚", "Simulated result · no penalty"));
        else {
            lines.add(local("首个生效的未达标需求 · ", "First unmet requirement with effects · ").copy()
                    .append(active.type() + " · " + active.preset()));
            appendEffects(lines, effects.get(active.preset()));
        }
        return lines;
    }

    private static void appendEffects(List<Component> lines, JsonArray effects) {
        Set<String> applied = new HashSet<>();
        for (JsonObject effect : objects(effects)) {
            String attribute = string(effect, "attribute", "");
            String operation = string(effect, "operation", "");
            double amount = decimal(effect, "value", 0);
            var resolved = LHMiracleRoadTool.stringConversionAttribute(attribute);
            var id = resolved == null ? null : ForgeRegistries.ATTRIBUTES.getKey(resolved);
            if (id == null || LHMiracleRoadTool.stringConversionOperation(operation) == null || amount == 0 || !applied.add(id.toString())) continue;
            lines.add(Component.literal("  " + attribute + " · " + signedModifier(amount, operation) + " · " + operation));
        }
    }

    private static int levelOverride(String input, String type, int fallback) {
        if (input == null || input.isBlank()) return fallback;
        if (!input.contains("=")) return integer(input, fallback, -100000, 100000);
        for (String pair : input.split(",")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2 && keyValue[0].trim().equals(type)) return integer(keyValue[1], fallback, -100000, 100000);
        }
        return fallback;
    }

    private static List<Component> trophies(JsonObject document, List<String> path, Minecraft minecraft, Map<String, String> inputs) {
        List<Component> lines = new ArrayList<>();
        lines.add(local("先按权重抽一个组，再按该组规则抽取物品。", "Draw one weighted group, then draw items using that group's rules."));
        if (path.isEmpty()) return lines;
        float luck = (float) decimal(inputs.get("luck"), minecraft.player == null ? 0 : minecraft.player.getLuck());
        if (!Float.isFinite(luck)) luck = 0;
        long seed = longNumber(inputs.get("seed"), 42);
        JsonArray groupsJson = array(document.get(path.get(0)));
        List<TrophyData> groups = new ArrayList<>();
        for (JsonObject data : objects(groupsJson)) {
            try {
                TrophyData group = GSON.fromJson(data, TrophyData.class);
                if (group.entries == null) group.entries = List.of();
                else group.entries.removeIf(entry -> entry == null);
                groups.add(group);
            } catch (RuntimeException invalid) {
                lines.add(local("部分条目格式无效。", "A group contains invalid values."));
            }
        }
        long groupTotal = 0;
        for (TrophyData group : groups) groupTotal += weight(group, luck);
        TrophyData selected = null;
        if (path.size() >= 2) {
            int index = integer(path.get(1), -1, -1, Integer.MAX_VALUE);
            if (index >= 0 && index < groups.size()) selected = groups.get(index);
        }
        lines.add(local("模拟幸运 · ", "Simulated luck · ").copy().append(number(luck)));
        for (TrophyData group : groups) {
            if (selected != null && selected != group) continue;
            double groupProbability = groupTotal > 0 ? (double) weight(group, luck) / groupTotal : 0;
            int minimum = Math.max(0, group.roll_count_min);
            int maximum = Math.max(minimum, group.roll_count_max);
            lines.add(Component.literal(group.name + " · " + percentage(groupProbability))
                    .append(local(" · 抽取 ", " · Draws ")).append(minimum + "–" + maximum)
                    .append(group.allowRepeat ? local(" · 允许重复", " · Repeats allowed") : local(" · 按物品 ID 去重", " · Unique item IDs")));
            if (selected != null) {
                long total = 0;
                for (TrophyEntry entry : group.entries) total += weight(entry, luck);
                lines.add(local("组内首抽命中 / 整袋首抽命中（无效条目抽空）",
                        "Entry chance on first draw / overall first draw (invalid entries give no item)"));
                int rewardIndex = path.size() >= 4 ? integer(path.get(3), -1, -1, Integer.MAX_VALUE) : -1;
                for (int index = 0; index < group.entries.size(); index++) {
                    if (rewardIndex >= 0 && rewardIndex != index) continue;
                    TrophyEntry entry = group.entries.get(index);
                    double conditional = total > 0 ? (double) weight(entry, luck) / total : 0;
                    double startsDrawing = maximum == 0 ? 0 : minimum > 0 ? 1 : (double) maximum / (maximum + 1L);
                    int minCount = Math.max(1, entry.min);
                    int maxCount = Math.max(minCount, entry.max);
                    lines.add(itemLabel(entry.item == null ? "" : entry.item).copy().append(" ×" + minCount + "–" + maxCount + " · ")
                            .append(percentage(conditional) + " / " + percentage(groupProbability * conditional * startsDrawing))
                            .append(local(" · 实际权重 ", " · Effective weight ")).append(Integer.toString(weight(entry, luck))));
                }
            }
        }

        Map<String, Integer> sample = ConfigEditorPageMath.sample(groups, luck, new Random(seed),
                ConfigEditorPagePreview::rewardId, ConfigEditorPagePreview::validReward);
        lines.add(local("整袋试抽 · 种子 ", "Bag sample · seed ").copy().append(Long.toString(seed)));
        if (sample.isEmpty()) lines.add(local("  无奖励", "  No rewards"));
        sample.forEach((id, count) -> lines.add(Component.literal("  ").append(itemLabel(id)).append(" ×" + count)));
        return lines;
    }

    private static int weight(TrophyData group, float luck) {
        return group.weight > 0 ? group.getWeight(luck) : 0;
    }

    private static int weight(TrophyEntry entry, float luck) {
        return entry.weight > 0 ? entry.getWeight(luck) : 0;
    }

    private static String rewardId(TrophyEntry entry) {
        ResourceLocation location = entry.item == null ? null : ResourceLocation.tryParse(entry.item);
        return location == null ? null : location.toString();
    }

    private static boolean validReward(TrophyEntry entry) {
        if (entry.nbt == null || registeredStack(entry.item).isEmpty()) return false;
        if (!entry.nbt.isBlank()) {
            try {
                TagParser.parseTag(entry.nbt);
            } catch (Exception invalid) {
                return false;
            }
        }
        return true;
    }

    private static ItemStack registeredStack(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        Item item = location == null ? null : ForgeRegistries.ITEMS.getValue(location);
        return item == null || item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static String id(ItemStack stack) {
        ResourceLocation key = stack.isEmpty() ? null : ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key == null ? "" : key.toString();
    }

    private static Component yes(boolean value) {
        return value ? local("是", "Yes") : local("否", "No");
    }

    private static String signedModifier(double amount, String operation) {
        boolean percentage = !"addition".equals(operation);
        return (amount > 0 ? "+" : "") + (percentage ? percentage(amount) : number(amount));
    }

    private static String percentage(double value) {
        return number(value * 100) + "%";
    }

    private static String number(double value) {
        return Double.isFinite(value) ? BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() : "—";
    }

    static String string(JsonObject object, String key, String fallback) {
        JsonElement field = object.get(key);
        return field != null && field.isJsonPrimitive() ? field.getAsString() : fallback;
    }

    private static int integer(JsonObject object, String key, int fallback) {
        return integer(string(object, key, ""), fallback, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static int integer(String value, int fallback, int min, int max) {
        return (int) Math.max(min, Math.min(max, longNumber(value, fallback)));
    }

    private static long longNumber(String value, long fallback) {
        if (value == null || value.isBlank()) return fallback;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException invalid) {
            return fallback;
        }
    }

    private static double decimal(JsonObject object, String key, double fallback) {
        return decimal(string(object, key, ""), fallback);
    }

    private static double decimal(String value, double fallback) {
        if (value == null || value.isBlank()) return fallback;
        try {
            double number = Double.parseDouble(value.trim());
            return Double.isFinite(number) ? number : fallback;
        } catch (NumberFormatException invalid) {
            return fallback;
        }
    }

    private static JsonElement element(JsonArray values, String index) {
        int row = integer(index, -1, -1, Integer.MAX_VALUE);
        return row >= 0 && row < values.size() ? values.get(row) : null;
    }

    private static JsonArray array(JsonElement value) {
        return value instanceof JsonArray array ? array : new JsonArray();
    }

    private static JsonObject object(JsonElement value) {
        return value instanceof JsonObject object ? object : new JsonObject();
    }

    private static List<JsonObject> objects(JsonArray array) {
        List<JsonObject> objects = new ArrayList<>();
        for (JsonElement element : array) if (element instanceof JsonObject object) objects.add(object);
        return objects;
    }
}
