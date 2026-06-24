package dev.lhkongyu.lhmiracleroad.packet;

import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.registry.ParticleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AbyssbindActivatePacket {

    public AbyssbindActivatePacket() {
    }

    public AbyssbindActivatePacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public static void handle(AbyssbindActivatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            // 中间显示物品动画
            mc.gameRenderer.displayItemActivation(new ItemStack(ItemsRegistry.ABYSSBIND_BRACELET.get()));
            mc.player.playSound(SoundEvents.TOTEM_USE, 1F, 1F);
        });

        ctx.get().setPacketHandled(true);
    }
}
