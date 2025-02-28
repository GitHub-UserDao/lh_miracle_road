package dev.lhkongyu.lhmiracleroad.screen;

import dev.lhkongyu.lhmiracleroad.tool.ResourceLocationTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ImageButton extends Button {

    protected OnPress pressFunc;

    private final boolean isPage;

    private final ResourceLocation backgroundImage;

    private final ResourceLocation hoveredFocusedImage;

    private final int initWidth;

    private final int initHeight;

    private final int imageWidthBase;

    private final int imageHeightBase;

    private final int imageWidth;

    private final int imageHeight;

    private final boolean isText;

    public ImageButton(int x, int y, int width, int height, Component message, boolean isPage, boolean isText, ResourceLocation backgroundImage,ResourceLocation hoveredFocusedImage,
                       int initWidth, int initHeight, int imageWidthBase, int imageHeightBase, int imageWidth, int imageHeight) {
        super(x, y, width, height, message, b -> {}, Supplier::get);
        this.isPage = isPage;
        this.isText = isText;
        this.backgroundImage = backgroundImage;
        this.hoveredFocusedImage = hoveredFocusedImage;
        this.initWidth = initWidth;
        this.initHeight = initHeight;
        this.imageWidthBase = imageWidthBase;
        this.imageHeightBase = imageHeightBase;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.pressFunc = b -> {};
    }

    public void setPressFunc(OnPress pressFunc) {
        this.pressFunc = pressFunc;
    }

    @Override
    public void onPress() {
        pressFunc.onPress(this);
    }

    @Override
    public void renderWidget(
            @NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(backgroundImage, getX(), getY(), initWidth, initHeight, imageWidthBase, imageHeightBase, imageWidth, imageHeight);
        if (isText) {
            renderText(graphics,0xC56F49);
        }
        // 鼠标否悬触发
        if (isHovered() && isPage) {
            graphics.blit(hoveredFocusedImage, getX(), getY(), initWidth, initHeight, imageWidthBase, imageHeightBase, imageWidth, imageHeight);
            if (isText) {
                renderText(graphics,0xC59290);
            }
        }
    }

    protected void renderText(@NotNull GuiGraphics graphics,int color) {

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int x = getX() + width / 2;
        int y = getY() + height / 5;
        graphics.drawCenteredString(font, getMessage(),  x, y, color);
//        boolean isFullscreen = minecraft.getWindow().isFullscreen();
//        final int targetGuiScale = 4;
//        if (currentGuiScale < targetGuiScale && currentGuiScale > 0) {
//            float scale =  targetGuiScale / (float) currentGuiScale;
//            graphics.pose().pushPose();
//            graphics.pose().scale(scale, scale, 0);
//            graphics.drawCenteredString(font, getMessage(), (int) (x / scale), (int) (y / scale) + (font.lineHeight / 5), color);
//
//            graphics.pose().popPose();
//        }else {
//            graphics.drawCenteredString(font, getMessage(),  x, y, color);
//        }
//        graphics.drawCenteredString(
//                font, getMessage(), getX() + width / 2, getY() + height / 5, color);
    }

}
