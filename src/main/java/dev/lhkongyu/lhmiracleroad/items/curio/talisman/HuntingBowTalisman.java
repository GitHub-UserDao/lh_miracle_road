package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import dev.lhkongyu.lhmiracleroad.capability.PlayerCurio;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.Optional;
import java.util.UUID;

public class HuntingBowTalisman {

    public static TalismanItem addAttributeModifier(){
        return new TalismanItem(new Item.Properties().rarity(Rarity.RARE),null);
    }

    public static void equipHuntingBowTalisman(LivingEntity livingEntity, boolean isEquipHuntingBowTalisman){
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setEquipHuntingBowTalisman(isEquipHuntingBowTalisman);
        });
    }

    public static boolean getIsEquipHuntingBowTalisman(Player player){
        Optional<PlayerCurio> playerCurioOptional = player.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).resolve();
        return playerCurioOptional.map(PlayerCurio::isEquipHuntingBowTalisman).orElse(false);
    }
    public static void damageCount(LivingEntity source, LivingEntity target, LivingDamageEvent event){
        float maxDamageAddition = 16f;
        int distanceDamageAddition = 125;
        double distanceSqr = source.distanceToSqr(target.getPosition(0));
        float damage = (float) (Math.min(distanceSqr / distanceDamageAddition,maxDamageAddition) * 0.01 + 1);
        event.setAmount(event.getAmount() * damage);
    }

}
