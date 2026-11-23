package dev.lhkongyu.lhmiracleroad.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.lhkongyu.lhmiracleroad.renderType.ItemOutlineColor;
import dev.lhkongyu.lhmiracleroad.renderType.ItemOutlineRenderType;
import dev.lhkongyu.lhmiracleroad.renderType.ItemOutlineVertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemOutlineRendererMixin {

    @Unique
    private static final float LH_MIRACLE_ROAD_OUTLINE_SCALE = 1F;

    @Unique
    private static final float LH_MIRACLE_ROAD_OUTLINE_PIXEL = 0.4F / 16.0F;

    @Unique
    private static final float[][] LH_MIRACLE_ROAD_OUTLINE_OFFSETS = new float[][]{
            {-LH_MIRACLE_ROAD_OUTLINE_PIXEL, 0.0F, 0.0F},
            {LH_MIRACLE_ROAD_OUTLINE_PIXEL, 0.0F, 0.0F},
            {0.0F, -LH_MIRACLE_ROAD_OUTLINE_PIXEL, 0.0F},
            {0.0F, LH_MIRACLE_ROAD_OUTLINE_PIXEL, 0.0F},
            {-LH_MIRACLE_ROAD_OUTLINE_PIXEL, -LH_MIRACLE_ROAD_OUTLINE_PIXEL, 0.0F},
            {-LH_MIRACLE_ROAD_OUTLINE_PIXEL, LH_MIRACLE_ROAD_OUTLINE_PIXEL, 0.0F},
            {LH_MIRACLE_ROAD_OUTLINE_PIXEL, -LH_MIRACLE_ROAD_OUTLINE_PIXEL, 0.0F},
            {LH_MIRACLE_ROAD_OUTLINE_PIXEL, LH_MIRACLE_ROAD_OUTLINE_PIXEL, 0.0F}
    };

    @Shadow
    public abstract void renderModelLists(BakedModel model, ItemStack stack, int packedLight, int packedOverlay, PoseStack poseStack, VertexConsumer buffer);

    // Draw before the normal model gets a vertex buffer, without replacing its render call.
    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/BakedModel;getRenderPasses(Lnet/minecraft/world/item/ItemStack;Z)Ljava/util/List;",
            remap = false), require = 0)
    private void lh_miracle_road$renderOutline(ItemStack stack, ItemDisplayContext context, boolean leftHand,
                                               PoseStack poseStack, MultiBufferSource buffers, int packedLight,
                                               int packedOverlay, BakedModel model, CallbackInfo ci) {
        int[] outlineColors = ItemOutlineColor.getColors(stack);
        if (!ItemOutlineColor.hasOutline(outlineColors)) return;

        boolean directBuffer = true;
        if (context != ItemDisplayContext.GUI && !context.firstPerson() && stack.getItem() instanceof BlockItem blockItem) {
            var block = blockItem.getBlock();
            directBuffer = !(block instanceof HalfTransparentBlock || block instanceof StainedGlassPaneBlock);
        }

        for (BakedModel pass : model.getRenderPasses(stack, directBuffer)) {
            if (pass.getRenderTypes(stack, directBuffer).isEmpty()) continue;
            VertexConsumer outlineVertices = buffers.getBuffer(ItemOutlineRenderType.getRenderType());
            for (int i = 0; i < LH_MIRACLE_ROAD_OUTLINE_OFFSETS.length; i++) {
                float[] offset = LH_MIRACLE_ROAD_OUTLINE_OFFSETS[i];
                int outlineColor = outlineColors[i % outlineColors.length];
                VertexConsumer outlineBuffer = new ItemOutlineVertexConsumer(outlineVertices, outlineColor);
                poseStack.pushPose();
                try {
                    poseStack.translate(offset[0], offset[1], offset[2]);
                    poseStack.scale(LH_MIRACLE_ROAD_OUTLINE_SCALE, LH_MIRACLE_ROAD_OUTLINE_SCALE, LH_MIRACLE_ROAD_OUTLINE_SCALE);
                    this.renderModelLists(pass, stack, LightTexture.FULL_BRIGHT, packedOverlay, poseStack, outlineBuffer);
                } finally {
                    poseStack.popPose();
                }
            }
        }
    }
}
