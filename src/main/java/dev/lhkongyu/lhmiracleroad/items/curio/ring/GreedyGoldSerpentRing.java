package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.UUID;

/**
 * 贪婪金蛇戒指 的饰品功能
 */
public class GreedyGoldSerpentRing {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.SOUL_INCREASE, new AttributeModifier(UUID.fromString("018a834b-ae02-0854-07c5-ceb6aeee1113"), "", .15, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(Attributes.LUCK, new AttributeModifier(UUID.fromString("cbf2fbbe-2786-471e-ad63-8d53cbb645bf"), "", 5, AttributeModifier.Operation.ADDITION));
        builder.put(LHMiracleRoadAttributes.CRITICAL_HIT_RATE, new AttributeModifier(UUID.fromString("67f20c7d-07cb-4828-8111-24f7fc5a79b7"), "", 5, AttributeModifier.Operation.ADDITION));
        return new RingItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }
}
