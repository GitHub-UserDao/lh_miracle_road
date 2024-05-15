package dev.lhkongyu.lhmiracleroad.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.data.reloader.*;
import dev.lhkongyu.lhmiracleroad.packet.ClientDataMessage;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributeChannel;
import net.minecraft.server.level.ServerPlayer;

public class SynchronizationData {

    /**
     * 将服务端数据包文件发送至客户端
     */
    public static void synchronization(ServerPlayer player) {
        //将服务端数据包同步至客户端
        //同步AttributePointsRewardsReloadListener类的数据包
        JsonObject attributePointsRewardsReloadListenerData = new JsonObject();
        JsonObject attributePointsRewards = new JsonObject();
        AttributePointsRewardsReloadListener.ATTRIBUTE_POINTS_REWARDS.forEach(attributePointsRewards::add);
        attributePointsRewardsReloadListenerData.add("attributePointsRewards",attributePointsRewards);

        ClientDataMessage attributePointsRewardsReloadListenerMessage = new ClientDataMessage(attributePointsRewardsReloadListenerData);
        PlayerAttributeChannel.sendToClient(attributePointsRewardsReloadListenerMessage, player);

        //同步AttributeReloadListener类的数据包
        JsonObject attributeTypesData = new JsonObject();
        JsonObject attributeTypes = new JsonObject();
        AttributeReloadListener.ATTRIBUTE_TYPES.forEach(attributeTypes::add);
        attributeTypesData.add("attributeTypes",attributeTypes);

        ClientDataMessage attributeTypesMessage = new ClientDataMessage(attributeTypesData);
        PlayerAttributeChannel.sendToClient(attributeTypesMessage, player);

        //同步EquipmentReloadListener类的数据包
        JsonObject equipmentData = new JsonObject();
        JsonObject equipment = LHMiracleRoadTool.equipmentDataCompress();
        equipmentData.add("equipment",equipment);

        ClientDataMessage equipmentMessage = new ClientDataMessage(equipmentData);
        PlayerAttributeChannel.sendToClient(equipmentMessage, player);

        //同步OccupationReloadListener类的数据包
        JsonObject occupationData = new JsonObject();
        JsonArray occupation = new JsonArray();
        OccupationReloadListener.OCCUPATION.forEach(occupation::add);
        occupationData.add("occupation",occupation);

        ClientDataMessage occupationMessage = new ClientDataMessage(occupationData);
        PlayerAttributeChannel.sendToClient(occupationMessage, player);

        //同步ShowGuiAttributeReloadListener类的数据包
        JsonObject showGuiAttributeData = new JsonObject();
        JsonArray showGuiAttribute = new JsonArray();
        ShowGuiAttributeReloadListener.SHOW_GUI_ATTRIBUTE.forEach(showGuiAttribute::add);
        showGuiAttributeData.add("showGuiAttribute",showGuiAttribute);

        ClientDataMessage showGuiAttributeMessage = new ClientDataMessage(showGuiAttributeData);
        PlayerAttributeChannel.sendToClient(showGuiAttributeMessage, player);

        //同步InitItemResourceReloadListener类的数据包
        JsonObject initItemData = new JsonObject();
        JsonObject initItem = new JsonObject();
        InitItemResourceReloadListener.INIT_ITEM.forEach(initItem::add);
        initItemData.add("initItem",initItem);

        ClientDataMessage initItemMessage = new ClientDataMessage(initItemData);
        PlayerAttributeChannel.sendToClient(initItemMessage, player);
    }
}
