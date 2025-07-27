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

public class ConsecratedCombatPlume {

    public static TalismanItem addAttributeModifier(){
        return new TalismanItem(new Item.Properties().rarity(Rarity.EPIC),null);
    }

    public static void equipConsecratedCombatPlume(LivingEntity livingEntity, boolean isEquipConsecratedCombatPlume){
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setEquipConsecratedCombatPlume(isEquipConsecratedCombatPlume);
        });
    }

    public static boolean getIsEquipConsecratedCombatPlume(Player player){
        Optional<PlayerCurio> playerCurioOptional = player.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).resolve();
        return playerCurioOptional.map(PlayerCurio::isEquipConsecratedCombatPlume).orElse(false);
    }

    public static void damageCount(LivingEntity source, LivingDamageEvent event){
        if (source.getHealth() == source.getMaxHealth()){
            float damage = 1.2f;
            event.setAmount(event.getAmount() * damage);
        }
    }

}
