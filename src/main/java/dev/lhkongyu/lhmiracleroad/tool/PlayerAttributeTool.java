package dev.lhkongyu.lhmiracleroad.tool;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.config.initItem.InitItemConfig;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationAttribute;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationConfig;
import dev.lhkongyu.lhmiracleroad.config.occupation.PointsReward;
import dev.lhkongyu.lhmiracleroad.data.reloader.AttributePointsRewardsReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerAttributeTool {

    public static void initOccupation(ServerPlayer player,String occupationId){
        Occupation configured = OccupationConfig.getOccupation(occupationId);
        if (configured != null) {
            player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(attributes -> {
                if (attributes.getOccupationId() != null && !attributes.getOccupationId().isBlank()) return;
                applyConfiguredOccupation(player, configured, attributes, true);
                InitItemConfig.grantToPlayer(player, occupationId);
                LHMiracleRoadTool.playerPunishmentStateUpdate(player, attributes);
            });
            return;
        }

        JsonObject occupation = LHMiracleRoadTool.getOccupation(occupationId);
        if (occupation == null) return;
        //获取职业基本信息
        int initDifficultyLevel = LHMiracleRoadTool.isAsInt(occupation.get("init_difficulty_level"));
        Map<String,Integer> initAttributeLevel = LHMiracleRoadTool.setInitAttributeLevel(occupation);
        List<JsonObject> items = LHMiracleRoadTool.setInitItem(occupation);

        Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
        if (optionalPlayerOccupationAttribute.isEmpty()) return;
        PlayerOccupationAttribute playerOccupationAttribute = optionalPlayerOccupationAttribute.get();
        if (playerOccupationAttribute.getOccupationId() != null && !playerOccupationAttribute.getOccupationId().isBlank()) return;

        //设置基础属性值
        LHMiracleRoadTool.setConfigBaseAttribute(player,initDifficultyLevel);
        //设置初始属性等级所带来的提升值
        calculateAttribute(player,initAttributeLevel,occupationId,playerOccupationAttribute);
        //初始物品
        initItem(player,items);
        //更新玩家奖惩状态
        LHMiracleRoadTool.playerPunishmentStateUpdate(player,playerOccupationAttribute);
    }

    public static boolean restoreConfiguredOccupation(ServerPlayer player, PlayerOccupationAttribute attributes) {
        Occupation occupation = OccupationConfig.getOccupation(attributes.getOccupationId());
        if (occupation == null) return false;

        applyConfiguredOccupation(player, occupation, attributes, false);
        return true;
    }

    private static void applyConfiguredOccupation(ServerPlayer player, Occupation occupation,
                                                  PlayerOccupationAttribute attributes, boolean resetLevels) {
        clearOccupationModifiers(player, attributes);
        JsonObject legacy = LHMiracleRoadTool.getOccupation(occupation.getId());
        int difficulty = legacy == null ? 0 : LHMiracleRoadTool.isAsInt(legacy.get("init_difficulty_level"));
        LHMiracleRoadTool.setConfigBaseAttribute(player, difficulty);

        Map<String, Integer> levels = resetLevels ? new HashMap<>() : new HashMap<>(attributes.getOccupationAttributeLevel());
        if (occupation.getOccupationAttributes() != null) {
            for (OccupationAttribute type : occupation.getOccupationAttributes()) {
                String id = type.getType_id();
                if (id == null || id.isBlank()) continue;
                int level = levels.computeIfAbsent(id, key -> type.getInit_lv());
                applyConfiguredRewards(player, attributes, type, level, true);
            }
        }

        attributes.setOccupationId(occupation.getId());
        attributes.setOccupationAttributeLevel(levels);
        if (resetLevels) {
            int total = levels.values().stream().mapToInt(Integer::intValue).sum();
            attributes.setOccupationLevel(total - levels.size() * LHMiracleRoadConfig.COMMON.LEVEL_BASE.get() + 1);
        }
        attributes.setEmpiricalCalculationFormula(LHMiracleRoadConfig.COMMON.EMPIRICAL_CALCULATION_FORMULA.get());
        if (attributes.getAttributeMaxLevel() < 1) {
            attributes.setAttributeMaxLevel(LHMiracleRoadConfig.COMMON.ATTRIBUTE_MAX_LEVEL.get());
        }
        attributes.setMaxLevel(LHMiracleRoadConfig.COMMON.MAX_LEVEL.get());
    }

    private static OccupationAttribute configuredAttribute(Occupation occupation, String typeId) {
        if (occupation.getOccupationAttributes() == null) return null;
        for (OccupationAttribute type : occupation.getOccupationAttributes()) {
            if (typeId.equals(type.getType_id())) return type;
        }
        return null;
    }

    private static void applyConfiguredRewards(ServerPlayer player, PlayerOccupationAttribute attributes,
                                               OccupationAttribute type, int level, boolean recoverHealth) {
        int effectiveLevel = level + attributes.getCurioAttributeLevelValue(type.getType_id());
        for (PointsReward reward : type.getPointsRewards()) {
            String attributeId = reward.getAttribute_id();
            if (attributeId == null || attributeId.isBlank()) continue;

            AttributeModifier.Operation operation = LHMiracleRoadTool.stringConversionOperation(reward.getOperation());
            if (operation == null) continue;

            String namespace = reward.getMod_id();
            if (namespace == null || namespace.isBlank()) namespace = "minecraft";
            ResourceLocation id = ResourceLocation.tryParse(attributeId.contains(":") ? attributeId : namespace + ":" + attributeId);
            if (id == null) continue;

            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(id);
            if (attribute == null && id.getNamespace().equals("minecraft") && !id.getPath().contains(".")) {
                attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("minecraft", "generic." + id.getPath()));
            }
            if (attribute == null) continue;
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) continue;

            String key = ForgeRegistries.ATTRIBUTES.getKey(attribute) + "#" + type.getType_id();
            double amount = LHMiracleRoadTool.calculateTotalIncrease(effectiveLevel, reward.getValue(),
                    reward.getLv_promote_value(), reward.getLv_promote(), reward.getMin());
            AttributeModifier oldModifier = attributes.getAttributeModifier().remove(key);
            if (oldModifier != null) instance.removeModifier(oldModifier);
            if (amount != 0) {
                AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "", amount, operation);
                instance.addTransientModifier(modifier);
                attributes.addAttributeModifier(key, modifier);
            }
            if (attribute == Attributes.MAX_HEALTH) {
                player.setHealth(recoverHealth ? player.getMaxHealth() : Math.min(player.getHealth(), player.getMaxHealth()));
            }
        }
    }

    private static void clearOccupationModifiers(ServerPlayer player, PlayerOccupationAttribute attributes) {
        attributes.getAttributeModifier().forEach((key, modifier) -> {
            Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(key);
            AttributeInstance instance = attribute == null ? null : player.getAttribute(attribute);
            if (instance != null) instance.removeModifier(modifier);
        });
        attributes.clearAttributeModifier();
    }

    /**
     * 初始化属性值
     * @param player
     * @param initAttributeLevel
     * @param occupationId
     * @param playerOccupationAttribute
     */
    public static void calculateAttribute(ServerPlayer player, Map<String,Integer> initAttributeLevel,String occupationId,PlayerOccupationAttribute playerOccupationAttribute){
        int level = 0;
        for (String key : initAttributeLevel.keySet()) {
            JsonObject jsonObject = AttributePointsRewardsReloadListener.ATTRIBUTE_POINTS_REWARDS.get(key);
            setAttribute(player, jsonObject, initAttributeLevel.get(key),playerOccupationAttribute,key);
            level += initAttributeLevel.get(key);
        }
        level = (level - initAttributeLevel.size() * LHMiracleRoadConfig.COMMON.LEVEL_BASE.get()) + 1;
        playerOccupationAttribute.setOccupationId(occupationId);
        playerOccupationAttribute.setOccupationAttributeLevel(initAttributeLevel);
        playerOccupationAttribute.setOccupationLevel(level);
        playerOccupationAttribute.setEmpiricalCalculationFormula(LHMiracleRoadConfig.COMMON.EMPIRICAL_CALCULATION_FORMULA.get());
        if (playerOccupationAttribute.getAttributeMaxLevel() < 1) {
            playerOccupationAttribute.setAttributeMaxLevel(LHMiracleRoadConfig.COMMON.ATTRIBUTE_MAX_LEVEL.get());
        }
        playerOccupationAttribute.setMaxLevel(LHMiracleRoadConfig.COMMON.MAX_LEVEL.get());
    }

    /**
     * 根据现有的条件下进行重新计算属性来达到同步
     * @param player
     * @param newAttributeLevel
     * @param playerOccupationAttribute
     */
    public static void calculateAttribute(ServerPlayer player, Map<String,Integer> newAttributeLevel,PlayerOccupationAttribute playerOccupationAttribute){
        int level = 0;
        for (String key : newAttributeLevel.keySet()) {
            JsonObject jsonObject = AttributePointsRewardsReloadListener.ATTRIBUTE_POINTS_REWARDS.get(key);
            setAttribute(player, jsonObject, newAttributeLevel.get(key),playerOccupationAttribute,key);
            level += newAttributeLevel.get(key);
        }
        level = (level - newAttributeLevel.size() * LHMiracleRoadConfig.COMMON.LEVEL_BASE.get()) + 1;
        playerOccupationAttribute.setOccupationAttributeLevel(newAttributeLevel);
        playerOccupationAttribute.setOccupationLevel(level);
        playerOccupationAttribute.setEmpiricalCalculationFormula(LHMiracleRoadConfig.COMMON.EMPIRICAL_CALCULATION_FORMULA.get());
        player.getAttribute(LHMiracleRoadAttributes.BURDEN).setBaseValue(LHMiracleRoadConfig.COMMON.INIT_BURDEN.get());
        if (playerOccupationAttribute.getAttributeMaxLevel() < 1) {
            playerOccupationAttribute.setAttributeMaxLevel(LHMiracleRoadConfig.COMMON.ATTRIBUTE_MAX_LEVEL.get());
        }
        playerOccupationAttribute.setMaxLevel(LHMiracleRoadConfig.COMMON.MAX_LEVEL.get());
    }

    /**
     * 给对应能力进行属性的增加
     * @param player
     * @param jsonObject
     * @param attributeLevel
     * @param playerOccupationAttribute
     */
    public static void setAttribute(ServerPlayer player, JsonObject jsonObject, int attributeLevel, PlayerOccupationAttribute playerOccupationAttribute,String attributeTypeName){
        for (JsonElement jsonElement : LHMiracleRoadTool.isAsJsonArray(jsonObject.get("points_rewards"))) {
            JsonObject object = jsonElement.getAsJsonObject();
            String attributeName = LHMiracleRoadTool.isAsString(object.get("attribute"));
            double value = LHMiracleRoadTool.isAsDouble(object.get("value"));
            if (value == 0) continue;
            int levelPromote = LHMiracleRoadTool.isAsInt(object.get("level_promote"));
            double levelPromoteValue = LHMiracleRoadTool.isAsDouble(object.get("level_promote_value"));
            String operationString = LHMiracleRoadTool.isAsString(object.get("operation"));
            double attributeValue;
            double min = LHMiracleRoadTool.isAsDouble(object.get("min"));

            int curioAttributeLevelValue = playerOccupationAttribute.getCurioAttributeLevelValue(attributeTypeName);
            attributeLevel += curioAttributeLevelValue;

            attributeValue = LHMiracleRoadTool.calculateTotalIncrease(attributeLevel, value, levelPromoteValue, levelPromote, min);
            addExtraAttributes(player,attributeName,attributeValue,operationString,playerOccupationAttribute,attributeTypeName);
        }
    }

    /**
     * 给对应能力进行属性的增加
     * @param player
     * @param jsonObject
     * @param attributeLevel
     * @param playerOccupationAttribute
     */
    public static void setAttributeNotRecoverHP(ServerPlayer player, JsonObject jsonObject, int attributeLevel, PlayerOccupationAttribute playerOccupationAttribute,String attributeTypeName){
        Occupation occupation = OccupationConfig.getOccupation(playerOccupationAttribute.getOccupationId());
        if (occupation != null) {
            OccupationAttribute type = configuredAttribute(occupation, attributeTypeName);
            if (type != null) applyConfiguredRewards(player, playerOccupationAttribute, type, attributeLevel, false);
            return;
        }
        if (jsonObject == null) return;

        for (JsonElement jsonElement : LHMiracleRoadTool.isAsJsonArray(jsonObject.get("points_rewards"))) {
            JsonObject object = jsonElement.getAsJsonObject();
            String attributeName = LHMiracleRoadTool.isAsString(object.get("attribute"));
            double value = LHMiracleRoadTool.isAsDouble(object.get("value"));
            if (value == 0) continue;
            int levelPromote = LHMiracleRoadTool.isAsInt(object.get("level_promote"));
            double levelPromoteValue = LHMiracleRoadTool.isAsDouble(object.get("level_promote_value"));
            String operationString = LHMiracleRoadTool.isAsString(object.get("operation"));
            double attributeValue;
            double min = LHMiracleRoadTool.isAsDouble(object.get("min"));

            int curioAttributeLevelValue = playerOccupationAttribute.getCurioAttributeLevelValue(attributeTypeName);
            attributeLevel += curioAttributeLevelValue;

            attributeValue = LHMiracleRoadTool.calculateTotalIncrease(attributeLevel, value, levelPromoteValue, levelPromote, min);
            addExtraAttributesNotRecoverHP(player,attributeName,attributeValue,operationString,playerOccupationAttribute,attributeTypeName);
        }
    }

    /**
     * 将计算后的数据添加进能力当中去
     * @param player
     * @param attributeName
     * @param attributeValue
     * @param operationString
     * @param playerOccupationAttribute
     */
    private static void addExtraAttributes(ServerPlayer player,String attributeName,double attributeValue,String operationString, PlayerOccupationAttribute playerOccupationAttribute,String attributeTypeName){
        AttributeModifier.Operation operation = LHMiracleRoadTool.stringConversionOperation(operationString);
        if (operation == null) return;
        Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(attributeName);
        if (attribute != null){
            AttributeModifier attributeModifier = new AttributeModifier(UUID.randomUUID(), "", attributeValue, operation);

            AttributeModifier playerAttributeModifier = playerOccupationAttribute.getAttributeModifier().get(attributeName + "#" + attributeTypeName);
            if (playerAttributeModifier != null) player.getAttribute(attribute).removeModifier(playerAttributeModifier);

            player.getAttribute(attribute).addTransientModifier(attributeModifier);
            if (attribute.getDescriptionId().equals(Attributes.MAX_HEALTH.getDescriptionId())){
                player.setHealth((float) player.getAttribute(attribute).getValue());
            }
            playerOccupationAttribute.addAttributeModifier(attributeName + "#" + attributeTypeName,attributeModifier);
        }
    }

    /**
     * 将计算后的数据添加进能力当中去
     * @param player
     * @param attributeName
     * @param attributeValue
     * @param operationString
     * @param playerOccupationAttribute
     */
    private static void addExtraAttributesNotRecoverHP(ServerPlayer player,String attributeName,double attributeValue,String operationString, PlayerOccupationAttribute playerOccupationAttribute,String attributeTypeName){
        AttributeModifier.Operation operation = LHMiracleRoadTool.stringConversionOperation(operationString);
        if (operation == null) return;
        Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(attributeName);
        if (attribute != null){
            AttributeModifier attributeModifier = new AttributeModifier(UUID.randomUUID(), "", attributeValue, operation);

            AttributeModifier playerAttributeModifier = playerOccupationAttribute.getAttributeModifier().get(attributeName + "#" + attributeTypeName);
            if (playerAttributeModifier != null) player.getAttribute(attribute).removeModifier(playerAttributeModifier);

            player.getAttribute(attribute).addTransientModifier(attributeModifier);
            playerOccupationAttribute.addAttributeModifier(attributeName + "#" + attributeTypeName,attributeModifier);
        }
    }

    /**
     * 初始化物品
     * @param player
     * @param initItem
     */
    private static void initItem(ServerPlayer player,List<JsonObject> initItem){
        for (JsonObject object :initItem){
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(LHMiracleRoadTool.isAsString(object.get("item"))));
            if (item == null) continue;
            int quantity = LHMiracleRoadTool.isAsInt(object.get("quantity"));
            Boolean isSplit = LHMiracleRoadTool.isAsBoolean(object.get("is_split"));
            ItemStack itemStack = null;
            String tag = LHMiracleRoadTool.isAsString(object.get("tag"));
            if (isSplit){
                itemStack = new ItemStack(item, 1);
                LHMiracleRoadTool.setTag(itemStack,tag);
                for (int i = 0; i < quantity; i++) {
                    LHMiracleRoadTool.addItemStack(player,itemStack.copy());
                }
            }else {
                itemStack = new ItemStack(item, quantity);
                LHMiracleRoadTool.setTag(itemStack,tag);
                LHMiracleRoadTool.addItemStack(player, itemStack);
            }
        }
    }

    public static void points(ServerPlayer player, String attributeTypeName, int count) {
        if (count != 1 && count != 10) return;
        Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
        if (optionalPlayerOccupationAttribute.isEmpty()) return;
        PlayerOccupationAttribute playerOccupationAttribute = optionalPlayerOccupationAttribute.get();
        String occupationId = playerOccupationAttribute.getOccupationId();
        if (occupationId == null || occupationId.isBlank()) return;

        Occupation occupation = OccupationConfig.getOccupation(occupationId);
        OccupationAttribute type = occupation == null ? null : configuredAttribute(occupation, attributeTypeName);
        if (occupation != null && type == null) return;

        JsonObject legacyRewards = type == null ? AttributePointsRewardsReloadListener.ATTRIBUTE_POINTS_REWARDS.get(attributeTypeName) : null;
        if (type == null && legacyRewards == null) return;

        Integer currentLevel = playerOccupationAttribute.getOccupationAttributeLevel().get(attributeTypeName);
        if (currentLevel == null) return;
        int attributeLimit = type == null ? LHMiracleRoadTool.isAsInt(legacyRewards.get("max_level")) : type.getMax_lv();
        if (!isWithinAttributeLimit(playerOccupationAttribute, currentLevel, attributeLimit, count)) return;

        String formula = LHMiracleRoadConfig.COMMON.EMPIRICAL_CALCULATION_FORMULA.get();
        UpgradePayment payment = quoteUpgrade(playerOccupationAttribute, count, formula);
        if (payment == null) return;

        int newLevel = currentLevel + count;
        if (type != null) {
            applyConfiguredRewards(player, playerOccupationAttribute, type, newLevel, true);
        } else {
            setAttribute(player, legacyRewards, newLevel, playerOccupationAttribute, attributeTypeName);
        }
        playerOccupationAttribute.putOccupationAttributeLevel(attributeTypeName, newLevel);
        playerOccupationAttribute.setPoints(payment.remainingPoints());
        playerOccupationAttribute.setOccupationExperience(payment.remainingSouls());
        playerOccupationAttribute.setOccupationLevel(playerOccupationAttribute.getOccupationLevel() + count);
        LHMiracleRoadTool.playerPunishmentStateUpdate(player, playerOccupationAttribute);
    }

    public static boolean isWithinAttributeLimit(PlayerOccupationAttribute attributes, int level, int configuredLimit, int count) {
        int override = attributes.getAttributeMaxLevel();
        return (long) level + count <= (override > 0 ? override : configuredLimit);
    }

    public static boolean canPayForUpgrade(PlayerOccupationAttribute attributes, int count) {
        return quoteUpgrade(attributes, count, attributes.getEmpiricalCalculationFormula()) != null;
    }

    private static UpgradePayment quoteUpgrade(PlayerOccupationAttribute attributes, int count, String formula) {
        int level = attributes.getOccupationLevel();
        if ((long) level + count > attributes.getMaxLevel()) return null;

        // Price every level before spending anything so a +10 request cannot partially succeed.
        int points = attributes.getPoints();
        int souls = attributes.getOccupationExperience();
        for (int i = 0; i < count; i++) {
            if (points > 0) {
                points--;
            } else {
                if (formula == null || formula.isBlank()) return null;
                int cost = Math.max(0, LHMiracleRoadTool.evaluateFormula(formula, level + i));
                if (souls < cost) return null;
                souls -= cost;
            }
        }
        return new UpgradePayment(points, souls);
    }

    private record UpgradePayment(int remainingPoints, int remainingSouls) {
    }

    public static void resetLevel(ServerPlayer player,PlayerOccupationAttribute playerOccupationAttribute){
        String occupationId = playerOccupationAttribute.getOccupationId();
        Occupation configured = OccupationConfig.getOccupation(occupationId);
        if (configured != null) {
            applyConfiguredOccupation(player, configured, playerOccupationAttribute, true);
            LHMiracleRoadTool.playerPunishmentStateUpdate(player, playerOccupationAttribute);
            return;
        }
        JsonObject occupation = LHMiracleRoadTool.getOccupation(occupationId);
        if (occupation == null) return;
        //获取职业基本信息
        int initDifficultyLevel = LHMiracleRoadTool.isAsInt(occupation.get("init_difficulty_level"));
        Map<String,Integer> initAttributeLevel = LHMiracleRoadTool.setInitAttributeLevel(occupation);

        //设置基础属性值
        LHMiracleRoadTool.setConfigBaseAttribute(player,initDifficultyLevel);
        //设置初始属性等级所带来的提升值
        calculateAttribute(player,initAttributeLevel,occupationId,playerOccupationAttribute);
        //更新玩家奖惩状态
        LHMiracleRoadTool.playerPunishmentStateUpdate(player,playerOccupationAttribute);
    }

    public static void resetLevelReturnPoints(ServerPlayer player,PlayerOccupationAttribute playerOccupationAttribute){
        String occupationId = playerOccupationAttribute.getOccupationId();
        Occupation configured = OccupationConfig.getOccupation(occupationId);
        if (configured != null) {
            Map<String, Integer> previousLevels = playerOccupationAttribute.getOccupationAttributeLevel();
            resetLevel(player, playerOccupationAttribute);
            int points = 0;
            for (Map.Entry<String, Integer> entry : playerOccupationAttribute.getOccupationAttributeLevel().entrySet()) {
                int initial = entry.getValue();
                points += Math.max(0, previousLevels.getOrDefault(entry.getKey(), initial) - initial);
            }
            playerOccupationAttribute.addPoints(points);
            return;
        }
        JsonObject occupation = LHMiracleRoadTool.getOccupation(occupationId);
        if (occupation == null) return;
        //获取职业基本信息
        int initDifficultyLevel = LHMiracleRoadTool.isAsInt(occupation.get("init_difficulty_level"));
        Map<String,Integer> initAttributeLevel = LHMiracleRoadTool.setInitAttributeLevel(occupation);
        int points = 0;
        for (String key : initAttributeLevel.keySet()){
            int from = initAttributeLevel.get(key);
            int to = playerOccupationAttribute.getOccupationAttributeLevel().get(key);
            points += to - from;
        }

        //设置基础属性值
        LHMiracleRoadTool.setConfigBaseAttribute(player,initDifficultyLevel);
        //设置初始属性等级所带来的提升值
        calculateAttribute(player,initAttributeLevel,occupationId,playerOccupationAttribute);
        //设置返回的技能点数
        playerOccupationAttribute.addPoints(points);
        //更新玩家奖惩状态
        LHMiracleRoadTool.playerPunishmentStateUpdate(player,playerOccupationAttribute);
    }

    public static void resetOccupation(ServerPlayer player,PlayerOccupationAttribute playerOccupationAttribute){
        playerOccupationAttribute.setOccupationId(null);
        clearOccupationModifiers(player, playerOccupationAttribute);
        SyncTool.synchronizationClient(playerOccupationAttribute,player);
        //更新玩家奖惩状态
        LHMiracleRoadTool.playerPunishmentStateUpdate(player,playerOccupationAttribute);
    }
}
