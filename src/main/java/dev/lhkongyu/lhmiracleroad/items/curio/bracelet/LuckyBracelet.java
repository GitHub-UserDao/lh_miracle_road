package dev.lhkongyu.lhmiracleroad.items.curio.bracelet;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.items.curio.BraceletItem;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.UUID;

public class LuckyBracelet {
    public static BraceletItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.CRITICAL_HIT_RATE, new AttributeModifier(UUID.fromString("083b9b40-cd06-bff4-6be7-3110d5112f34"), "", 10, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.LUCK, new AttributeModifier(UUID.fromString("ff1a1dce-06d4-77ff-5119-a5b2ff4ae316"), "", 10, AttributeModifier.Operation.ADDITION));
        return new BraceletItem(new Item.Properties().rarity(Rarity.RARE),builder.build());
    }

}
