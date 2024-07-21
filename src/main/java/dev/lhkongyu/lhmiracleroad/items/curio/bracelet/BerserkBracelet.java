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

public class BerserkBracelet {

    public static BraceletItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("99b866e7-f2b0-1873-7665-1c3389f647d2"), "", .20, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return new BraceletItem(new Item.Properties().rarity(Rarity.RARE),builder.build());
    }

}
