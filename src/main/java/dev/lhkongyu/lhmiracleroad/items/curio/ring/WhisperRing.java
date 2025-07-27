package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurio;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.Optional;
import java.util.UUID;

public class WhisperRing {

    public static void equipWhisperRing(LivingEntity livingEntity, boolean isEquipWhisperRing){
        livingEntity.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
            playerCurio.setEquipWhisperRing(isEquipWhisperRing);
        });
    }

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(UUID.fromString("ff008da9-a580-c426-a561-95bf73fdef45"), "", .30, AttributeModifier.Operation.MULTIPLY_TOTAL));
        builder.put(LHMiracleRoadAttributes.JUMP, new AttributeModifier(UUID.fromString("d3eca14b-7993-bfbb-f12e-e2336d48bf6a"), "", .30, AttributeModifier.Operation.MULTIPLY_TOTAL));

        return new RingItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }

    public static boolean getIsEquipWhisperRing(Player player){
        Optional<PlayerCurio> playerCurioOptional = player.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).resolve();
        return playerCurioOptional.map(PlayerCurio::isEquipWhisperRing).orElse(false);
    }
}
