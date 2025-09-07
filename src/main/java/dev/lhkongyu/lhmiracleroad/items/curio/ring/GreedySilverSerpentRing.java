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
 * 贪婪银蛇戒指 的饰品功能
 */
public class GreedySilverSerpentRing {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.SOUL_INCREASE, new AttributeModifier(UUID.fromString("7bd3dcff-f220-1053-1047-bfbefbee7d7e"), "", .3, AttributeModifier.Operation.MULTIPLY_BASE));
        return new RingItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }
}
