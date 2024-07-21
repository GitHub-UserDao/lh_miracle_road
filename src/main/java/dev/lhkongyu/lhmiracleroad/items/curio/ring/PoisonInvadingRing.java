package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * 侵毒戒指 的饰品功能
 */
public class PoisonInvadingRing {

    public static void removeEffect(LivingEntity livingEntity){
        livingEntity.removeEffect(MobEffects.POISON);
    }
}
