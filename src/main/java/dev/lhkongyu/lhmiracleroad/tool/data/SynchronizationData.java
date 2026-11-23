package dev.lhkongyu.lhmiracleroad.tool.data;

import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.packet.ClientDataMessage;
import dev.lhkongyu.lhmiracleroad.packet.InitItemPreviewMessage;
import dev.lhkongyu.lhmiracleroad.packet.OccupationCatalogMessage;
import dev.lhkongyu.lhmiracleroad.packet.PlayerChannel;
import net.minecraft.server.level.ServerPlayer;

public class SynchronizationData {

    /**
     * 将服务端配置同步至客户端
     */
    public static void synchronization(ServerPlayer player) {
        InitItemPreviewMessage.sendAll(player);
        OccupationCatalogMessage.sendAll(player);
        JsonObject equipment = DataCompressTool.equipmentDataCompress();
        JsonObject clearEquipment = new JsonObject();
        clearEquipment.addProperty("key", "clearEquipment");
        var config = LHMiracleRoadConfig.COMMON;
        clearEquipment.addProperty("weightEnabled", config.ENABLE_EQUIPMENT_WEIGHT.get());
        clearEquipment.addProperty("offhandWeightEnabled", config.IS_OFFHAND_CALCULATE_HEAVY.get());
        clearEquipment.addProperty("attributePenaltiesEnabled", config.IS_SKILL_POINTS_RESTRICT.get());
        PlayerChannel.sendToClient(new ClientDataMessage(clearEquipment), player);
        for (String key : equipment.keySet()) {
            JsonObject equipmentData = new JsonObject();
            JsonObject data = new JsonObject();
            data.add(key,equipment.get(key));
            equipmentData.addProperty("key", "modEquipment");
            equipmentData.add("data", data);
            ClientDataMessage equipmentMessage = new ClientDataMessage(equipmentData);
            PlayerChannel.sendToClient(equipmentMessage, player);
        }
    }
}
