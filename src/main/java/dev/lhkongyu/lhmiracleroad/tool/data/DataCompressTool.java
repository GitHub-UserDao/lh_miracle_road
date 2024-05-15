package dev.lhkongyu.lhmiracleroad.tool.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.data.reloader.*;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;

import java.util.Map;

public class DataCompressTool {


    /**
     * 压缩装备数据包
     */
    public static JsonObject equipmentDataCompress() {
        JsonObject equipment = new JsonObject();
        for (String key : EquipmentReloadListener.EQUIPMENT.keySet()){
            JsonObject equipmentNew = new JsonObject();

            Map<String,JsonObject> equipmentMap = EquipmentReloadListener.EQUIPMENT.get(key);
            for (String equipmentMapKey :equipmentMap.keySet()) {
                JsonObject equipmentObjNew = new JsonObject();
                equipmentObjNew.addProperty("h", LHMiracleRoadTool.isAsInt(equipmentMap.get(equipmentMapKey).get("heavy")));

                JsonArray attributeNeed = LHMiracleRoadTool.isAsJsonArray(equipmentMap.get(equipmentMapKey).get("attribute_need"));
                if (attributeNeed != null && !attributeNeed.isEmpty()) {
                    JsonArray attributeNeedNew = new JsonArray();
                    for (JsonElement jsonElement : attributeNeed) {
                        JsonObject object = LHMiracleRoadTool.isAsJsonObject(jsonElement);
                        JsonObject attributeNeedNewObj = new JsonObject();
                        attributeNeedNewObj.addProperty("i", LHMiracleRoadTool.isAsString(object.get("attribute_id")));
                        attributeNeedNewObj.addProperty("n", LHMiracleRoadTool.isAsInt(object.get("need_points")));
                        String pid = LHMiracleRoadTool.isAsString(object.get("punishment_id"));
                        if (pid != null) attributeNeedNewObj.addProperty("p", pid);
                        attributeNeedNew.add(attributeNeedNewObj);
                    }
                    equipmentObjNew.add("a", attributeNeedNew);
                }
                equipmentNew.add(equipmentMapKey,equipmentObjNew);
            }
            equipment.add(key,equipmentNew);
        }
        return equipment;
    }

    /**
     * 还原装备数据包
     */
    public static Map<String,JsonObject> equipmentDataRestore(JsonObject equipment) {
        Map<String,JsonObject> equipmentMap = Maps.newHashMap();
        for (String key : equipment.keySet()) {
            JsonObject equipmentObj = LHMiracleRoadTool.isAsJsonObject(equipment.get(key));

            JsonObject equipmentObjNew = new JsonObject();
            equipmentObjNew.addProperty("heavy", LHMiracleRoadTool.isAsInt(equipmentObj.get("h")));
            JsonArray attributeNeed = LHMiracleRoadTool.isAsJsonArray(equipmentObj.get("a"));
            if (attributeNeed != null && !attributeNeed.isEmpty()) {
                JsonArray attributeNeedNew = new JsonArray();
                for (JsonElement jsonElement : attributeNeed) {
                    JsonObject object = LHMiracleRoadTool.isAsJsonObject(jsonElement);
                    JsonObject attributeNeedNewObj = new JsonObject();
                    attributeNeedNewObj.addProperty("attribute_id", LHMiracleRoadTool.isAsString(object.get("i")));
                    attributeNeedNewObj.addProperty("need_points", LHMiracleRoadTool.isAsInt(object.get("n")));
                    String pid = LHMiracleRoadTool.isAsString(object.get("p"));
                    if (pid != null) attributeNeedNewObj.addProperty("punishment_id", pid);
                    attributeNeedNew.add(attributeNeedNewObj);
                }
                equipmentObjNew.add("attribute_need", attributeNeedNew);
            }
            equipmentMap.put(key,equipmentObjNew);
        }
        return equipmentMap;
    }

