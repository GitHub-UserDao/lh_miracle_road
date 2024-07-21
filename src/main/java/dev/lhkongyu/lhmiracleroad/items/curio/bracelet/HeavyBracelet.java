package dev.lhkongyu.lhmiracleroad.items.curio.bracelet;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.items.curio.BraceletItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.UUID;

public class HeavyBracelet {

    public static BraceletItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.BURDEN, new AttributeModifier(UUID.fromString("6110368f-7515-9a71-48cd-380057d57219"), "", .15, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return new BraceletItem(new Item.Properties().rarity(Rarity.RARE),builder.build());
    }

}
