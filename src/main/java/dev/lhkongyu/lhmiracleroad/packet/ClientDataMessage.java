package dev.lhkongyu.lhmiracleroad.packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import dev.lhkongyu.lhmiracleroad.data.reloader.*;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
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
            JsonObject attributePointsRewards = LHMiracleRoadTool.isAsJsonObject(data.get("attributePointsRewards"));
            if (attributePointsRewards != null) {
                ClientData.ATTRIBUTE_POINTS_REWARDS.clear();
                for (Map.Entry<String, JsonElement> jsonElement : attributePointsRewards.entrySet()) {
                    ClientData.ATTRIBUTE_POINTS_REWARDS.put(jsonElement.getKey(), LHMiracleRoadTool.isAsJsonObject(jsonElement.getValue()));
                }
            }

            JsonObject pointsRewards = LHMiracleRoadTool.isAsJsonObject(data.get("pointsRewards"));
            if (pointsRewards != null) {
                ClientData.POINTS_REWARDS.clear();
                for (Map.Entry<String, JsonElement> jsonElement : pointsRewards.entrySet()) {
                    ClientData.POINTS_REWARDS.put(jsonElement.getKey(), LHMiracleRoadTool.isAsJsonObject(jsonElement.getValue()));
                }
            }

            JsonObject attributeTypes = LHMiracleRoadTool.isAsJsonObject(data.get("attributeTypes"));
            if (attributeTypes != null) {
                ClientData.ATTRIBUTE_TYPES.clear();
                for (Map.Entry<String, JsonElement> jsonElement : attributeTypes.entrySet()) {
                    ClientData.ATTRIBUTE_TYPES.put(jsonElement.getKey(), LHMiracleRoadTool.isAsJsonObject(jsonElement.getValue()));
                }
            }

            JsonObject equipment = LHMiracleRoadTool.isAsJsonObject(data.get("equipment"));
            if (equipment != null) {
                ClientData.EQUIPMENT.clear();
                for (Map.Entry<String, JsonElement> jsonElement : equipment.entrySet()) {
                    ClientData.EQUIPMENT.put(jsonElement.getKey(), LHMiracleRoadTool.isAsJsonObject(jsonElement.getValue()));
                }
            }

            JsonArray occupation = LHMiracleRoadTool.isAsJsonArray(data.get("occupation"));
            if (occupation != null && !occupation.isEmpty()) {
                ClientData.OCCUPATION.clear();
                for (JsonElement jsonElement : occupation) {
                    JsonObject object = jsonElement.getAsJsonObject();
                    ClientData.OCCUPATION.add(object);
                }
            }

            JsonArray showGuiAttribute = LHMiracleRoadTool.isAsJsonArray(data.get("showGuiAttribute"));
            if (showGuiAttribute != null && !showGuiAttribute.isEmpty()) {
                ClientData.SHOW_GUI_ATTRIBUTE.clear();
                for (JsonElement jsonElement : showGuiAttribute) {
                    JsonObject object = jsonElement.getAsJsonObject();
                    ClientData.SHOW_GUI_ATTRIBUTE.add(object);
                }
            }

            JsonObject initItem = LHMiracleRoadTool.isAsJsonObject(data.get("initItem"));
            if (initItem != null) {
                ClientData.INIT_ITEM.clear();
                for (Map.Entry<String, JsonElement> jsonElement : initItem.entrySet()) {
                    ClientData.INIT_ITEM.put(jsonElement.getKey(), LHMiracleRoadTool.isAsJsonArray(jsonElement.getValue()));
                }
            }

            JsonObject showAttribute = LHMiracleRoadTool.isAsJsonObject(data.get("showAttribute"));
            if (showAttribute != null) {
                ClientData.SHOW_ATTRIBUTE.clear();
                for (Map.Entry<String, JsonElement> jsonElement : showAttribute.entrySet()) {
                    ClientData.SHOW_ATTRIBUTE.put(jsonElement.getKey(), LHMiracleRoadTool.isAsJsonObject(jsonElement.getValue()));
                }
            }
        });
        context.setPacketHandled(true);
    }
}
