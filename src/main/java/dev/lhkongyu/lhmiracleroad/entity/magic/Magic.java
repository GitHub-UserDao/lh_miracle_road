package dev.lhkongyu.lhmiracleroad.entity.magic;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class Magic extends Projectile {
    protected boolean isAttract = true;
    protected Level level;
    protected float damage;
    protected double gravity = 0.05;
    protected int rangeAdditional = 1;
    protected int duration = 200;

    public boolean isAttract() {
        return this.isAttract;
    }

    public void setAttract(boolean attract) {
        this.isAttract = attract;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public double getGravity() {
        return this.gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public int getRangeAdditional() {
        return this.rangeAdditional;
    }

    public void setRangeAdditional(int rangeAdditional) {
        this.rangeAdditional = rangeAdditional;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return this.duration;
    }

    protected Magic(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.level = level;
    }

    protected void defineSynchedData() {
    }

    protected boolean isBelongTeammate(Entity target) {
        if (this.getOwner() == null || target == this.getOwner() || this.getOwner().isAlliedTo(target)) {
            return false;
        }
        if (!(target instanceof TamableAnimal tamable)) {
            return true;
        }
        LivingEntity owner = tamable.getOwner();
        return owner == null || !owner.equals(this.getOwner());
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return this.isBelongTeammate(target) && super.canHitEntity(target) && target.isAlive();
    }
}
