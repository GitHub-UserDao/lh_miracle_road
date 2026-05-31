package dev.lhkongyu.lhmiracleroad.abnormal.sync;

import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalType;
import dev.lhkongyu.lhmiracleroad.abnormal.capability.AbnormalProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncAbnormalPacket {

    private final int entityId;

    private final int bleedMaxBuildup;
    private final int frostMaxBuildup;
    private final int poisonMaxBuildup;
    private final int burnMaxBuildup;

    private final float bleed;
    private final float frost;
    private final float poison;
    private final float burn;

    public SyncAbnormalPacket(
            int entityId,
            int bleedMaxBuildup,
            int frostMaxBuildup,
            int poisonMaxBuildup,
            int burnMaxBuildup,
            float bleed,
            float frost,
            float poison,
            float burn
    ) {
        this.entityId = entityId;
        this.bleedMaxBuildup = bleedMaxBuildup;
        this.frostMaxBuildup = frostMaxBuildup;
        this.poisonMaxBuildup = poisonMaxBuildup;
        this.burnMaxBuildup = burnMaxBuildup;
        this.bleed = bleed;
        this.frost = frost;
        this.poison = poison;
        this.burn = burn;
    }

    public static void encode(
            SyncAbnormalPacket packet,
            FriendlyByteBuf buf
    ) {
        buf.writeInt(packet.entityId);

        buf.writeInt(packet.bleedMaxBuildup);
        buf.writeInt(packet.frostMaxBuildup);
        buf.writeInt(packet.poisonMaxBuildup);
        buf.writeInt(packet.burnMaxBuildup);

        buf.writeFloat(packet.bleed);
        buf.writeFloat(packet.frost);
        buf.writeFloat(packet.poison);
        buf.writeFloat(packet.burn);
    }

    public static SyncAbnormalPacket decode(
            FriendlyByteBuf buf
    ) {
        return new SyncAbnormalPacket(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public static void handle(SyncAbnormalPacket packet, Supplier<NetworkEvent.Context> supplier) {

        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();

            if (mc.level == null) return;

            Entity entity = mc.level.getEntity(packet.entityId);

            if (!(entity instanceof LivingEntity living)) return;

            living.getCapability(AbnormalProvider.CAPABILITY).ifPresent(cap -> {

                cap.get(AbnormalType.BLEED).maxBuildup = packet.bleedMaxBuildup;
                cap.get(AbnormalType.BLEED).buildup = packet.bleed;

                cap.get(AbnormalType.FROST).maxBuildup = packet.frostMaxBuildup;
                cap.get(AbnormalType.FROST).buildup = packet.frost;

                cap.get(AbnormalType.POISON).maxBuildup = packet.poisonMaxBuildup;
                cap.get(AbnormalType.POISON).buildup = packet.poison;

                cap.get(AbnormalType.BURN).maxBuildup = packet.burnMaxBuildup;
                cap.get(AbnormalType.BURN).buildup = packet.burn;
            });
        });

        context.setPacketHandled(true);
    }
}
