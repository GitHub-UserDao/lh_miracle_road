package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.UUID;

public class FlameTalisman {

    public static TalismanItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.ABNORMAL_BURN_BUILDUP, new AttributeModifier(UUID.fromString("4bcfac66-bb9b-420b-9628-44bb606c0ac9"), "", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.ABNORMAL_BURN_DAMAGE, new AttributeModifier(UUID.fromString("9a52ab78-8305-4fed-9c27-2c3521321828"), "", 0.08, AttributeModifier.Operation.MULTIPLY_BASE));
        return new TalismanItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }
}
