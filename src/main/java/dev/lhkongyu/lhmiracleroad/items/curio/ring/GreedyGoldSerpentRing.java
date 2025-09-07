package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.UUID;

/**
 * 贪婪金蛇戒指 的饰品功能
 */
public class GreedyGoldSerpentRing {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.SOUL_INCREASE, new AttributeModifier(UUID.fromString("018a834b-ae02-0854-07c5-ceb6aeee1113"), "", .5, AttributeModifier.Operation.MULTIPLY_BASE));
        return new RingItem(new Item.Properties().rarity(Rarity.UNCOMMON),builder.build());
    }
}
