package dev.lhkongyu.lhmiracleroad.tool.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.config.punishment.PunishmentConfig;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;

import java.util.Map;

public class DataCompressTool {


    /**
     * 压缩装备数据包
     */
    public static JsonObject equipmentDataCompress() {
        JsonObject equipment = new JsonObject();
        for (var entry : PunishmentConfig.getEquipment().entrySet()) {
            String key = entry.getKey();
            JsonObject equipmentNew = new JsonObject();

            Map<String,JsonObject> equipmentMap = entry.getValue();
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

}
