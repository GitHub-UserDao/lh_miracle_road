package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.UUID;

public class TaintedGoddessStatue {

    public static TalismanItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.ABNORMAL_FROST_BUILDUP, new AttributeModifier(UUID.fromString("51dff2d0-06d5-4c8d-9cca-eb58ad9fbe85"), "", 0.35, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.ABNORMAL_POISON_BUILDUP, new AttributeModifier(UUID.fromString("32196a24-285c-4cf5-aa17-57e96d5f752f"), "", 0.35, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.ABNORMAL_BURN_BUILDUP, new AttributeModifier(UUID.fromString("277754ac-1e81-4460-a021-e29d191227d2"), "", 0.35, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.ABNORMAL_BLEED_BUILDUP, new AttributeModifier(UUID.fromString("8a64d211-91d5-4451-98e2-bc47e6b7488d"), "", 0.35, AttributeModifier.Operation.MULTIPLY_BASE));

        builder.put(LHMiracleRoadAttributes.ABNORMAL_DAMAGE, new AttributeModifier(UUID.fromString("8d47d419-726e-475d-a004-5feb22238c95"), "", 0.08, AttributeModifier.Operation.MULTIPLY_BASE));
        return new TalismanItem(new Item.Properties().rarity(Rarity.UNCOMMON),builder.build());
    }
}
