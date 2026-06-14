package dev.lhkongyu.lhmiracleroad.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSheetParticle extends TextureSheetParticle {
    protected final SpriteSet sprite;

    public AbstractSheetParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, x, y, z, xd, yd, zd);
        this.sprite = spriteSet;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.setSpriteFromAge(spriteSet);
    }

    protected abstract ParticleRenderType renderType();

    protected void particleRandomOffset() {

    }

    public void tick() {
        super.tick();
        this.particleRandomOffset();
        this.setSpriteFromAge(this.sprite);
    }

    public @NotNull ParticleRenderType getRenderType() {
        return this.renderType();
    }

    public int getLightColor(float pPartialTick) {
        return LightTexture.FULL_BRIGHT;
    }
}
