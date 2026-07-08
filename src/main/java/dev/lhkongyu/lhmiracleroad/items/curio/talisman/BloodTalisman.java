package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.UUID;

public class BloodTalisman {

    public static TalismanItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.ABNORMAL_BLEED_BUILDUP, new AttributeModifier(UUID.fromString("8b2a6bbc-b590-41da-8d72-4a4cecefc395"), "", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.ABNORMAL_BLEED_DAMAGE, new AttributeModifier(UUID.fromString("65fa299e-4727-48b3-909d-f6854475b0ca"), "", 0.08, AttributeModifier.Operation.MULTIPLY_BASE));
        return new TalismanItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }
}
