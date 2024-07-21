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
 * 无尽欲望 的饰品功能
 */
public class EndlessDesire {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.CRITICAL_HIT_DAMAGE, new AttributeModifier(UUID.fromString("0172ce72-519c-034a-0b13-0741bb3cac56"), "", .5, AttributeModifier.Operation.ADDITION));
        builder.put(LHMiracleRoadAttributes.INJURED, new AttributeModifier(UUID.fromString("1f3b0a36-90a7-4ca9-ea21-a7ffdadf7b95"), "", .20, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return new RingItem(new Item.Properties().rarity(Rarity.UNCOMMON),builder.build());
    }

}
