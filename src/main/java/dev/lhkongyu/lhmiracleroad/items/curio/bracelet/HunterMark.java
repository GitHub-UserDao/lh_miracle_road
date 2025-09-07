package dev.lhkongyu.lhmiracleroad.items.curio.bracelet;

import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.registry.EffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class HunterMark {

    private static final int maxDamageAddition = 200;

    public static void setHunterMark(LivingEntity livingEntity, boolean isVigilanceRingDistant){
        if (livingEntity.tickCount % 20 != 0) return;
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setEquipHunterMark(isVigilanceRingDistant);
            if (playerCurio.getHunterMarkKillAmount() > 0) {
                livingEntity.addEffect(new MobEffectInstance(EffectRegistry.HUNTER_MARK.get(), 300, Math.min(playerCurio.getHunterMarkKillAmount() - 1, maxDamageAddition - 1), false, false, true));
            }
        });
    }
}
