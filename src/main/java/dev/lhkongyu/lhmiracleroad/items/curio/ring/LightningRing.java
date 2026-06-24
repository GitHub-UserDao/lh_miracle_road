package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class LightningRing {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.LIGHTNING_ATTRIBUTE_DAMAGE, new AttributeModifier(UUID.fromString("766bbccc-bbdb-4fd7-a040-ff0882cb9468"), "", 0.24, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.DAMAGE_REDUCTION, new AttributeModifier(UUID.fromString("3907c13b-cc30-406a-9506-10b4c2408da0"), "", -.08, AttributeModifier.Operation.MULTIPLY_TOTAL));
        if (LHMiracleRoadTool.isModExist("irons_spellbooks")) {
            String attributeName = "irons_spellbooks:lightning_spell_power";
            ResourceLocation resourceLocation = ForgeRegistries.ATTRIBUTES.getKeys()
                    .stream()
                    .filter(p -> attributeName.equals(p.toString()))
                    .findFirst()
                    .orElse(null);

            Attribute instanceAttribute = ForgeRegistries.ATTRIBUTES.getValue(resourceLocation);
            if (instanceAttribute != null) {
                builder.put(instanceAttribute, new AttributeModifier(UUID.fromString("e9ab92f4-b5de-4ecb-a989-20c9272ccbab"), "", 0.24, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
        return new RingItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }
}
