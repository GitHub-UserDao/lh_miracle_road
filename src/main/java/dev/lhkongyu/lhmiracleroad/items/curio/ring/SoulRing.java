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


public class SoulRing {
    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.SOUL_ATTRIBUTE_DAMAGE, new AttributeModifier(UUID.fromString("36b7d337-2b09-4cb2-8211-c12404d7be09"), "", 0.24, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.DAMAGE_REDUCTION, new AttributeModifier(UUID.fromString("7c258498-eb55-4f0d-9a6d-f23939bcab99"), "", -.08, AttributeModifier.Operation.MULTIPLY_TOTAL));
        if (LHMiracleRoadTool.isModExist("irons_spellbooks")) {
            String attributeName = "irons_spellbooks:eldritch_spell_power";
            ResourceLocation resourceLocation = ForgeRegistries.ATTRIBUTES.getKeys()
                    .stream()
                    .filter(p -> attributeName.equals(p.toString()))
                    .findFirst()
                    .orElse(null);

            Attribute instanceAttribute = ForgeRegistries.ATTRIBUTES.getValue(resourceLocation);
            if (instanceAttribute != null) {
                builder.put(instanceAttribute, new AttributeModifier(UUID.fromString("094e37c4-814f-400c-957a-c8660dba5280"), "", 0.24, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
        return new RingItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }
}
