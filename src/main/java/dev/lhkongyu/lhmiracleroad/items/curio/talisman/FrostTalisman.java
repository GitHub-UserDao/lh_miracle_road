package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.UUID;

public class FrostTalisman {

    public static TalismanItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.ABNORMAL_FROST_BUILDUP, new AttributeModifier(UUID.fromString("af6b60eb-c678-4c6c-91c2-9ce3b90959fa"), "", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.ABNORMAL_FROST_DAMAGE, new AttributeModifier(UUID.fromString("96afa293-46b3-4022-af12-e0d0da7f2266"), "", 0.08, AttributeModifier.Operation.MULTIPLY_BASE));
        return new TalismanItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }
}
