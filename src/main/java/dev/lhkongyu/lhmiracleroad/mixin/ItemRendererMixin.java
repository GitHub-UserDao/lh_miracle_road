package dev.lhkongyu.lhmiracleroad.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import dev.lhkongyu.lhmiracleroad.renderType.ItemRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements ResourceManagerReloadListener {
    @Redirect(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBufferDirect(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"),
            method = "render")
    public VertexConsumer foilBufferDirect(MultiBufferSource multiBuffer, RenderType p_115224_, boolean p_115225_, boolean p_115226_, ItemStack item, ItemDisplayContext context , boolean bool) {
        LocalPlayer localplayer = Minecraft.getInstance().player;

        if (localplayer != null && item.getTag() != null) {
            CompoundTag compoundTag = item.getOrCreateTag().getCompound("lh_gem");
            if (compoundTag.contains("type")) return lhmiracleroad$getFoilBufferDirect(item, multiBuffer, p_115224_, compoundTag.getString("type"));
        }
        return ItemRenderer.getFoilBufferDirect(multiBuffer, p_115224_, true, item.hasFoil());
    }

    @Unique
    private static VertexConsumer lhmiracleroad$getFoilBufferDirect(ItemStack item, MultiBufferSource multiBufferSource, RenderType base, String type) {

        return VertexMultiConsumer.create(
                multiBufferSource.getBuffer(ItemRenderType.getGlint(type)),
                multiBufferSource.getBuffer(base));
    }

}
