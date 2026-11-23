package dev.lhkongyu.lhmiracleroad.tool;

import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalType;
import dev.lhkongyu.lhmiracleroad.abnormal.capability.AbnormalProvider;
import dev.lhkongyu.lhmiracleroad.abnormal.sync.AbnormalNetwork;
import dev.lhkongyu.lhmiracleroad.abnormal.sync.SyncAbnormalPacket;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.packet.ClientOccupationMessage;
import dev.lhkongyu.lhmiracleroad.packet.ClientSoulMessage;
import dev.lhkongyu.lhmiracleroad.packet.PlayerChannel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public class SyncTool {

    public static void abnormalSync(LivingEntity entity) {

        entity.getCapability(AbnormalProvider.CAPABILITY).ifPresent(cap -> {

            AbnormalNetwork.CHANNEL.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new SyncAbnormalPacket(entity.getId(),
                            cap.get(AbnormalType.BLEED).maxBuildup,
                            cap.get(AbnormalType.FROST).maxBuildup,
                            cap.get(AbnormalType.POISON).maxBuildup,
                            cap.get(AbnormalType.BURN).maxBuildup,
                            cap.get(AbnormalType.BLEED).buildup,
                            cap.get(AbnormalType.FROST).buildup,
                            cap.get(AbnormalType.POISON).buildup,
                            cap.get(AbnormalType.BURN).buildup
                    )
            );
        });
    }

    /**
     * 将服务端信息同步至客户端信息中
     *
     * @param playerOccupationAttribute
     * @param player
     */
    public static void synchronizationClient(PlayerOccupationAttribute playerOccupationAttribute, ServerPlayer player) {
        UUID playerUUID = player.getUUID();
        JsonObject playerOccupationAttributeObject = playerOccupationAttribute.getPlayerOccupationAttribute(playerUUID);
        ClientOccupationMessage message = new ClientOccupationMessage(playerOccupationAttributeObject);
        PlayerChannel.sendToClient(message, player);
    }

    /**
     * 同步获取的灵魂
     * @param occupationExperience
     * @param player
     * @param soulStart
     */
    public static void synchronizationSoul(Integer occupationExperience, ServerPlayer player,Integer soulStart) {
        synchronizationSoul(occupationExperience, player, soulStart, null);
    }

    public static void synchronizationSoul(Integer occupationExperience, ServerPlayer player, Integer soulStart, Integer points) {
        UUID playerUUID = player.getUUID();
        JsonObject playerOccupationAttributeObject = new JsonObject();
        playerOccupationAttributeObject.addProperty("playerUUID",playerUUID.toString());
        playerOccupationAttributeObject.addProperty("occupationExperience",occupationExperience);
        playerOccupationAttributeObject.addProperty("soulStart",soulStart);
        if (points != null) playerOccupationAttributeObject.addProperty("points", points);
        ClientSoulMessage message = new ClientSoulMessage(playerOccupationAttributeObject);
        PlayerChannel.sendToClient(message, player);
    }
}
