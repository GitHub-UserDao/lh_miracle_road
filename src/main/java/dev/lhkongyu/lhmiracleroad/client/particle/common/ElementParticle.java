package dev.lhkongyu.lhmiracleroad.client.particle.common;

import dev.lhkongyu.lhmiracleroad.client.particle.AbstractSheetParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ElementParticle extends AbstractSheetParticle {
    public ElementParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd, ElementParticleOption options) {
        super(level, x, y, z, spriteSet, xd, yd, zd);
        this.gravity = 1.5F;
        this.scale(3.0F);
        this.lifetime = (int)(24.0F / (Math.random() * 0.8 + 0.2F));
        this.setColor(options.getColor().x(), options.getColor().y(), options.getColor().z());
    }

    public int getLightColor(float partialTick) {
        int i = super.getLightColor(partialTick);
        int skyLight = 240;
        int blockLight = i >> 16 & 255;
        return skyLight | blockLight << 16;
    }

    public ParticleRenderType renderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public float getQuadSize(float partialTick) {
        float f = ((float)this.age + partialTick) / (float)this.lifetime;
        return this.quadSize * (1.0F - f * f);
    }

    public void tick() {
        if (this.onGround) {
            this.yd *= -(0.6F + this.random.nextFloat() * 0.2F);
        }
        super.tick();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<ElementParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(ElementParticleOption options, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new ElementParticle(level, x, y, z, this.sprite, dx, dy, dz, options);
        }
    }
}