    /**
     * 压缩属性奖励数据包
     */
    public static JsonObject attributePointsRewardsDataCompress() {
        JsonObject attributePointsRewards = new JsonObject();
        for (String key :  AttributePointsRewardsReloadListener.ATTRIBUTE_POINTS_REWARDS.keySet()){
            JsonObject data = AttributePointsRewardsReloadListener.ATTRIBUTE_POINTS_REWARDS.get(key);
            JsonObject dataNew = new JsonObject();

            JsonArray pointsRewards = LHMiracleRoadTool.isAsJsonArray(data.get("points_rewards"));
            JsonArray pointsRewardsNew = new JsonArray();
            for (JsonElement element : pointsRewards){
                JsonObject pointsRewardsObj = LHMiracleRoadTool.isAsJsonObject(element);
                JsonObject pointsRewardsNewObj = new JsonObject();
                pointsRewardsNewObj.addProperty("a",LHMiracleRoadTool.isAsString(pointsRewardsObj.get("attribute")));
                pointsRewardsNewObj.addProperty("v",LHMiracleRoadTool.isAsDouble(pointsRewardsObj.get("value")));
                pointsRewardsNewObj.addProperty("o",LHMiracleRoadTool.isAsString(pointsRewardsObj.get("operation")));

                double level_promote = LHMiracleRoadTool.isAsDouble(pointsRewardsObj.get("level_promote"));
                if (level_promote != 0) {
                    pointsRewardsNewObj.addProperty("p",level_promote);
                }
                double level_promote_value = LHMiracleRoadTool.isAsDouble(pointsRewardsObj.get("level_promote_value"));
                if (level_promote_value != 0) {
                    pointsRewardsNewObj.addProperty("pv",level_promote_value);
                }
                double min = LHMiracleRoadTool.isAsDouble(pointsRewardsObj.get("min"));
                if (min != 0) {
                    pointsRewardsNewObj.addProperty("m", min);
                }
                pointsRewardsNew.add(pointsRewardsNewObj);
            }
            dataNew.addProperty("m",LHMiracleRoadTool.isAsInt(data.get("max_level")));
            dataNew.add("p",pointsRewardsNew);
            attributePointsRewards.add(key,dataNew);
        }
        return attributePointsRewards;
    }

    /**
     * 还原奖励属性数据包
     */
    public static JsonObject attributePointsRewardsDataRestore(JsonObject attributePointsRewards) {
        JsonObject dataNew = new JsonObject();

        JsonArray pointsRewards = LHMiracleRoadTool.isAsJsonArray(attributePointsRewards.get("p"));
        JsonArray pointsRewardsNew = new JsonArray();
        for (JsonElement element : pointsRewards){
            JsonObject pointsRewardsObj = LHMiracleRoadTool.isAsJsonObject(element);
            JsonObject pointsRewardsNewObj = new JsonObject();
            pointsRewardsNewObj.addProperty("attribute",LHMiracleRoadTool.isAsString(pointsRewardsObj.get("a")));
            pointsRewardsNewObj.addProperty("value",LHMiracleRoadTool.isAsDouble(pointsRewardsObj.get("v")));
            pointsRewardsNewObj.addProperty("level_promote",LHMiracleRoadTool.isAsInt(pointsRewardsObj.get("p")));
            pointsRewardsNewObj.addProperty("level_promote_value",LHMiracleRoadTool.isAsDouble(pointsRewardsObj.get("pv")));
            pointsRewardsNewObj.addProperty("operation",LHMiracleRoadTool.isAsString(pointsRewardsObj.get("o")));
            pointsRewardsNewObj.addProperty("min",LHMiracleRoadTool.isAsDouble(pointsRewardsObj.get("m")));
            pointsRewardsNew.add(pointsRewardsNewObj);
        }
        dataNew.addProperty("max_level",LHMiracleRoadTool.isAsInt(attributePointsRewards.get("m")));
        dataNew.add("points_rewards",pointsRewardsNew);
        return dataNew;
    }

