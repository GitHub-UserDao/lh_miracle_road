package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * 冰霜戒指 的饰品功能
 */
public class FrostRing {

    public static void ignoreFrost(LivingEntity livingEntity){
        livingEntity.setTicksFrozen(0);
    }
}
