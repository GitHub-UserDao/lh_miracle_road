package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import dev.lhkongyu.lhmiracleroad.capability.PlayerCurio;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * 圣光戒指 的饰品功能
 */
public class RadianceRing {

    public static void equipRadianceRing(LivingEntity livingEntity,boolean isEquipRadianceRing){
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setEquipRadianceRing(isEquipRadianceRing);
        });
        if (isEquipRadianceRing){
            livingEntity.setTicksFrozen(0);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0, false, false, true));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 300, 0, false, false, true));
        }
    }

    public static void setEquipRadianceRing(LivingEntity livingEntity){
        if (livingEntity.tickCount % 100 != 0){
            livingEntity.setTicksFrozen(0);
            return;
        }
        equipRadianceRing(livingEntity,true);
    }

    public static boolean getIsEquipRadianceRing(Player player){
        Optional<PlayerCurio> playerCurioOptional = player.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).resolve();
        return playerCurioOptional.map(PlayerCurio::isEquipRadianceRing).orElse(false);
    }

    public static void resettingEffect(LivingEntity livingEntity){
        livingEntity.removeAllEffects();
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setEquipRadianceRing(true);
        });

        equipRadianceRing(livingEntity,true);
    }

}
