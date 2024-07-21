package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

/**
 * 守望戒指·近 的饰品功能
 */
public class VigilanceRingNear {

    //最大伤害加成
    private static final float maxDamageAddition = 10f;

    //伤害衰减距离
    private static final int distanceDamageAttenuation = 5;

    public static void initVigilanceRingNear(LivingEntity livingEntity,boolean isVigilanceRingNear){
        livingEntity.removeEffect(MobEffects.DARKNESS);
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setVigilanceRingNear(isVigilanceRingNear);
        });
        if (isVigilanceRingNear){
            livingEntity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false, true));
        }
    }

    public static void setVigilanceRingNear(LivingEntity livingEntity){
        if (livingEntity.tickCount % 100 != 0) {
            livingEntity.removeEffect(MobEffects.DARKNESS);
            return;
        }
        initVigilanceRingNear(livingEntity,true);
    }

    public static void damageCount(LivingEntity source, LivingEntity target, LivingDamageEvent event){
        double distanceSqr = source.distanceToSqr(target.getPosition(0));
        float damage = 0;
        double attenuation = distanceSqr / distanceDamageAttenuation;
        if (attenuation <= 1){
            damage = (float) (maxDamageAddition * 0.01 + 1);
        }else {
            damage = (float) Math.max((maxDamageAddition - attenuation) * 0.01 + 1, 1);
        }

        event.setAmount(event.getAmount() * damage);
    }
}
