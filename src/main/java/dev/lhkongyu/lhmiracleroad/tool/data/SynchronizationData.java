package dev.lhkongyu.lhmiracleroad.tool.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.data.reloader.*;
import dev.lhkongyu.lhmiracleroad.packet.ClientDataMessage;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributeChannel;
import dev.lhkongyu.lhmiracleroad.tool.data.DataCompressTool;
import net.minecraft.server.level.ServerPlayer;

public class SynchronizationData {

    /**
     * 将服务端数据包文件发送至客户端
     */
    public static void synchronization(ServerPlayer player) {
        //同步EquipmentReloadListener类的数据包
        JsonObject equipment = DataCompressTool.equipmentDataCompress();
        for (String key : equipment.keySet()) {
            JsonObject equipmentData = new JsonObject();
            JsonObject data = new JsonObject();
            data.add(key,equipment.get(key));
            equipmentData.add("modEquipment", data);
            ClientDataMessage equipmentMessage = new ClientDataMessage(equipmentData);
            PlayerAttributeChannel.sendToClient(equipmentMessage, player);
        }

        //将服务端数据包同步至客户端
        //同步AttributePointsRewardsReloadListener类的数据包
        JsonObject attributePointsRewardsReloadListenerData = new JsonObject();
        JsonObject attributePointsRewards = DataCompressTool.attributePointsRewardsDataCompress();
        attributePointsRewardsReloadListenerData.add("attributePointsRewards",attributePointsRewards);

        ClientDataMessage attributePointsRewardsReloadListenerMessage = new ClientDataMessage(attributePointsRewardsReloadListenerData);
        PlayerAttributeChannel.sendToClient(attributePointsRewardsReloadListenerMessage, player);

        //同步AttributeReloadListener类的数据包
        JsonObject attributeTypesData = new JsonObject();
        JsonObject attributeTypes = DataCompressTool.attributeTypesDataCompress();
        attributeTypesData.add("attributeTypes",attributeTypes);

        ClientDataMessage attributeTypesMessage = new ClientDataMessage(attributeTypesData);
        PlayerAttributeChannel.sendToClient(attributeTypesMessage, player);

        //同步OccupationReloadListener类的数据包
        JsonObject occupationData = new JsonObject();
        JsonArray occupation = DataCompressTool.occupationDataCompress();
        occupationData.add("occupation",occupation);

        ClientDataMessage occupationMessage = new ClientDataMessage(occupationData);
        PlayerAttributeChannel.sendToClient(occupationMessage, player);

        //同步ShowGuiAttributeReloadListener类的数据包
        JsonObject showGuiAttributeData = new JsonObject();
        JsonArray showGuiAttribute = DataCompressTool.showGuiAttributeDataCompress();
        showGuiAttributeData.add("showGuiAttribute",showGuiAttribute);

        ClientDataMessage showGuiAttributeMessage = new ClientDataMessage(showGuiAttributeData);
        PlayerAttributeChannel.sendToClient(showGuiAttributeMessage, player);

        //同步InitItemResourceReloadListener类的数据包
        JsonObject initItemData = new JsonObject();
        JsonObject initItem = DataCompressTool.initItemDataCompress();
        initItemData.add("initItem",initItem);

        ClientDataMessage initItemMessage = new ClientDataMessage(initItemData);
        PlayerAttributeChannel.sendToClient(initItemMessage, player);
    }
}
