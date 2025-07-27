package dev.lhkongyu.lhmiracleroad.tool;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.data.reloader.AttributePointsRewardsReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.K;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerAttributeTool {

    public static void initOccupation(ServerPlayer player,String occupationId){
        JsonObject occupation = LHMiracleRoadTool.getOccupation(occupationId);
        if (occupation == null) return;
        //获取职业基本信息
        int initDifficultyLevel = LHMiracleRoadTool.isAsInt(occupation.get("init_difficulty_level"));
        Map<String,Integer> initAttributeLevel = LHMiracleRoadTool.setInitAttributeLevel(occupation);
        List<JsonObject> items = LHMiracleRoadTool.setInitItem(occupation);

        Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
        if (optionalPlayerOccupationAttribute.isEmpty()) return;
        PlayerOccupationAttribute playerOccupationAttribute = optionalPlayerOccupationAttribute.get();

        //设置基础属性值
        LHMiracleRoadTool.setConfigBaseAttribute(player,initDifficultyLevel);
        //设置初始属性等级所带来的提升值
        calculateAttribute(player,initAttributeLevel,occupationId,playerOccupationAttribute);
        //初始物品
        initItem(player,items);
        //更新玩家奖惩状态
        LHMiracleRoadTool.playerPunishmentStateUpdate(player,playerOccupationAttribute);
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

    public static void points(ServerPlayer player,String attributeTypeName){
        Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
        if (optionalPlayerOccupationAttribute.isEmpty()) return;
        PlayerOccupationAttribute playerOccupationAttribute = optionalPlayerOccupationAttribute.get();

        //玩家所持有点数
        int points = playerOccupationAttribute.getPoints();

        //玩家所持有经验
        int exp = playerOccupationAttribute.getOccupationExperience();
        //获取玩家下一级所需要的经验
        int demandExperienceValue = LHMiracleRoadTool.evaluateFormula(LHMiracleRoadConfig.COMMON.EMPIRICAL_CALCULATION_FORMULA.get(),playerOccupationAttribute.getOccupationLevel());

        if (exp < demandExperienceValue && points < 1) return;

        //通过属性类型名称获取其下所有的属性
        JsonObject jsonObject = AttributePointsRewardsReloadListener.ATTRIBUTE_POINTS_REWARDS.get(attributeTypeName);
        if (jsonObject == null) return;

        int level = playerOccupationAttribute.getOccupationAttributeLevel().get(attributeTypeName);
        //每次执行该方法就代表该能力加一
        level++;
        //给该属性类型的等级进行修改
        playerOccupationAttribute.putOccupationAttributeLevel(attributeTypeName, level);
        //给对应属性类型进行 属性增长
        setAttribute(player, jsonObject, level, playerOccupationAttribute,attributeTypeName);

        //判断优化扣除点数
        if (points > 0){
            points -= 1;
            playerOccupationAttribute.setPoints(points);
        }else {
            //修改当前经验和提升当前等级
            exp -= demandExperienceValue;
            playerOccupationAttribute.setOccupationExperience(exp);
        }
        playerOccupationAttribute.upgrade();
        //更新玩家奖惩状态
        LHMiracleRoadTool.playerPunishmentStateUpdate(player, playerOccupationAttribute);
    }

    public static void resetLevel(ServerPlayer player,PlayerOccupationAttribute playerOccupationAttribute){
        String occupationId = playerOccupationAttribute.getOccupationId();
        JsonObject occupation = LHMiracleRoadTool.getOccupation(occupationId);
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
        JsonObject occupation = LHMiracleRoadTool.getOccupation(occupationId);
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
        for (String key : AttributePointsRewardsReloadListener.ATTRIBUTE_POINTS_REWARDS.keySet()){
            JsonObject jsonObject = AttributePointsRewardsReloadListener.ATTRIBUTE_POINTS_REWARDS.get(key);
            for (JsonElement pointsRewardElement : LHMiracleRoadTool.isAsJsonArray(jsonObject.get("points_rewards"))) {
                JsonObject pointsRewardObj = pointsRewardElement.getAsJsonObject();
                String attributeName = LHMiracleRoadTool.isAsString(pointsRewardObj.get("attribute"));
                Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(attributeName);
                if (attribute != null) {
                    AttributeModifier playerAttributeModifier = playerOccupationAttribute.getAttributeModifier().get(attributeName + "#" + key);
                    if (playerAttributeModifier != null) player.getAttribute(attribute).removeModifier(playerAttributeModifier);
                }
            }
        }
        LHMiracleRoadTool.synchronizationClient(playerOccupationAttribute,player);
        //更新玩家奖惩状态
        LHMiracleRoadTool.playerPunishmentStateUpdate(player,playerOccupationAttribute);
    }
}
