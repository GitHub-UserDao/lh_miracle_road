package dev.lhkongyu.lhmiracleroad.client.particle.bleed;

import dev.lhkongyu.lhmiracleroad.client.particle.AbstractSheetParticle;
import dev.lhkongyu.lhmiracleroad.registry.ParticleRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BleedParticle extends AbstractSheetParticle {
    private final SpriteSet sprites;

    public BleedParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {

        super(level, xCoord, yCoord, zCoord, spriteSet,xd, yd, zd);

        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 1f;
        this.scale(3.5f);
        this.lifetime = 25 + (int) (Math.random() * 20);
        sprites = spriteSet;
        this.gravity = 0.75F;
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    @Override
    protected ParticleRenderType renderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        if (this.onGround) {
            this.level.addParticle( (SimpleParticleType) ParticleRegistry.BLEED_GROUND.get(),
                    this.x, this.y, this.z,
                    0.0D, 0.0D, 0.0D);
            this.remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new BleedParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}