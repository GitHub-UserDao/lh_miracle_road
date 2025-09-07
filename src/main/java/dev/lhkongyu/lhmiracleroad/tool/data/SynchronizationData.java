package dev.lhkongyu.lhmiracleroad.tool.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.packet.ClientDataMessage;
import dev.lhkongyu.lhmiracleroad.packet.PlayerChannel;
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
            equipmentData.addProperty("key", "modEquipment");
            equipmentData.add("data", data);
            ClientDataMessage equipmentMessage = new ClientDataMessage(equipmentData);
            PlayerChannel.sendToClient(equipmentMessage, player);
        }
        LHMiracleRoad.LOGGER.warn("LHMiracleRoad: synchronization mod equipment");

        //将服务端数据包同步至客户端
        //同步AttributePointsRewardsReloadListener类的数据包
        JsonObject attributePointsRewardsReloadListenerData = new JsonObject();
        JsonObject attributePointsRewards = DataCompressTool.attributePointsRewardsDataCompress();
        attributePointsRewardsReloadListenerData.addProperty("key", "attributePointsRewards");
        attributePointsRewardsReloadListenerData.add("data",attributePointsRewards);

        ClientDataMessage attributePointsRewardsReloadListenerMessage = new ClientDataMessage(attributePointsRewardsReloadListenerData);
        PlayerChannel.sendToClient(attributePointsRewardsReloadListenerMessage, player);

        LHMiracleRoad.LOGGER.warn("LHMiracleRoad: synchronization attribute points rewards");

        //同步AttributeReloadListener类的数据包
        JsonObject attributeTypesData = new JsonObject();
        JsonObject attributeTypes = DataCompressTool.attributeTypesDataCompress();
        attributeTypesData.addProperty("key", "attributeTypes");
        attributeTypesData.add("data",attributeTypes);

        ClientDataMessage attributeTypesMessage = new ClientDataMessage(attributeTypesData);
        PlayerChannel.sendToClient(attributeTypesMessage, player);

        LHMiracleRoad.LOGGER.warn("LHMiracleRoad: synchronization attribute types");

        //同步OccupationReloadListener类的数据包
        JsonObject occupationData = new JsonObject();
        JsonArray occupation = DataCompressTool.occupationDataCompress();
        occupationData.addProperty("key", "occupation");
        occupationData.add("data",occupation);

        ClientDataMessage occupationMessage = new ClientDataMessage(occupationData);
        PlayerChannel.sendToClient(occupationMessage, player);

        LHMiracleRoad.LOGGER.warn("LHMiracleRoad:synchronization occupation");

        //同步ShowGuiAttributeReloadListener类的数据包
        JsonObject showGuiAttributeData = new JsonObject();
        JsonArray showGuiAttribute = DataCompressTool.showGuiAttributeDataCompress();
        showGuiAttributeData.addProperty("key", "showGuiAttribute");
        showGuiAttributeData.add("data",showGuiAttribute);

        ClientDataMessage showGuiAttributeMessage = new ClientDataMessage(showGuiAttributeData);
        PlayerChannel.sendToClient(showGuiAttributeMessage, player);

        LHMiracleRoad.LOGGER.warn("LHMiracleRoad:synchronization show gui attribute");

        //同步InitItemResourceReloadListener类的数据包
        JsonObject initItemData = new JsonObject();
        JsonObject initItem = DataCompressTool.initItemDataCompress();
        initItemData.addProperty("key", "initItem");
        initItemData.add("data",initItem);

        ClientDataMessage initItemMessage = new ClientDataMessage(initItemData);
        PlayerChannel.sendToClient(initItemMessage, player);

        LHMiracleRoad.LOGGER.warn("LHMiracleRoad:synchronization init item");
    }
}
