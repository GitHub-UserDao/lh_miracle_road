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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class ItemOutlineRendererMixin {

    @Unique
    private static final float LH_MIRACLE_ROAD_OUTLINE_SCALE = 1.035F;

    @Unique
    private static final float LH_MIRACLE_ROAD_OUTLINE_PIXEL = 1.0F / 16.0F;

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

    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"))
    private void lh_miracle_road$renderOutline(ItemRenderer itemRenderer, BakedModel model, ItemStack stack, int packedLight, int packedOverlay, PoseStack poseStack, VertexConsumer buffer, ItemStack originalStack, ItemDisplayContext context, boolean leftHand, PoseStack originalPoseStack, MultiBufferSource multiBufferSource, int originalPackedLight, int originalPackedOverlay, BakedModel originalModel) {
        int outlineColor = ItemOutlineColor.getColor(stack);
        if (ItemOutlineColor.hasOutline(outlineColor)) {
            VertexConsumer outlineBuffer = new ItemOutlineVertexConsumer(multiBufferSource.getBuffer(ItemOutlineRenderType.getRenderType()), outlineColor);

            for (float[] offset : LH_MIRACLE_ROAD_OUTLINE_OFFSETS) {
                poseStack.pushPose();
                poseStack.translate(offset[0], offset[1], offset[2]);
                poseStack.scale(LH_MIRACLE_ROAD_OUTLINE_SCALE, LH_MIRACLE_ROAD_OUTLINE_SCALE, LH_MIRACLE_ROAD_OUTLINE_SCALE);
                this.renderModelLists(model, stack, LightTexture.FULL_BRIGHT, packedOverlay, poseStack, outlineBuffer);
                poseStack.popPose();
            }
        }

        this.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer);
    }
}
