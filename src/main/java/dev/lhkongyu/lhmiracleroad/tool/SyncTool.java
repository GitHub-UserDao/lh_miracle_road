package dev.lhkongyu.lhmiracleroad.tool;

import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalType;
import dev.lhkongyu.lhmiracleroad.abnormal.capability.AbnormalProvider;
import dev.lhkongyu.lhmiracleroad.abnormal.sync.AbnormalNetwork;
import dev.lhkongyu.lhmiracleroad.abnormal.sync.SyncAbnormalPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;

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
}
