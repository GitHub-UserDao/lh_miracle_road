package dev.lhkongyu.lhmiracleroad.abnormal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.abnormal.capability.AbnormalProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AbnormalBarRenderer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/abnormal.png");

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity entity = event.getEntity();
        if (!entity.isAlive()) return;

        entity.getCapability(AbnormalProvider.CAPABILITY).ifPresent(cap -> {
            List<BarData> bars = new ArrayList<>();
            int bleedMaxBuildup = cap.get(AbnormalType.BLEED).maxBuildup;
            float bleed = cap.get(AbnormalType.BLEED).buildup;

            int frostMaxBuildup = cap.get(AbnormalType.FROST).maxBuildup;
            float frost = cap.get(AbnormalType.FROST).buildup;

            int poisonMaxBuildup = cap.get(AbnormalType.POISON).maxBuildup;
            float poison = cap.get(AbnormalType.POISON).buildup;

            int burnMaxBuildup = cap.get(AbnormalType.BURN).maxBuildup;
            float burn = cap.get(AbnormalType.BURN).buildup;

            if (bleed > 0) bars.add(new BarData(AbnormalType.BLEED,bleedMaxBuildup, bleed));
            if (frost > 0) bars.add(new BarData(AbnormalType.FROST,frostMaxBuildup, frost));
            if (poison > 0) bars.add(new BarData(AbnormalType.POISON,poisonMaxBuildup, poison));
            if (burn > 0) bars.add(new BarData(AbnormalType.BURN,burnMaxBuildup, burn));

            if (bars.isEmpty()) return;
            renderBars(event, entity, bars);
        });
    }

    private static void renderBars(RenderLivingEvent.Post<?, ?> event, LivingEntity entity, List<BarData> bars) {
        PoseStack poseStack = event.getPoseStack();
        Minecraft mc = Minecraft.getInstance();
        poseStack.pushPose();
        float height = entity.getBbHeight() + 0.45f + (entity.isShiftKeyDown() ? 0.2f : 0);

        poseStack.translate(0, height, 0);

        poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());

        poseStack.scale(-0.025f, -0.025f, 0.025f);

        RenderSystem.enableBlend();

        int totalHeight = bars.size() * 14;

        int startY = -totalHeight;

        for (int i = 0; i < bars.size(); i++) {

            BarData data = bars.get(i);

            int y = startY + i * 14;

            renderBar(poseStack, data, y);
        }

        poseStack.popPose();
    }

    private static void renderBar(PoseStack poseStack, BarData data, int y) {

        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = -31;

        GuiGraphicsFake.blit(poseStack, x, y,
                0, 0, 62, 13, 62, 41
        );

        int width = Mth.clamp((int) (data.value / data.maxBuildup * 56f), 0, 56);

        if (width <= 0) return;

        int v = switch (data.type) {
            case FROST -> 14;
            case BLEED -> 21;
            case POISON -> 28;
            case BURN -> 35;
        };

        GuiGraphicsFake.blit(poseStack,
                x + 4, y + 4, 4, v,
                width, 6, 62, 41
        );
    }

    private record BarData(AbnormalType type,int maxBuildup, float value) {
    }
}
