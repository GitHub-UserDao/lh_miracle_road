package dev.lhkongyu.lhmiracleroad.tool.data;

import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.packet.ClientDataMessage;
import dev.lhkongyu.lhmiracleroad.packet.InitItemPreviewMessage;
import dev.lhkongyu.lhmiracleroad.packet.OccupationCatalogMessage;
import dev.lhkongyu.lhmiracleroad.packet.PlayerChannel;
import net.minecraft.server.level.ServerPlayer;

public class SynchronizationData {

    /**
     * 将服务端数据包文件发送至客户端
     */
    public static void synchronization(ServerPlayer player) {
        InitItemPreviewMessage.sendAll(player);
        OccupationCatalogMessage.sendAll(player);
        //同步EquipmentReloadListener类的数据包
        JsonObject equipment = DataCompressTool.equipmentDataCompress();
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
