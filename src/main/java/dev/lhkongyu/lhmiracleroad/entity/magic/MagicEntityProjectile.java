package dev.lhkongyu.lhmiracleroad.entity.magic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Objects;
import java.util.Optional;

public abstract class MagicEntityProjectile
        extends Magic {
    protected int range = 0;

    public int getRange() {
        return this.range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public abstract void trailParticles();

    public abstract void impactParticles(double x, double y, double z);

    public abstract Optional<SoundEvent> getImpactSound();

    public MagicEntityProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        HitResult hitResult;
        super.tick();
        if (this.tickCount > this.duration) {
            this.discard();
            return;
        }
        if (this.level.isClientSide) {
            this.trailParticles();
        }
        if ((hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity)).getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitResult)) {
            this.onHit(hitResult);
        }
        this.setPos(this.position().add(this.getDeltaMovement()));
        if (this.isInWater()) {
            Vec3 delta = this.getDeltaMovement();
            this.setDeltaMovement(delta.x, delta.y - this.gravity, delta.z);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level.isClientSide) {
            this.impactParticles(this.getX(), this.getY(), this.getZ());
            this.getImpactSound().ifPresent(this::doImpactSound);
            this.discard();
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return super.shouldBeSaved() && !Objects.equals(this.getRemovalReason(), Entity.RemovalReason.UNLOADED_TO_CHUNK);
    }

    protected void doImpactSound(SoundEvent sound) {
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 2.0f, 1.0f);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Duration", this.getDuration());
        compound.putInt("TickCount", this.tickCount);
        compound.putFloat("Damage", this.getDamage());
        compound.putInt("Range", this.getRange());
        compound.putInt("RangeAdditional", this.getRangeAdditional());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setDuration(compound.getInt("Duration"));
        this.tickCount = compound.getInt("TickCount");
        this.setDamage(compound.getFloat("Damage"));
        this.setRange(compound.getInt("Range"));
        this.setRangeAdditional(compound.getInt("RangeAdditional"));
        super.readAdditionalSaveData(compound);
    }
}
