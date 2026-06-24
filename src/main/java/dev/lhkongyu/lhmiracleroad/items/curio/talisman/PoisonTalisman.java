package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.UUID;

public class PoisonTalisman {

    public static TalismanItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.ABNORMAL_POISON_BUILDUP, new AttributeModifier(UUID.fromString("47d1ca79-1897-48da-ae35-f24e3d65e203"), "", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.ABNORMAL_POISON_DAMAGE, new AttributeModifier(UUID.fromString("ef8bb92e-591d-471b-9470-c2d45e6663b4"), "", 0.1, AttributeModifier.Operation.MULTIPLY_BASE));
        return new TalismanItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }
}
