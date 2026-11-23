package dev.lhkongyu.lhmiracleroad.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.config.SoulItemConfig;
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
                case "clearEquipment" -> {
                    var config = LHMiracleRoadConfig.COMMON;
                    ClientData.resetEquipment(new ClientData.EquipmentRules(
                            equipmentFlag("weightEnabled", config.ENABLE_EQUIPMENT_WEIGHT.get()),
                            equipmentFlag("offhandWeightEnabled", config.IS_OFFHAND_CALCULATE_HEAVY.get()),
                            equipmentFlag("attributePenaltiesEnabled", config.IS_SKILL_POINTS_RESTRICT.get())));
                }
                case "modEquipment" -> modEquipmentManage(LHMiracleRoadTool.isAsJsonObject(data.get("data")));
                case "soulItems" -> ClientData.setSoulRewards(SoulItemConfig.fromJson(LHMiracleRoadTool.isAsJsonObject(data.get("data"))));
            }
        });
        context.setPacketHandled(true);
    }

    private boolean equipmentFlag(String key, boolean fallback) {
        return data.has(key) ? data.get(key).getAsBoolean() : fallback;
    }

    private void modEquipmentManage(JsonObject equipment){
        if (equipment != null) {
            for (Map.Entry<String, JsonElement> jsonElement : equipment.entrySet()) {
                Map<String, JsonObject> equipmentMap = DataCompressTool.equipmentDataRestore(LHMiracleRoadTool.isAsJsonObject(jsonElement.getValue()));
                ClientData.EQUIPMENT.put(jsonElement.getKey(), equipmentMap);
            }
        }
    }

}
