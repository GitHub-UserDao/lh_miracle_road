package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurio;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.Optional;
import java.util.UUID;

public class HuntingBowTalisman {

    public static TalismanItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.RANGED_DAMAGE, new AttributeModifier(UUID.fromString("47246aeb-2d2e-ee0e-fa27-113f0624b063"), "", .25, AttributeModifier.Operation.MULTIPLY_BASE));
        return new TalismanItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
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
        float maxDamageAddition = 25f;
        int distanceDamageAddition = 10;
        double distanceSqr = source.distanceToSqr(target.getPosition(0));
        float damage = (float) (Math.min(distanceSqr / distanceDamageAddition,maxDamageAddition) * 0.01 + 1);
        event.setAmount(event.getAmount() * damage);
    }

}