    /**
     * 压缩属性类型数据包
     */
    public static JsonObject attributeTypesDataCompress() {
        JsonObject attributePointsRewards = new JsonObject();
        for (String key :  AttributeReloadListener.ATTRIBUTE_TYPES.keySet()){
            JsonArray describes = AttributeReloadListener.ATTRIBUTE_TYPES.get(key);
            JsonArray describesNew = new JsonArray();
            for (JsonElement element : describes){
                JsonObject obj = LHMiracleRoadTool.isAsJsonObject(element);
                JsonObject newObj = new JsonObject();
                newObj.addProperty("a",LHMiracleRoadTool.isAsString(obj.get("attribute")));
                newObj.addProperty("d",LHMiracleRoadTool.isAsString(obj.get("describe")));
                if (obj.get("condition") != null) {
                    newObj.addProperty("c",LHMiracleRoadTool.isAsString(obj.get("condition")));
                }
                describesNew.add(newObj);
            }
            attributePointsRewards.add(key,describesNew);
        }
        return attributePointsRewards;
    }

    /**
     * 还原属性类型数据包
     */
    public static JsonArray attributeTypesDataRestore(JsonArray describes) {
        JsonArray describesNew = new JsonArray();
        for (JsonElement element : describes){
            JsonObject obj = LHMiracleRoadTool.isAsJsonObject(element);
            JsonObject newObj = new JsonObject();
            newObj.addProperty("attribute",LHMiracleRoadTool.isAsString(obj.get("a")));
            newObj.addProperty("describe",LHMiracleRoadTool.isAsString(obj.get("d")));
            if (obj.get("c") != null) {
                newObj.addProperty("condition",LHMiracleRoadTool.isAsString(obj.get("c")));
            }
            describesNew.add(newObj);
        }
        return describesNew;
    }

    /**
     * 压缩初始装备数据包
     */
    public static JsonObject initItemDataCompress() {
        JsonObject attributePointsRewards = new JsonObject();
        for (String key : InitItemResourceReloadListener.INIT_ITEM.keySet()){
            JsonArray items = InitItemResourceReloadListener.INIT_ITEM.get(key);
            JsonArray itemsNew = new JsonArray();
            for (JsonElement element : items){
                JsonObject obj = LHMiracleRoadTool.isAsJsonObject(element);
                JsonObject newObj = new JsonObject();
                newObj.addProperty("i",LHMiracleRoadTool.isAsString(obj.get("item")));
                newObj.addProperty("q",LHMiracleRoadTool.isAsInt(obj.get("quantity")));
                if (obj.get("tag") != null) {
                    newObj.addProperty("t",LHMiracleRoadTool.isAsString(obj.get("tag")));
                }
                if (obj.get("is_split") != null) {
                    newObj.addProperty("s",LHMiracleRoadTool.isAsBoolean(obj.get("is_split")));
                }
                itemsNew.add(newObj);
            }
            attributePointsRewards.add(key,itemsNew);
        }
        return attributePointsRewards;
    }

    /**
     * 还原初始装备数据包
     */
    public static JsonArray initItemDataRestore(JsonArray items) {
        JsonArray itemsNew = new JsonArray();
        for (JsonElement element : items){
            JsonObject obj = LHMiracleRoadTool.isAsJsonObject(element);
            JsonObject newObj = new JsonObject();
            newObj.addProperty("item",LHMiracleRoadTool.isAsString(obj.get("i")));
            newObj.addProperty("quantity",LHMiracleRoadTool.isAsInt(obj.get("q")));
            if (obj.get("t") != null) {
                newObj.addProperty("tag",LHMiracleRoadTool.isAsString(obj.get("t")));
            }
            if (obj.get("s") != null) {
                newObj.addProperty("is_split",LHMiracleRoadTool.isAsBoolean(obj.get("s")));
            }
            itemsNew.add(newObj);
        }
        return itemsNew;
    }

