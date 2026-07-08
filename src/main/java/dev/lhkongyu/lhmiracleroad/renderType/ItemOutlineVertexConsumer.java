package dev.lhkongyu.lhmiracleroad.renderType;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class ItemOutlineVertexConsumer implements VertexConsumer {

    private final VertexConsumer delegate;
    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;

    public ItemOutlineVertexConsumer(VertexConsumer delegate, int argb) {
        this.delegate = delegate;
        this.alpha = argb >>> 24 & 255;
        this.red = argb >>> 16 & 255;
        this.green = argb >>> 8 & 255;
        this.blue = argb & 255;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        return delegate.vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return delegate.color(this.red, this.green, this.blue, this.alpha);
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        return delegate.uv(u, v);
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        return delegate.overlayCoords(u, v);
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        return delegate.uv2(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return delegate.normal(x, y, z);
    }

    @Override
    public void endVertex() {
        delegate.endVertex();
    }

    @Override
    public void defaultColor(int red, int green, int blue, int alpha) {
        delegate.defaultColor(this.red, this.green, this.blue, this.alpha);
    }

    @Override
    public void unsetDefaultColor() {
        delegate.unsetDefaultColor();
    }
}
