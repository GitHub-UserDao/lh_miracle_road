package dev.lhkongyu.lhmiracleroad.tool.data;

import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.config.editor.ConfigEditorPackets;
import dev.lhkongyu.lhmiracleroad.config.editor.ConfigSection;
import dev.lhkongyu.lhmiracleroad.config.strengthen.StrengthenConfig;
import dev.lhkongyu.lhmiracleroad.items.trophy.TrophyManager;
import dev.lhkongyu.lhmiracleroad.config.SoulItemConfig;
import dev.lhkongyu.lhmiracleroad.config.strengthen.WeaponTypeConfig;
import dev.lhkongyu.lhmiracleroad.packet.ClientDataMessage;
import dev.lhkongyu.lhmiracleroad.packet.InitItemPreviewMessage;
import dev.lhkongyu.lhmiracleroad.packet.OccupationCatalogMessage;
import dev.lhkongyu.lhmiracleroad.packet.PlayerChannel;
import dev.lhkongyu.lhmiracleroad.packet.WeaponTypesMessage;
import net.minecraft.server.level.ServerPlayer;

public class SynchronizationData {

    /**
     * 将服务端配置同步至客户端
     */
    public static void synchronization(ServerPlayer player) {
        PlayerChannel.sendToClient(new WeaponTypesMessage(WeaponTypeConfig.forSide(false)), player);
        ConfigEditorPackets.syncGameplay(player, ConfigSection.STRENGTHEN, StrengthenConfig.snapshot());
        ConfigEditorPackets.syncGameplay(player, ConfigSection.TROPHY, TrophyManager.snapshot());
        syncSoulRewards(player);
        InitItemPreviewMessage.sendAll(player);
        OccupationCatalogMessage.sendAll(player);
        JsonObject equipment = new JsonObject();
        equipment.add("data", DataCompressTool.equipmentDataCompress());
        var config = LHMiracleRoadConfig.COMMON;
        equipment.addProperty("occupationLevelBase", config.LEVEL_BASE.get());
        equipment.addProperty("weightEnabled", config.ENABLE_EQUIPMENT_WEIGHT.get());
        equipment.addProperty("offhandWeightEnabled", config.IS_OFFHAND_CALCULATE_HEAVY.get());
        equipment.addProperty("attributePenaltiesEnabled", config.IS_SKILL_POINTS_RESTRICT.get());
        ConfigEditorPackets.syncGameplay(player, ConfigSection.EQUIPMENT, equipment);
    }

    public static void syncSoulRewards(ServerPlayer player) {
        JsonObject soulItems = new JsonObject();
        soulItems.addProperty("key", "soulItems");
        soulItems.add("data", SoulItemConfig.toJson(SoulItemConfig.forSide(false)));
        PlayerChannel.sendToClient(new ClientDataMessage(soulItems), player);
    }
}
