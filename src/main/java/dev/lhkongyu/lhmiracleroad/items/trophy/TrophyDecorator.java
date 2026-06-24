package dev.lhkongyu.lhmiracleroad.items.trophy;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.items.TrophyItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemDecorator;

@OnlyIn(Dist.CLIENT)
public class TrophyDecorator implements IItemDecorator {

    private static final ResourceLocation HALO = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/halo.png");

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int x, int y) {

        if (!(stack.getItem() instanceof TrophyItem trophy)) return false;

        ItemStack displayStack = TrophyManager.getDisplayStack(trophy);

        if (displayStack.isEmpty()) return false;

        PoseStack pose = guiGraphics.pose();

        // ===== 动画参数 =====
        float time = (System.currentTimeMillis() % 2000) / 2000f;
        float scale = 0.48f + 0.04f * Mth.sin(time * Mth.TWO_PI);
        float offsetY = Mth.sin(time * Mth.TWO_PI) * 0.5f;
//        float offsetY = Mth.sin((System.currentTimeMillis() % 1000L) / 1000f * Mth.TWO_PI) * 1.5f;
        int centerX = x + 8;
        int centerY = y + 10;

        float haloScale = 0.9f;
        float haloSize = 16 * haloScale;
        int color = getHaloColor(displayStack.getRarity());
        float alpha = 0.65f;

        pose.pushPose();
        pose.translate(centerX, centerY - 1 + offsetY, 200);
        guiGraphics.setColor(
                ((color >> 16) & 255) / 255f,
                ((color >> 8) & 255) / 255f,
                (color & 255) / 255f,
                alpha);

        guiGraphics.blit(
                HALO,
                (int)(-haloSize / 2),
                (int)(-haloSize / 2),
                0,
                0,
                (int)haloSize,
                (int)haloSize,
                (int)haloSize,
                (int)haloSize
        );
        guiGraphics.setColor(1,1,1,1);
        pose.popPose();

        //渲染奖励物品
        pose.pushPose();
        pose.translate(centerX, centerY - 1 + offsetY, 100);
        pose.scale(scale, scale, 1f);
        guiGraphics.renderItem(displayStack, -8, -8);
        pose.popPose();

        return false;
    }

    private static int getHaloColor(Rarity rarity) {
        return switch (rarity) {
            case UNCOMMON -> 0xFFFFD700;   // 金
            case RARE -> 0xFF55AAFF;       // 蓝
            case EPIC -> 0xFFFF55FF;       // 紫
            default -> 0xFF55FF55;         // 绿
        };
    }

}