    /**
     * 压缩职业数据包
     */
    public static JsonArray occupationDataCompress() {
        JsonArray occupation = new JsonArray();
        for (JsonObject occupationObj : OccupationReloadListener.OCCUPATION){
            JsonObject occupationNew = new JsonObject();

            JsonArray initAttribute = LHMiracleRoadTool.isAsJsonArray(occupationObj.get("init_attribute"));
            JsonArray initAttributeNew = new JsonArray();
            for (JsonElement element : initAttribute){
                JsonObject obj = LHMiracleRoadTool.isAsJsonObject(element);
                JsonObject newObj = new JsonObject();
                newObj.addProperty("i",LHMiracleRoadTool.isAsString(obj.get("id")));
                newObj.addProperty("l",LHMiracleRoadTool.isAsInt(obj.get("level")));
                initAttributeNew.add(newObj);
            }

            occupationNew.addProperty("i",LHMiracleRoadTool.isAsString(occupationObj.get("id")));
            occupationNew.addProperty("d",LHMiracleRoadTool.isAsInt(occupationObj.get("init_difficulty_level")));
            occupationNew.add("a",initAttributeNew);
            occupation.add(occupationNew);
        }
        return occupation;
    }

    /**
     * 还原职业数据包
     */
    public static JsonObject occupationDataRestore(JsonObject occupation) {
        JsonObject occupationNew = new JsonObject();

        JsonArray initAttribute = LHMiracleRoadTool.isAsJsonArray(occupation.get("a"));
        JsonArray initAttributeNew = new JsonArray();
        for (JsonElement element : initAttribute){
            JsonObject obj = LHMiracleRoadTool.isAsJsonObject(element);
            JsonObject newObj = new JsonObject();
            newObj.addProperty("id",LHMiracleRoadTool.isAsString(obj.get("i")));
            newObj.addProperty("level",LHMiracleRoadTool.isAsInt(obj.get("l")));
            initAttributeNew.add(newObj);
        }

        occupationNew.addProperty("id",LHMiracleRoadTool.isAsString(occupation.get("i")));
        occupationNew.addProperty("init_difficulty_level",LHMiracleRoadTool.isAsInt(occupation.get("d")));
        occupationNew.add("init_attribute",initAttributeNew);
        return occupationNew;
    }

    /**
     * 压缩属性样式数据包
     */
    public static JsonArray showGuiAttributeDataCompress() {
        JsonArray showGuiAttribute = new JsonArray();
        for (JsonObject showGuiAttributeObj : ShowGuiAttributeReloadListener.SHOW_GUI_ATTRIBUTE){
            JsonObject showGuiAttributeNew = new JsonObject();
            showGuiAttributeNew.addProperty("a",LHMiracleRoadTool.isAsString(showGuiAttributeObj.get("attribute")));
            showGuiAttributeNew.addProperty("t",LHMiracleRoadTool.isAsString(showGuiAttributeObj.get("attribute_text")));
            showGuiAttributeNew.addProperty("s",LHMiracleRoadTool.isAsString(showGuiAttributeObj.get("show_value_type")));
            if (showGuiAttributeObj.get("condition") != null) {
                showGuiAttributeNew.addProperty("c", LHMiracleRoadTool.isAsString(showGuiAttributeObj.get("condition")));
            }
            showGuiAttribute.add(showGuiAttributeNew);
        }
        return showGuiAttribute;
    }

    /**
     * 还原属性样式数据包
     */
    public static JsonObject showGuiAttributeDataRestore(JsonObject showGuiAttribute) {
        JsonObject showGuiAttributeNew = new JsonObject();

        showGuiAttributeNew.addProperty("attribute",LHMiracleRoadTool.isAsString(showGuiAttribute.get("a")));
        showGuiAttributeNew.addProperty("attribute_text",LHMiracleRoadTool.isAsString(showGuiAttribute.get("t")));
        showGuiAttributeNew.addProperty("show_value_type",LHMiracleRoadTool.isAsString(showGuiAttribute.get("s")));
        if (showGuiAttribute.get("c") != null) {
            showGuiAttributeNew.addProperty("condition", LHMiracleRoadTool.isAsString(showGuiAttribute.get("c")));
        }
        return showGuiAttributeNew;
    }
}
