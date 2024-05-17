package dev.lhkongyu.lhmiracleroad.packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import dev.lhkongyu.lhmiracleroad.tool.data.DataCompressTool;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public record ClientDataMessage(JsonObject data) {
    public ClientDataMessage(FriendlyByteBuf buf){
        this(getPlayerOccupationAttributeObject(buf));
    }

    private static JsonObject getPlayerOccupationAttributeObject(FriendlyByteBuf buf){
        String jsonStr = buf.readUtf();
        return JsonParser.parseString(jsonStr).getAsJsonObject();
    }
    public void encode(FriendlyByteBuf buf){
        buf.writeUtf(data.toString());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            String key = LHMiracleRoadTool.isAsString(data.get("key"));
            switch (key){
                case "modEquipment" -> modEquipmentManage(LHMiracleRoadTool.isAsJsonObject(data.get("data")));
                case "attributePointsRewards" -> attributePointsRewardsManage(LHMiracleRoadTool.isAsJsonObject(data.get("data")));
                case "attributeTypes" -> attributeTypesManage(LHMiracleRoadTool.isAsJsonObject(data.get("data")));
                case "occupation" -> occupationManage(LHMiracleRoadTool.isAsJsonArray(data.get("data")));
                case "showGuiAttribute" -> showGuiAttributeManage(LHMiracleRoadTool.isAsJsonArray(data.get("data")));
                case "initItem" -> initItemManage(LHMiracleRoadTool.isAsJsonObject(data.get("data")));
                case "showAttribute" -> showAttributeManage(LHMiracleRoadTool.isAsJsonObject(data.get("data")));
            }
        });
        context.setPacketHandled(true);
    }

    private void modEquipmentManage(JsonObject equipment){
        if (equipment != null) {
            for (Map.Entry<String, JsonElement> jsonElement : equipment.entrySet()) {
                if (ClientData.EQUIPMENT.get(jsonElement.getKey()) != null) {
                    ClientData.EQUIPMENT.remove(jsonElement.getKey());
                }
                Map<String, JsonObject> equipmentMap = DataCompressTool.equipmentDataRestore(LHMiracleRoadTool.isAsJsonObject(jsonElement.getValue()));
                ClientData.EQUIPMENT.put(jsonElement.getKey(), equipmentMap);
            }
        }
    }

    private void attributePointsRewardsManage(JsonObject attributePointsRewards){
        if (attributePointsRewards != null) {
            ClientData.ATTRIBUTE_POINTS_REWARDS.clear();
            for (Map.Entry<String, JsonElement> jsonElement : attributePointsRewards.entrySet()) {
                JsonObject attributePointsRewardsObj = DataCompressTool.attributePointsRewardsDataRestore(LHMiracleRoadTool.isAsJsonObject(jsonElement.getValue()));
                ClientData.ATTRIBUTE_POINTS_REWARDS.put(jsonElement.getKey(), attributePointsRewardsObj);
            }

            //从 ATTRIBUTE_POINTS_REWARDS 里获取 POINTS_REWARDS
            ClientData.POINTS_REWARDS.clear();
            for (String key : ClientData.ATTRIBUTE_POINTS_REWARDS.keySet()) {
                JsonObject data = ClientData.ATTRIBUTE_POINTS_REWARDS.get(key);
                for (JsonElement pointsRewardElement : LHMiracleRoadTool.isAsJsonArray(data.get("points_rewards"))) {
                    JsonObject pointsRewardObj = pointsRewardElement.getAsJsonObject();
                    String attributeName = LHMiracleRoadTool.isAsString(pointsRewardObj.get("attribute"));
                    ClientData.POINTS_REWARDS.put(attributeName, pointsRewardObj);
                }
            }
        }
    }

    private void attributeTypesManage(JsonObject attributeTypes){
        if (attributeTypes != null) {
            ClientData.ATTRIBUTE_TYPES.clear();
            for (Map.Entry<String, JsonElement> jsonElement : attributeTypes.entrySet()) {
                JsonArray attributeTypesArr = DataCompressTool.attributeTypesDataRestore(LHMiracleRoadTool.isAsJsonArray(jsonElement.getValue()));
                ClientData.ATTRIBUTE_TYPES.put(jsonElement.getKey(), attributeTypesArr);
            }
        }
    }

    private void occupationManage(JsonArray occupation){
        if (occupation != null && !occupation.isEmpty()) {
            ClientData.OCCUPATION.clear();
            for (JsonElement jsonElement : occupation) {
                JsonObject object = DataCompressTool.occupationDataRestore(jsonElement.getAsJsonObject());
                ClientData.OCCUPATION.add(object);
            }
        }
    }

    private void showGuiAttributeManage(JsonArray showGuiAttribute){
        if (showGuiAttribute != null && !showGuiAttribute.isEmpty()) {
            ClientData.SHOW_GUI_ATTRIBUTE.clear();
            for (JsonElement jsonElement : showGuiAttribute) {
                JsonObject object = DataCompressTool.showGuiAttributeDataRestore(jsonElement.getAsJsonObject());
                ClientData.SHOW_GUI_ATTRIBUTE.add(object);
            }
        }
    }

    private void initItemManage(JsonObject initItem){
        if (initItem != null) {
            ClientData.INIT_ITEM.clear();
            for (Map.Entry<String, JsonElement> jsonElement : initItem.entrySet()) {
                JsonArray initItemObj = DataCompressTool.initItemDataRestore(LHMiracleRoadTool.isAsJsonArray(jsonElement.getValue()));
                ClientData.INIT_ITEM.put(jsonElement.getKey(), initItemObj);
            }
        }
    }

    private void showAttributeManage(JsonObject showAttribute){
        if (showAttribute != null) {
            ClientData.SHOW_ATTRIBUTE.clear();
            for (Map.Entry<String, JsonElement> jsonElement : showAttribute.entrySet()) {
                ClientData.SHOW_ATTRIBUTE.put(jsonElement.getKey(), LHMiracleRoadTool.isAsJsonObject(jsonElement.getValue()));
            }
        }
    }
}
