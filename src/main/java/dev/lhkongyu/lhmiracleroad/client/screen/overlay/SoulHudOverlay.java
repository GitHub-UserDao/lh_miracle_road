package dev.lhkongyu.lhmiracleroad.client.screen.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Queue;

public class SoulHudOverlay implements IGuiOverlay {
    public static long lastSoulGainTime = 0;
    public static int lastSoulEndAmount = 0;
    public static int lastSoulStartAmount = 0;
    public static final long HUD_DISPLAY_DURATION = 5000; // 显示5秒
    public static Queue<Integer> integerSequence;

    public static void obtainSoul(int soulStart,int soulEnd) {
        lastSoulGainTime = System.currentTimeMillis();
        lastSoulEndAmount = soulEnd;
        integerSequence = LHMiracleRoadTool.getIntegerSequence(soulStart,soulEnd);
        lastSoulStartAmount = soulStart;
    }

    public static void synchronizeSoul(int soul) {
        lastSoulEndAmount = soul;
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        long now = System.currentTimeMillis();
        long elapsed = now - lastSoulGainTime;
        if (elapsed > HUD_DISPLAY_DURATION){
            lastSoulGainTime = 0;
            return;
        }

        float alpha = 1.0f;
        long fadeDuration = 2000;
        long remain = HUD_DISPLAY_DURATION - elapsed;
        if (remain < fadeDuration) {
            alpha = remain / (float) fadeDuration;
        }

        int soul = lastSoulEndAmount;
        if (integerSequence != null && !integerSequence.isEmpty()){
            soul = integerSequence.poll();
        }

        int iconX = screenWidth - 80;
        int iconY = screenHeight - 20;
        int pngWidth = 72;
        int pngHeight = 17;

        Font font = Minecraft.getInstance().font;

        guiGraphics.pose().pushPose();
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, alpha);

        // 渲染底图
        guiGraphics.blit(
                new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/soul.png"),
                iconX, iconY,
                0, 0,
                pngWidth, pngHeight,
                pngWidth, pngHeight
        );

        // 计算居中坐标
        String soulStr = String.valueOf(soul);
        int textWidth = font.width(soulStr);
        int textX = iconX + (pngWidth - textWidth) / 2;
        int textY = iconY + (pngHeight - font.lineHeight) / 2 + 1;

        guiGraphics.drawString(
                font,
                soulStr,
                textX, textY,
                (int)(0xFFFFFF | ((int)(alpha * 255) << 24)),
                false
        );

        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        guiGraphics.pose().popPose();

        //显示添加了多少的魂
        if (elapsed > 3000) return;
        alpha = 1.0f;
        remain = 3000 - elapsed;
        if (remain < fadeDuration) {
            alpha = remain / (float) fadeDuration;
        }
        iconY = screenHeight - 20 - pngHeight;

        guiGraphics.pose().pushPose();
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, alpha);

        // 渲染底图
        guiGraphics.blit(
                new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/soul_add.png"),
                iconX, iconY,
                0, 0,
                pngWidth, pngHeight,
                pngWidth, pngHeight
        );

        // 计算居中坐标
        soulStr = lastSoulStartAmount <= lastSoulEndAmount ? "+ " + (lastSoulEndAmount - lastSoulStartAmount) : "- " + (lastSoulStartAmount - lastSoulEndAmount);
        textWidth = font.width(soulStr);
        textX = iconX + (pngWidth - textWidth) / 2;
        textY = iconY + (pngHeight - font.lineHeight) / 2 + 1;

        guiGraphics.drawString(
                font,
                soulStr,
                textX, textY,
                (int)(0xFFFFFF | ((int)(alpha * 255) << 24)),
                false
        );

        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        guiGraphics.pose().popPose();
    }
}