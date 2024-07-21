package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * 防火戒指 的饰品功能
 */
public class FireResistanceRing {

    public static void setEffect(LivingEntity livingEntity){
        if (livingEntity.tickCount % 100 != 0) return;
        livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0, false, false, true));
    }

    public static void addEffect(LivingEntity livingEntity){
        livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0, false, false, true));
    }
}
