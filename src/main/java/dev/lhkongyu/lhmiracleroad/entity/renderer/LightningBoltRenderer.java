package dev.lhkongyu.lhmiracleroad.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.lhkongyu.lhmiracleroad.entity.magic.LightningBoltEntity;
import dev.lhkongyu.lhmiracleroad.renderType.CommonRenderType;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class LightningBoltRenderer
        extends EntityRenderer<LightningBoltEntity> {
    private static final ResourceLocation[] FIRE_TEXTURES = new ResourceLocation[]{
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_1.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_2.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_3.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_4.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_5.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_6.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_7.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_8.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_9.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_10.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_11.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_12.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_13.png"),
            LHMiracleRoadTool.resourceLocationId("textures/entity/lightning/lightning_beginning_14.png")
    };

    public LightningBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(LightningBoltEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float height = entity.getBbHeight() - 1.0f;
        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotationY(Minecraft.getInstance().getCameraEntity().getYRot() + 90.0f));
        this.renderBeam(entity, poseStack, bufferSource, height);
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotationY((-Minecraft.getInstance().getCameraEntity().getYRot() - 90.0f) * ((float)Math.PI / 180)));
        this.renderBeam(entity, poseStack, bufferSource, height);
        poseStack.popPose();
    }

    private void renderBeam(LightningBoltEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float height) {
        poseStack.translate(0.0f, height * 0.5f + 0.5f, 0.0f);
        poseStack.scale(1.0f + entity.getBbHeight() / 6.0f * 0.1f, height * 0.675f, 1.0f + entity.getBbHeight() / 6.0f * 0.1f);
        float scale = 0.1f;
        poseStack.scale(scale, scale, scale);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        VertexConsumer consumer = bufferSource.getBuffer(CommonRenderType.magic(this.getTextureLocation(entity)));
        consumer.vertex(poseMatrix, 0.0f, -8.0f, -8.0f).color(255, 255, 255, 255).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normalMatrix, 0.0f, 1.0f, 0.0f).endVertex();
        consumer.vertex(poseMatrix, 0.0f, 8.0f, -8.0f).color(255, 255, 255, 255).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normalMatrix, 0.0f, 1.0f, 0.0f).endVertex();
        consumer.vertex(poseMatrix, 0.0f, 8.0f, 8.0f).color(255, 255, 255, 255).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normalMatrix, 0.0f, 1.0f, 0.0f).endVertex();
        consumer.vertex(poseMatrix, 0.0f, -8.0f, 8.0f).color(255, 255, 255, 255).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(normalMatrix, 0.0f, 1.0f, 0.0f).endVertex();
    }

    public @NotNull ResourceLocation getTextureLocation(LightningBoltEntity entity) {
        int frame = entity.tickCount % FIRE_TEXTURES.length;
        return FIRE_TEXTURES[frame];
    }
}
