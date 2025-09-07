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
 * 力量戒指 的饰品功能
 */
public class RingStrength {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("c69662c9-210e-b8b4-2c49-6ca13cc4237b"), "", .12, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.RANGED_DAMAGE, new AttributeModifier(UUID.fromString("1bfb7f78-e834-95b7-be48-8e7f7615a94b"), "", .12, AttributeModifier.Operation.MULTIPLY_BASE));
        return new RingItem(new Item.Properties().rarity(Rarity.RARE),builder.build());
    }
}
