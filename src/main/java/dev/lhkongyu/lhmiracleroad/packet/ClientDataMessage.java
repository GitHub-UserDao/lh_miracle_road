package dev.lhkongyu.lhmiracleroad.packet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.config.SoulItemConfig;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import dev.lhkongyu.lhmiracleroad.tool.data.DataCompressTool;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
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
                case "soulItems" -> ClientData.setSoulRewards(SoulItemConfig.fromJson(LHMiracleRoadTool.isAsJsonObject(data.get("data"))));
            }
        });
        context.setPacketHandled(true);
    }

    public static void installEquipmentSnapshot(JsonObject snapshot) {
        Map<String, Map<String, JsonObject>> profiles = new HashMap<>();
        for (var entry : snapshot.getAsJsonObject("data").entrySet()) {
            profiles.put(entry.getKey(), DataCompressTool.equipmentDataRestore(entry.getValue().getAsJsonObject()));
        }
        var config = LHMiracleRoadConfig.COMMON;
        ClientData.setOccupationLevelBase(snapshot.get("occupationLevelBase").getAsInt());
        ClientData.resetEquipment(new ClientData.EquipmentRules(
                equipmentFlag(snapshot, "weightEnabled", config.ENABLE_EQUIPMENT_WEIGHT.get()),
                equipmentFlag(snapshot, "offhandWeightEnabled", config.IS_OFFHAND_CALCULATE_HEAVY.get()),
                equipmentFlag(snapshot, "attributePenaltiesEnabled", config.IS_SKILL_POINTS_RESTRICT.get())));
        ClientData.EQUIPMENT.putAll(profiles);
    }

    private static boolean equipmentFlag(JsonObject snapshot, String key, boolean fallback) {
        return snapshot.has(key) ? snapshot.get(key).getAsBoolean() : fallback;
    }

}
