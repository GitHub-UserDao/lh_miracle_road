package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import dev.lhkongyu.lhmiracleroad.capability.PlayerCurio;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class HeartOfBloodLust {

    private static final String TIMER_TAG = "HeartOfBloodLustTimer";

    public static void equipHeartOfBloodLust(LivingEntity livingEntity, boolean isEquipHeartOfBloodLust){
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setEquipHeartOfBloodLust(isEquipHeartOfBloodLust);
        });
    }

    public static boolean getIsEquipHeartOfBloodLust(Player player){
        Optional<PlayerCurio> playerCurioOptional = player.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).resolve();
        return playerCurioOptional.map(PlayerCurio::isEquipHeartOfBloodLust).orElse(false);
    }

    public static void bloodLust(LivingEntity entity){
        if (entity.level().isClientSide) return;

        CompoundTag data = entity.getPersistentData();
        int timer = data.getInt(TIMER_TAG);
        timer++;
        if (timer >= 100) {
            restoreHp(entity);
            timer = 0;
        }
        data.putInt(TIMER_TAG, timer);
    }

    public static void restoreHp(LivingEntity entity){
        float toAdd = 2f;
        int hpA = (int) Math.max(entity.getMaxHealth() * .025f,1);
        int absorptionUpperLimit = (int) (entity.getMaxHealth() * 0.45F);
        if (entity.getHealth() >= entity.getMaxHealth()) {
            float absorption = entity.getAbsorptionAmount();
            if (absorption < absorptionUpperLimit) {
                float newAbsorption = Math.min(absorption + toAdd +hpA, absorptionUpperLimit);
                entity.setAbsorptionAmount(newAbsorption);
            }
            return;
        }
        int hpB = (int) Math.max((entity.getMaxHealth() - entity.getHealth()) * .025f,1);
        entity.heal(toAdd + hpA + hpB);
    }

    public static void killRestoreHp(LivingEntity entity){
        float toAdd = 4f;
        int hpA = (int) Math.max(entity.getMaxHealth() * .025f,1);
        int absorptionUpperLimit = (int) (entity.getMaxHealth() * 0.45F);
        float absorption = entity.getAbsorptionAmount();
        if (absorption < absorptionUpperLimit) {
            float newAbsorption = Math.min(absorption + toAdd + hpA, absorptionUpperLimit);
            entity.setAbsorptionAmount(newAbsorption);
        }
        if (entity.getHealth() >= entity.getMaxHealth())  return;

        int hpB = (int) Math.max((entity.getMaxHealth() - entity.getHealth()) * .1f,1);
        entity.heal(toAdd + hpB);
    }
}
