package dev.lhkongyu.lhmiracleroad.entity.magic;

import dev.lhkongyu.lhmiracleroad.client.particle.common.BlastParticleOption;
import dev.lhkongyu.lhmiracleroad.client.particle.common.ElementParticleOption;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypes;
import dev.lhkongyu.lhmiracleroad.registry.EntityRegistry;
import dev.lhkongyu.lhmiracleroad.registry.ParticleRegistry;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.particle.ParticleTool;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LightningBoltEntity extends MagicEntityProjectile {

    public final int DURATION = 14;

    private final List<Entity> victims = new ArrayList<Entity>();
    private LightningBolt lightningBolt;

    public LightningBoltEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.setGravity(gravity);
        this.setDuration(DURATION);
        this.setAttract(isAttract);
    }

    public LightningBoltEntity(Level level, LivingEntity livingEntity, LightningBolt lightningBolt) {
        this(EntityRegistry.LIGHTNING_BOLT.get(), level);
        this.setOwner(livingEntity);
        this.setDuration(DURATION);
        this.lightningBolt = lightningBolt;
        this.lightningBolt.setVisualOnly(true);
        this.lightningBolt.setDamage(this.damage);
    }

    @Override
    public void trailParticles() {
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        Vector3f redColor = LHMiracleRoadTool.RGBChangeVector3f(238, 225, 114);
        ElementParticleOption element = new ElementParticleOption(redColor);
        BlastParticleOption blast = new BlastParticleOption(redColor, (float)this.range, 1.0F,  1);

        ParticleTool.nearbyPlayers(this.level, x, y, z).forEach(player -> {
            ((ServerLevel)this.level).sendParticles(player, element, true, x, y, z, 15, 0.2, 1.0F, 0.2, 0.2);
            ((ServerLevel)this.level).sendParticles(player, blast, true, x, y + 0.25F, z, 1,0.0F,0.0F, 0.0F, 0.0F);
            ((ServerLevel)this.level).sendParticles(player, (SimpleParticleType) ParticleRegistry.LIGHTNING.get(), true, x, y, z, 30, 0.075, 0.125, 0.075, 0.075);
        });
    }

    @Override
    public Optional<SoundEvent> getImpactSound() {
        return Optional.empty();
    }

    @Override
    public void tick() {
        if (this.tickCount > this.duration) {
            this.discard();
            return;
        }
        this.trailParticles();
        if (!this.level.isClientSide) {
            if (this.tickCount == 2) {
                this.impactParticles(this.position().x, this.position().y, this.position().z);
            }
            if (this.tickCount > 1 && this.tickCount % 2 == 0) {
                this.explosionAttack();
                this.lightningAttack();
            }
        }
        this.setPos(this.position().add(this.getDeltaMovement()));
    }

    private void explosionAttack() {
        AABB aabb = this.getBoundingBox().inflate(this.getRange(), 0.0, this.getRange());
        aabb = aabb.move(0.0, -this.getRange(), 0.0);
        float explosionRadius = this.getRange() * this.getRangeAdditional();
        float explosionRadiusSqr = explosionRadius * explosionRadius;
        List<Entity> targets = this.level.getEntities(this, aabb).stream()
                .filter(target -> !this.victims.contains(target) && this.canHitEntity(target))
                .toList();
        for (Entity target : targets) {
            double distanceSqr = target.distanceToSqr(this.position());
            if (!(distanceSqr <= (double)explosionRadiusSqr)) continue;
            if (this.lightningBolt != null) {
                target.thunderHit((ServerLevel)this.level(), this.lightningBolt);
            }
            LHMiracleRoadTool.attackMagicHurt((LivingEntity) this.getOwner(),(LivingEntity) target, SpellDamageTypes.LIGHTNING_MAGIC , this.damage);
            this.victims.add(target);
        }
    }

    private void lightningAttack() {
        AABB aabb = this.getBoundingBox();
        List<Entity> targets = this.level.getEntities(this, aabb).stream()
                .filter(target -> !this.victims.contains(target) && this.canHitEntity(target))
                .toList();
        for (Entity target : targets) {
            if (this.lightningBolt != null) {
                target.thunderHit((ServerLevel)this.level(), this.lightningBolt);
            }
            LHMiracleRoadTool.attackMagicHurt((LivingEntity) this.getOwner(), (LivingEntity) target, SpellDamageTypes.LIGHTNING_MAGIC , this.damage);
            this.victims.add(target);
        }
    }
}
