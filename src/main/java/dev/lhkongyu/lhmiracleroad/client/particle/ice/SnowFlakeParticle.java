package dev.lhkongyu.lhmiracleroad.client.particle.ice;

import dev.lhkongyu.lhmiracleroad.client.particle.AbstractSheetParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SnowFlakeParticle extends AbstractSheetParticle {
    private final SpriteSet sprites;

    public SnowFlakeParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {

        super(level, xCoord, yCoord, zCoord, spriteSet,xd, yd, zd);

        this.friction = 0.9f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= .8f;
        this.scale(this.random.nextFloat() * .5f  + .8F);
        this.lifetime = 30 + (int) (Math.random() * 20);
        sprites = spriteSet;
        this.gravity = 0.001F;
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    @Override
    protected ParticleRenderType renderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
            this.xd += this.random.nextFloat() / 5000.0F * (float) (this.random.nextBoolean() ? 1 : -1);
            this.zd += this.random.nextFloat() / 5000.0F * (float) (this.random.nextBoolean() ? 1 : -1);
            this.yd -= this.gravity;
            xd *=friction;
            zd *=friction;
            yd *=friction;
            this.move(this.xd, this.yd, this.zd);
            if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
            }
            if (this.age > lifetime / 2 && gravity < 0.225F)
                gravity += .001f;
        } else {
            this.remove();
        }
        this.setSpriteFromAge(this.sprites);

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
            return new SnowFlakeParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
