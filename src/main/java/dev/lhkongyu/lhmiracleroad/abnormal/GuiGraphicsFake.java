package dev.lhkongyu.lhmiracleroad.abnormal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public class GuiGraphicsFake {

    public static void blit(PoseStack pose, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight) {
        Matrix4f matrix = pose.last().pose();

        BufferBuilder buffer = Tesselator.getInstance()
                        .getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        float minU = (float) u / texWidth;
        float maxU = (float) (u + width) / texWidth;

        float minV = (float) v / texHeight;
        float maxV = (float) (v + height) / texHeight;

        buffer.vertex(matrix, x, y + height, 0).uv(minU, maxV).endVertex();

        buffer.vertex(matrix, x + width, y + height, 0).uv(maxU, maxV).endVertex();

        buffer.vertex(matrix, x + width, y, 0).uv(maxU, minV).endVertex();

        buffer.vertex(matrix, x, y, 0).uv(minU, minV).endVertex();

        BufferUploader.drawWithShader(buffer.end());
    }
}
