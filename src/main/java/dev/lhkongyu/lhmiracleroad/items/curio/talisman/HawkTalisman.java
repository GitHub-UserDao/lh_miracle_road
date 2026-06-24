package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import net.minecraft.world.entity.LivingEntity;

public class HawkTalisman {

    private static final float AIR_DAMAGE = 1.12f;

    public static void equipHawkTalisman(LivingEntity livingEntity, boolean isEquipHawkTalisman){
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setEquipHawkTalisman(isEquipHawkTalisman);
        });
    }

    public static float airDamageAdd(LivingEntity source,float amount){
        if (!source.onGround()){
            amount = amount * AIR_DAMAGE;
        }
        return amount;
    }
}
