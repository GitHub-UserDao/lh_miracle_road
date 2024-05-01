package dev.lhkongyu.lhmiracleroad.tool;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.data.reloader.AttributePointsRewardsReloadListener;
import dev.lhkongyu.lhmiracleroad.data.reloader.OccupationReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerAttributeTool {

    public static void initOccupation(ServerPlayer player,String occupationId){
        JsonObject occupation = LHMiracleRoadTool.getOccupation(occupationId);
        if (occupation == null) return;
        //获取职业基本信息
        int initDifficultyLevel = LHMiracleRoadTool.isAsInt(occupation.get("init_difficulty_level"));
        Map<String,Integer> initAttributeLevel = LHMiracleRoadTool.setInitAttributeLevel(occupation);
        List<JsonObject> items = LHMiracleRoadTool.setInitItem(occupation);
        PlayerOccupationAttribute playerOccupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve().get();
        LHMiracleRoadTool.setConfigBaseAttribute(player,initDifficultyLevel);
        calculateAttribute(player,initAttributeLevel,occupationId,playerOccupationAttribute);
        initItem(player,items);
        playerOccupationAttribute.setShowAttribute(LHMiracleRoadTool.setShowAttribute(player));
        LHMiracleRoadTool.playerPunishmentStateUpdate(player,playerOccupationAttribute);

        //第三版
//        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
//        map.put(Attributes.MAX_HEALTH,new AttributeModifier(UUID.randomUUID(),"",2,AttributeModifier.Operation.ADDITION));
//        player.getAttributes().addTransientAttributeModifiers(map);
        //第二版
//        AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
//        attributeInstance.addTransientModifier(new AttributeModifier(UUID.randomUUID(),"",2,AttributeModifier.Operation.ADDITION));
//        attributeInstance.removeModifiers();
//        Double value = attributeInstance.getValue();
        //第一版 指令去设置
//        MinecraftServer server = player.getServer();
//        if (server == null) return;
//        String command = String.format("attribute @s epicfight:staminar base set %s",20);
//        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);


    }

    public static void calculateAttribute(ServerPlayer player, Map<String,Integer> initAttributeLevel,String occupationId,PlayerOccupationAttribute playerOccupationAttribute){
        int level = 0;
        for (String key : initAttributeLevel.keySet()) {
            JsonObject jsonObject = AttributePointsRewardsReloadListener.ATTRIBUTE_POINTS_REWARDS.get(key);
            setAttribute(player, jsonObject, initAttributeLevel.get(key),playerOccupationAttribute);
            level += initAttributeLevel.get(key);
        }
        level = (level - initAttributeLevel.size() * LHMiracleRoadConfig.COMMON.LEVEL_BASE.get()) + 1;
        playerOccupationAttribute.setOccupationId(occupationId);
        playerOccupationAttribute.setOccupationAttributeLevel(initAttributeLevel);
        playerOccupationAttribute.setOccupationLevel(level);
        playerOccupationAttribute.setEmpiricalCalculationFormula(LHMiracleRoadConfig.COMMON.EMPIRICAL_CALCULATION_FORMULA.get());
    }

    private static void setAttribute(ServerPlayer player, JsonObject jsonObject, int attributeLevel, PlayerOccupationAttribute playerOccupationAttribute){
        for (JsonElement jsonElement : LHMiracleRoadTool.isAsJsonArray(jsonObject.get("points_rewards"))) {
            JsonObject object = jsonElement.getAsJsonObject();
            String attributeName = LHMiracleRoadTool.isAsString(object.get("attribute"));
            double value = LHMiracleRoadTool.isAsDouble(object.get("value"));
            if (value == 0) continue;
            double levelPromote = LHMiracleRoadTool.isAsDouble(object.get("level_promote"));
            int levelPromoteValue = LHMiracleRoadTool.isAsInt(object.get("level_promote"));
            String operationString = LHMiracleRoadTool.isAsString(object.get("operation"));
            double attributeValue = LHMiracleRoadTool.calculateTotalIncrease(attributeLevel,value,levelPromote,levelPromoteValue);
            addExtraAttributes(player,attributeName,attributeValue,operationString,playerOccupationAttribute);
        }
    }

    private static void addExtraAttributes(ServerPlayer player,String attributeName,double attributeValue,String operationString, PlayerOccupationAttribute playerOccupationAttribute){
        AttributeModifier.Operation operation = LHMiracleRoadTool.stringConversionOperation(operationString);
        if (operation == null) return;
        Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(attributeName);
        if (attribute != null){
            AttributeModifier attributeModifier = new AttributeModifier(UUID.randomUUID(), "", attributeValue, operation);
            player.getAttribute(attribute).addTransientModifier(attributeModifier);
            if (attribute.getDescriptionId().equals(Attributes.MAX_HEALTH.getDescriptionId())){
                player.setHealth((float) player.getAttribute(attribute).getValue());
            }
            playerOccupationAttribute.addAttributeModifier(attributeName,attributeModifier);
        }
        //老版代码
//        else {
//            Attribute instanceAttribute = ForgeRegistries.ATTRIBUTES.getValues()
//                    .stream()
//                    .filter(p-> p.getDescriptionId().equals(attributeName) || LHMiracleRoadTool.isAttributeName(p.getDescriptionId(),attributeName))
//                    .findFirst()
//                    .orElse(null);
//            if (instanceAttribute == null) return;
//            AttributePointsRewardsReloadListener.recordAttribute.put(attributeName,instanceAttribute);
//            player.getAttribute(instanceAttribute).addTransientModifier(new AttributeModifier(UUID.randomUUID(), "", attributeValue, operation));
//
//            player.getAttributes().getSyncableAttributes().forEach(attributeInstance -> {
//                Attribute instanceAttribute = attributeInstance.getAttribute();
//                if (instanceAttribute.getDescriptionId().equals(attributeName) || LHMiracleRoadTool.isAttributeName(instanceAttribute.getDescriptionId(),attributeName)) {
//                    LHMiracleRoad.recordAttribute.put(attributeName,instanceAttribute);
//                    attributeInstance.addTransientModifier(new AttributeModifier(UUID.randomUUID(), "", attributeValue, operation));
//                }
//            });
//        }
    }

    private static void initItem(ServerPlayer player,List<JsonObject> initItem){
        for (JsonObject object :initItem){
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(LHMiracleRoadTool.isAsString(object.get("item"))));
            if (item == null) continue;
            int quantity = LHMiracleRoadTool.isAsInt(object.get("quantity"));
            Boolean isSplit = LHMiracleRoadTool.isAsBoolean(object.get("is_split"));
            ItemStack itemStack = null;
            JsonObject tag = LHMiracleRoadTool.isAsJsonObject(object.get("tag"));
            if (isSplit){
                itemStack = new ItemStack(item, 1);
                LHMiracleRoadTool.setTag(itemStack,tag);
                for (int i = 0; i < quantity; i++) {
                    addItemStack(player,itemStack.copy());
                }
            }else {
                itemStack = new ItemStack(item, quantity);
                LHMiracleRoadTool.setTag(itemStack,tag);
                addItemStack(player, itemStack);
            }
        }
    }

    private static void addItemStack(ServerPlayer player,ItemStack itemStack){
        boolean wasAdded = player.getInventory().add(itemStack);
        if (!wasAdded) {
            player.drop(itemStack, false);
        }
    }
}
