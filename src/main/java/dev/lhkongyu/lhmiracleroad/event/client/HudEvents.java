package dev.lhkongyu.lhmiracleroad.event.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID, value = Dist.CLIENT)
public class HudEvents {

    private static String lastText;

    public static long lastSoulGainTime = 0;

    public static final long HUD_DISPLAY_DURATION = 1500;

    @SubscribeEvent
    public static void onRenderGui(ScreenEvent.Render.Post event) {
        if (!(Minecraft.getInstance().screen instanceof AnvilScreen)) return;

        long now = System.currentTimeMillis();
        long elapsed = now - lastSoulGainTime;
        if (elapsed > HUD_DISPLAY_DURATION){
            lastSoulGainTime = 0;
            return;
        }

        PoseStack pose = event.getGuiGraphics().pose();

        pose.pushPose();
        Window win = Minecraft.getInstance().getWindow();
        Font font  = Minecraft.getInstance().font;
        int x = win.getGuiScaledWidth()  / 2;
        int y = win.getGuiScaledHeight() / 2 - 9;

        font.drawInBatch(
                Component.literal(lastText),
                x - font.width(lastText) / 2f, y,
                0xFFFF5555, true,
                pose.last().pose(),
                Minecraft.getInstance().renderBuffers().bufferSource(),
                Font.DisplayMode.NORMAL,
                0, 0xF000F0
        );
        pose.popPose();

    }

    public static void show(String txt) {
        lastText = txt;
        lastSoulGainTime = System.currentTimeMillis();
    }
}
