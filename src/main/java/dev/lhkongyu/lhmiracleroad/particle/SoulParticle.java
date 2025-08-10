package dev.lhkongyu.lhmiracleroad.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SoulParticle extends TextureSheetParticle {

    protected final SpriteSet sprite;

    public int ownerId;

    public SoulParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd,int ownerId) {
        super(level, x, y, z, xd, yd, zd);
        this.sprite = spriteSet;
        this.ownerId = ownerId;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.scale(this.random.nextFloat() * 1.75f + 1f);
        this.hasPhysics = false;

        Entity owner = getEntity();
        if (owner != null) {
            Vec3 ownerCenter = getOwnerPosition(owner);
            double dx = x - ownerCenter.x;
            double dy = y - ownerCenter.y;
            double dz = z - ownerCenter.z;
            double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);

            this.lifetime = Math.max((int) distance * 3 + random.nextInt(5), 30 + random.nextInt(5));
        } else {
            this.lifetime = 20 + random.nextInt(5);
            this.gravity = -0.1F;
        }

        this.setSpriteFromAge(spriteSet);

    }

    public Entity getEntity() {
        return this.ownerId == -1 ? null : this.level.getEntity(this.ownerId);
    }

    public Vec3 getOwnerPosition(Entity owner) {

        return owner.position().add(0,owner.getBbHeight() * .5f,0);
    }


    @Override
    public void tick() {
        super.tick();
        Entity owner = getEntity();
        if (owner != null) {
            Vec3 ownerPos = getOwnerPosition(owner);
            Vec3 currentPos = new Vec3(x, y, z);

            double distance = currentPos.distanceTo(ownerPos);

            if (age > lifetime * .5) {
                double baseSpeedFactor = 0.5 + (distance / 50.0) * 0.5;
                double speedMultiplier = 1.8 * baseSpeedFactor;

                Vec3 direction = ownerPos.subtract(currentPos).normalize();
                Vec3 finalDirection = direction.scale(speedMultiplier);

                this.xd = this.xd * 0.2 + finalDirection.x;
                this.yd = this.yd * 0.2 + finalDirection.y;
                this.zd = this.zd * 0.2 + finalDirection.z;

                double accelerationFactor = 1.0 + (distance / 200.0);
                this.xd *= 1.075 * accelerationFactor;
                this.yd *= 1.075 * accelerationFactor;
                this.zd *= 1.075 * accelerationFactor;

                double maxSpeed = 1.0 + (distance / 100.0) * 0.5;
                Vec3 velocity = new Vec3(xd, yd, zd);
                if (velocity.length() > maxSpeed) {
                    velocity = velocity.normalize().scale(maxSpeed);
                    this.xd = velocity.x;
                    this.yd = velocity.y;
                    this.zd = velocity.z;
                }

                if (distance < 0.5) {
                    this.remove();
                    return;
                }
            }
        }
        this.setSpriteFromAge(this.sprite);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float p_107564_) {
        return 255;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SoulParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SoulParticleOption particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new SoulParticle(level, x, y, z, this.sprite, dx, dy, dz, particleType.getOwnerId());
        }
    }

}
