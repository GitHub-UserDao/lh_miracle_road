package dev.lhkongyu.lhmiracleroad.mixin;


import dev.lhkongyu.lhmiracleroad.attributes.AttributeInstanceAccess;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Unique
	private int entityDroppedXp = 0;

	@ModifyVariable(method = "hurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private float modifyVariableAtDamage(float damage, DamageSource source) {
		if (damage < 0) {
			return damage;
		}

		if (source.getEntity() instanceof Player player) {
			if (source.is(DamageTypeTags.IS_PROJECTILE)) {
				var attribute = ((AttributeInstanceAccess) player.getAttribute(LHMiracleRoadAttributes.RANGED_DAMAGE));
				damage = (float) attribute.computeIncreasedValueForInitial(damage);
			}
			if(!player.level().isClientSide) {
				double criticalHitRate = player.getAttribute(LHMiracleRoadAttributes.CRITICAL_HIT_RATE).getValue();
				double criticalHitDamage = player.getAttribute(LHMiracleRoadAttributes.CRITICAL_HIT_DAMAGE).getValue();
				if (LHMiracleRoadTool.percentageProbability(criticalHitRate)) {
					damage = (float) (damage * criticalHitDamage);
					LivingEntity entity = (LivingEntity) (Object) this;
					int particleCount = (int) (15 * entity.getBbWidth() * entity.getBbHeight()); // 基于实体体积调整粒子数量
					ServerLevel serverLevel = (ServerLevel) player.level();
					serverLevel.sendParticles((ServerPlayer) player, ParticleTypes.CRIT, true, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(), particleCount, entity.getBbWidth() / 2, entity.getBbHeight() / 2, entity.getBbWidth() / 2,0.2);
					serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.5F, 1.0F);
				}
			}
		}
		return damage;
	}

	@ModifyVariable(method = "heal", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private float modifyVariableAtHeal(float amount) {
		if (amount < 0) {
			return amount;
		}

		if (((LivingEntity) (Object) this) instanceof Player player) {
			var attribute = ((AttributeInstanceAccess) player.getAttribute(LHMiracleRoadAttributes.HEALING));
			amount = (float) attribute.computeIncreasedValueForInitial(amount);
		}
		return amount;
	}

	@Inject(method = "getJumpPower", at = @At("RETURN"), cancellable = true)
	private void injectAtGetJumpVelocity(CallbackInfoReturnable<Float> cir) {
		if (((LivingEntity) (Object) this) instanceof Player player) {
			var attribute = ((AttributeInstanceAccess) player.getAttribute(LHMiracleRoadAttributes.JUMP));
			cir.setReturnValue((float) attribute.computeIncreasedValueForInitial(cir.getReturnValueF()));
		}
	}

	@ModifyVariable(method = "calculateFallDamage", at = @At("STORE"), ordinal = 2)
	private float modifyVariableAtComputeFallDamage(float reduction) {
		if (((LivingEntity) (Object) this) instanceof Player player) {
			var attribute = ((AttributeInstanceAccess) player.getAttribute(LHMiracleRoadAttributes.JUMP));
			reduction += (attribute.computeIncreasedValueForInitial(1.0f) - 1.0f) * 10.0f;
		}
		return reduction;
	}

	@Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
	private void injectAtDrop(DamageSource source, CallbackInfo ci) {
		if (source.getEntity() instanceof ServerPlayer player) {
			player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
				playerOccupationAttribute.addOccupationExperience((int) (entityDroppedXp * LHMiracleRoadConfig.COMMON.EMPIRICAL_BASE_MULTIPLIER.get()));
			});
		}

	}

	@ModifyArg(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"), index = 2)
	private int injectAtDropXp(int droppedXp) {
		entityDroppedXp = droppedXp;
		return droppedXp;
	}
}
