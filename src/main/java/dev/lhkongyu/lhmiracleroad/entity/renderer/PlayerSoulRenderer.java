package dev.lhkongyu.lhmiracleroad.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.entity.CommonRenderType;
import dev.lhkongyu.lhmiracleroad.entity.player.PlayerSoulEntity;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

import java.util.Objects;

public class PlayerSoulRenderer extends EntityRenderer<Entity> {

    private final Vector3f redColorA = LHMiracleRoadTool.RGBChangeVector3f(248, 176, 73);
    private final Vector3f redColorB = LHMiracleRoadTool.RGBChangeVector3f(205, 95, 0);

    private static final ResourceLocation TEXTURE = LHMiracleRoadTool.resourceLocationId("textures/entity/player_soul.png");

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(LHMiracleRoad.MODID, "player_soul"), "main");
    private final ModelPart main;

    protected final float scale;

    public PlayerSoulRenderer(EntityRendererProvider.Context context, float scale) {
        super(context);
        this.scale = scale;
        ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.main = modelpart.getChild("main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition Head = main.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -28.0F, 0.0F));

        PartDefinition cube_r1 = Head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, -0.0175F, 0.0F));

        PartDefinition Body = main.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, 0.0F));

        PartDefinition LeftArm = main.addOrReplaceChild("LeftArm", CubeListBuilder.create(), PartPose.offset(4.0F, -24.0F, 0.0F));

        PartDefinition cube_r2 = LeftArm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 16).addBox(0.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, 0.0F, 0.0F, 0.0F, -2.2689F));

        PartDefinition RightArm = main.addOrReplaceChild("RightArm", CubeListBuilder.create(), PartPose.offset(-10.0F, -26.0F, 0.0F));

        PartDefinition cube_r3 = RightArm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 4.0F, 0.0F, 0.0F, 0.0F, 2.2689F));

        PartDefinition RightLeg = main.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LeftLeg = main.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }


    @Override
    public void render(Entity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        VertexConsumer consumer = bufferSource.getBuffer(CommonRenderType.magic(getTextureLocation(entity)));
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.scale(scale, scale, scale);
        this.main.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,redColorA.x,redColorA.y,redColorA.z,.6f);
        poseStack.popPose();

        poseStack.pushPose();
        float scale = this.scale * 1.1f;
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.scale(scale, scale, scale);
        this.main.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,redColorB.x,redColorB.y,redColorB.z,.3f);
        poseStack.popPose();

        // 渲染名称标签（核心代码）
        if (entity.isCustomNameVisible()) {
            renderNameTag(entity, Objects.requireNonNull(entity.getCustomName()), poseStack, bufferSource, LightTexture.FULL_BRIGHT);
        }
//        super.render(entity, yaw, partialTicks, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return TEXTURE;
    }
}
