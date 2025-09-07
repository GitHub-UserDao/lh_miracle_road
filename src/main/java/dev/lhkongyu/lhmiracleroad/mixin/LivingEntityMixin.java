package dev.lhkongyu.lhmiracleroad.mixin;


import dev.lhkongyu.lhmiracleroad.attributes.AttributeInstanceAccess;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.items.curio.ring.RadianceRing;
import dev.lhkongyu.lhmiracleroad.items.curio.ring.WhisperRing;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Shadow @Nullable public abstract AttributeInstance getAttribute(Attribute p_21052_);

	@Shadow public abstract Collection<MobEffectInstance> getActiveEffects();

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

			if(WhisperRing.getIsEquipWhisperRing(player)){
				reduction += 20;
			}
		}
		return reduction;
	}

	@Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
	private void injectAtDrop(DamageSource source, CallbackInfo ci) {
		int hp = (int) LHMiracleRoadTool.getAttributeValue(this.getAttribute(Attributes.MAX_HEALTH));
		int atk = (int) LHMiracleRoadTool.getAttributeValue(this.getAttribute(Attributes.ATTACK_DAMAGE));
		int arm = (int) LHMiracleRoadTool.getAttributeValue(this.getAttribute(Attributes.ARMOR));
		int atou = (int) LHMiracleRoadTool.getAttributeValue(this.getAttribute(Attributes.ARMOR_TOUGHNESS));
		int buff = (int) this.getActiveEffects().stream().map(MobEffectInstance::getEffect).filter(MobEffect::isBeneficial).count();
        if (source.getEntity() instanceof ServerPlayer killer) {
			killer.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
				LivingEntity target = ((LivingEntity) (Object) this);
				entityDroppedXp = Math.max(entityDroppedXp,15);
//				int occupationExperience = (int) (entityDroppedXp * LHMiracleRoadConfig.COMMON.EMPIRICAL_BASE_MULTIPLIER.get());

				int expValue = LHMiracleRoadTool.evaluateFormula(LHMiracleRoadConfig.COMMON.EXPERIENCE_ACQUISITION_FORMULA.get(),entityDroppedXp,hp,atk,arm,atou,buff);
				AttributeInstance attributeInstance = killer.getAttribute(LHMiracleRoadAttributes.SOUL_INCREASE);
				if (attributeInstance != null) {
					expValue = (int) (expValue * attributeInstance.getValue());
				}
				int killerSoulStart = playerOccupationAttribute.getOccupationExperience();
				playerOccupationAttribute.addOccupationExperience(expValue);
				LHMiracleRoadTool.getSoulParticle((ServerLevel) killer.level(), killer,expValue,200,2,1000,target);
				LHMiracleRoadTool.synchronizationSoul(playerOccupationAttribute.getOccupationExperience(),killer,killerSoulStart);

				if(LHMiracleRoadConfig.COMMON.IS_EXP_SHARING.get()) {
					double range = 64.0;
					ServerLevel level = killer.serverLevel();
					List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(
							ServerPlayer.class,
							killer.getBoundingBox().inflate(range, range, range),
							p -> !p.equals(killer)
					);

					if (!nearbyPlayers.isEmpty()) {
						int sharedExp = (int) (expValue * LHMiracleRoadConfig.COMMON.EXP_SHARING_PERCENTAGE.get());

						for (ServerPlayer p : nearbyPlayers) {
							p.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(attr -> {
								int pSoulStart = attr.getOccupationExperience();
								attr.addOccupationExperience(sharedExp);
								LHMiracleRoadTool.synchronizationSoul(attr.getOccupationExperience(),p,pSoulStart);
							});
							LHMiracleRoadTool.getSoulParticle((ServerLevel) p.level(), p, expValue, (int) (30 * LHMiracleRoadConfig.COMMON.EXP_SHARING_PERCENTAGE.get()), 2, 1000, target);
						}
					}
				}

			});
		}
	}

	@ModifyArg(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"), index = 2)
	private int injectAtDropXp(int droppedXp) {
		entityDroppedXp = droppedXp;
		return droppedXp;
	}

	@Inject(at = @At(value = "HEAD"), method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", cancellable = true)
	private void addEffect(MobEffectInstance p_147208_, Entity p_147209_, CallbackInfoReturnable<Boolean> cir) {
		if (((LivingEntity) (Object) this) instanceof Player player) {
			if (RadianceRing.getIsEquipRadianceRing(player)) {
				if (p_147208_.getEffect().isBeneficial()) return;
				cir.cancel();
				cir.setReturnValue(false);
			}
		}
	}
}

