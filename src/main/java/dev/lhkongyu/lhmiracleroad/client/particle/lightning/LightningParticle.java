package dev.lhkongyu.lhmiracleroad.client.particle.lightning;

import dev.lhkongyu.lhmiracleroad.client.particle.AbstractSheetParticle;
import dev.lhkongyu.lhmiracleroad.client.particle.fire.FireBottomParticle;
import dev.lhkongyu.lhmiracleroad.client.particle.fire.FireParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LightningParticle extends AbstractSheetParticle {

    public LightningParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, x, y, z, spriteSet, xd, yd, zd);
        this.scale(this.random.nextFloat() + 0.75F);
        this.lifetime = 10 + (int)(Math.random() * 5.0F);
        this.gravity = -0.1F;
        this.hasPhysics = false;
    }

    @Override
    protected ParticleRenderType renderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void tick() {
        super.tick();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new LightningParticle(level, x, y, z, this.sprite, dx, dy, dz);
        }
    }
}
