package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import dev.lhkongyu.lhmiracleroad.capability.PlayerCurio;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import dev.lhkongyu.lhmiracleroad.registry.EffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
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

    public static void addSpanningWingsEffect(LivingEntity entity){
        if (entity.tickCount % 100 != 0) return;
        float healthPercentage = (1 - entity.getHealth() / entity.getMaxHealth()) * 100;
        if (healthPercentage >= 80){
            entity.addEffect(new MobEffectInstance(EffectRegistry.SPANNING_WINGS.get(), 1200, 1, false, false, true));
        }
    }

}
