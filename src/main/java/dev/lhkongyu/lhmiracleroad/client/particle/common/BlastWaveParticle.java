package dev.lhkongyu.lhmiracleroad.client.particle.common;

import dev.lhkongyu.lhmiracleroad.client.particle.AbstractBlastParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BlastWaveParticle extends AbstractBlastParticle {
    BlastWaveParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd, BlastParticleOption options) {
        super(level, x, y, z, spriteSet, xd, yd, zd, options);
        this.rCol = options.getColor().x();
        this.gCol = options.getColor().y();
        this.bCol = options.getColor().z();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<BlastParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(@NotNull BlastParticleOption options, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            BlastWaveParticle blastWaveParticle = new BlastWaveParticle(level, x, y, z, this.sprite, xSpeed, ySpeed, zSpeed, options);
            blastWaveParticle.setSpriteFromAge(this.sprite);
            return blastWaveParticle;
        }
    }
}
