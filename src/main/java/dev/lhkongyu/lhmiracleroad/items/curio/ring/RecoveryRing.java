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
 * 恢复戒指 的饰品功能
 */
public class RecoveryRing {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.HEALING, new AttributeModifier(UUID.fromString("b90dca3b-f02d-e9cc-b679-54beff1556dd"), "", 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return new RingItem(new Item.Properties().rarity(Rarity.RARE),builder.build());
    }
}
