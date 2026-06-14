package dev.lhkongyu.lhmiracleroad.client.particle.common;

import dev.lhkongyu.lhmiracleroad.client.particle.AbstractSheetParticle;
import dev.lhkongyu.lhmiracleroad.tool.particle.ParticleTool;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

public class PhotonParticle extends AbstractSheetParticle {

    public PhotonParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd,PhotonParticleOption option) {
        super(level, x, y, z, spriteSet, xd, yd, zd);
        this.scale(this.random.nextFloat() * 1.5F + option.getScale());
        this.lifetime = 15 + (int)(Math.random() * (double)15F) + option.getLifetime();
        this.gravity = -0.25F;
        this.hasPhysics = false;
//        Vector3f color = ParticleTool.RGBChangeVector3f(203,26,255);
        Vector3f color = option.getColor();
        this.setColor(color.x,color.y,color.z);
    }

    protected ParticleRenderType renderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getQuadSize(float partialTick) {
        float f = (partialTick + (float)this.age) / (float)this.lifetime;
        return this.quadSize * (1.0f - f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.random.nextInt(2) == 0) {
            ++this.age;
        }
        float lifeCoeff = (float)this.age / (float)this.lifetime;
        this.alpha = 1.0f - lifeCoeff;
    }

    public boolean isAlive() {
        return this.age < this.lifetime;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<PhotonParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(PhotonParticleOption option, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new PhotonParticle(level, x, y, z, this.sprite, dx, dy, dz,option);
        }
    }
}