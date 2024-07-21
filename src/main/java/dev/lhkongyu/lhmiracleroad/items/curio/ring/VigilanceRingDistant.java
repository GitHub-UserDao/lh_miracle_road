package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

/**
 * 守望戒指·远 的饰品功能
 */
public class VigilanceRingDistant {

    private static final float maxDamageAddition = 30f;

    private static final int distanceDamageAddition = 200;

    public static void initVigilanceRingDistant(LivingEntity livingEntity,boolean isVigilanceRingDistant){
        livingEntity.removeEffect(MobEffects.DARKNESS);
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setVigilanceRingDistant(isVigilanceRingDistant);
        });
        if (isVigilanceRingDistant){
            livingEntity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false, true));
        }
    }

    public static void setVigilanceRingDistant(LivingEntity livingEntity){
        if (livingEntity.tickCount % 100 != 0){
            livingEntity.removeEffect(MobEffects.DARKNESS);
            return;
        }
        initVigilanceRingDistant(livingEntity,true);
    }

    public static void damageCount(LivingEntity source, LivingEntity target, LivingDamageEvent event){
        double distanceSqr = source.distanceToSqr(target.getPosition(0));
        float damage = (float) (Math.min(distanceSqr / distanceDamageAddition,maxDamageAddition) * 0.01 + 1);
        event.setAmount(event.getAmount() * damage);
    }
}
