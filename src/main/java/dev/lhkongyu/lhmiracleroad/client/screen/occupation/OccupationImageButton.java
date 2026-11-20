package dev.lhkongyu.lhmiracleroad.client.screen.occupation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OccupationImageButton extends Button {

    private static final float TEXT_SCALE = 0.7F;

    private static final int TEXT_COLOR = 0xC56F49;

    private final ResourceLocation texture;

    private final int normalU;

    private final int normalV;

    private final int hoverU;

    private final int hoverV;

    private final int textureWidth;

    private final int textureHeight;

    private final float scale;

    private final int sourceWidth;

    private final int sourceHeight;

    private final boolean drawLabel;

    public OccupationImageButton(int x, int y, int width, int height, Component message, OnPress onPress,
                                 ResourceLocation texture, int normalU, int normalV, int hoverU, int hoverV,
                                 int textureWidth, int textureHeight, float scale, int sourceWidth, int sourceHeight) {
        this(x, y, width, height, message, onPress, texture, normalU, normalV, hoverU, hoverV,
                textureWidth, textureHeight, scale, sourceWidth, sourceHeight, true);
    }

    public OccupationImageButton(int x, int y, int width, int height, Component message, OnPress onPress,
                                 ResourceLocation texture, int normalU, int normalV, int hoverU, int hoverV,
                                 int textureWidth, int textureHeight, float scale, int sourceWidth, int sourceHeight,
                                 boolean drawLabel) {
        super(x, y, width, height, message, onPress, Supplier::get);
        this.texture = texture;
        this.normalU = normalU;
        this.normalV = normalV;
        this.hoverU = hoverU;
        this.hoverV = hoverV;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.scale = scale;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        this.drawLabel = drawLabel;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean highlighted = isHovered();
        int u = highlighted ? hoverU : normalU;
        int v = highlighted ? hoverV : normalV;
        graphics.pose().pushPose();
        graphics.pose().translate(getX(), getY(), 0);
        graphics.pose().scale(scale, scale, 1.0F);
        boolean tintHover = highlighted && normalU == hoverU && normalV == hoverV;
        float brightness = !active ? 0.65F : tintHover ? 0.85F : 1.0F;
        if (brightness < 1.0F) graphics.setColor(brightness, brightness, brightness, 1.0F);
        graphics.blit(texture, 0, 0, u, v, sourceWidth, sourceHeight, textureWidth, textureHeight);
        if (brightness < 1.0F) graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (drawLabel && !getMessage().getString().isEmpty()) {
            int textWidth = Minecraft.getInstance().font.width(getMessage());
            graphics.pose().pushPose();
            graphics.pose().translate(sourceWidth / 2.0F, sourceHeight / 2.0F, 0);
            graphics.pose().scale(TEXT_SCALE, TEXT_SCALE, 1.0F);
            graphics.drawString(Minecraft.getInstance().font, getMessage().getVisualOrderText(), -textWidth / 2.0F,
                    -Minecraft.getInstance().font.lineHeight / 2.0F, TEXT_COLOR, false);
            graphics.pose().popPose();
        }
        graphics.pose().popPose();
    }
}
