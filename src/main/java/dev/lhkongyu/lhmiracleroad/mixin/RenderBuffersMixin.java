package dev.lhkongyu.lhmiracleroad.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import dev.lhkongyu.lhmiracleroad.renderType.ItemRenderType;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.SortedMap;

@Mixin({RenderBuffers.class})
public class RenderBuffersMixin {

    @Shadow @Final private SortedMap<RenderType, BufferBuilder> fixedBuffers;
    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectCustomRenderType(CallbackInfo ci) {
        this.fixedBuffers.put(ItemRenderType.getFlameGlint(), new BufferBuilder(ItemRenderType.getFlameGlint().bufferSize()));
        this.fixedBuffers.put(ItemRenderType.getLightningGlint(), new BufferBuilder(ItemRenderType.getLightningGlint().bufferSize()));
        this.fixedBuffers.put(ItemRenderType.getDarkGlint(), new BufferBuilder(ItemRenderType.getDarkGlint().bufferSize()));
        this.fixedBuffers.put(ItemRenderType.getBloodGlint(), new BufferBuilder(ItemRenderType.getBloodGlint().bufferSize()));
        this.fixedBuffers.put(ItemRenderType.getHolyGlint(), new BufferBuilder(ItemRenderType.getHolyGlint().bufferSize()));
        this.fixedBuffers.put(ItemRenderType.getMagicGlint(), new BufferBuilder(ItemRenderType.getMagicGlint().bufferSize()));
        this.fixedBuffers.put(ItemRenderType.getPoisonGlint(), new BufferBuilder(ItemRenderType.getPoisonGlint().bufferSize()));
        this.fixedBuffers.put(ItemRenderType.getIceGlint(), new BufferBuilder(ItemRenderType.getIceGlint().bufferSize()));
        this.fixedBuffers.put(ItemRenderType.getSharpGlint(), new BufferBuilder(ItemRenderType.getSharpGlint().bufferSize()));
    }
}
