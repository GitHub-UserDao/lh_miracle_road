package dev.lhkongyu.lhmiracleroad.packet;

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

}
