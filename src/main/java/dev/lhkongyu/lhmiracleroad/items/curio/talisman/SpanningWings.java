package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import dev.lhkongyu.lhmiracleroad.capability.PlayerCurio;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.Optional;

public class SpanningWings {

    public static TalismanItem addAttributeModifier(){
        return new TalismanItem(new Item.Properties().rarity(Rarity.EPIC),null);
    }

    public static void equipSpanningWings(LivingEntity livingEntity, boolean isEquipSpanningWings){
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setEquipSpanningWings(isEquipSpanningWings);
        });
    }

    public static boolean getIsEquipSpanningWings(Player player){
        Optional<PlayerCurio> playerCurioOptional = player.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).resolve();
        return playerCurioOptional.map(PlayerCurio::isEquipSpanningWings).orElse(false);
    }

    public static void damageCount(LivingEntity source, LivingDamageEvent event){
        float maxDamageAddition = 0.30f;
//        float damageAddition = 0.003f;
        float healthPercentage = (1 - source.getHealth() / source.getMaxHealth()) * 100;
        float damage = 1;
        if (healthPercentage >= 80) {
            damage = 1 + maxDamageAddition;
        }
//        else {
//            damage = 1 + (healthPercentage * damageAddition);
//        }
        event.setAmount(event.getAmount() * damage);
    }
}
