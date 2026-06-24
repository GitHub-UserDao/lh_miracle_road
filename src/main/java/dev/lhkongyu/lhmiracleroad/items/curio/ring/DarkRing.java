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

public class DarkRing {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.DARK_ATTRIBUTE_DAMAGE, new AttributeModifier(UUID.fromString("f5a2d7f6-ee9e-429f-9fac-ef571c1df73b"), "", 0.24, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.DAMAGE_REDUCTION, new AttributeModifier(UUID.fromString("c0ed3032-9ee0-4a0f-9b88-34fb5e8fd20c"), "", -.08, AttributeModifier.Operation.MULTIPLY_TOTAL));
        if (LHMiracleRoadTool.isModExist("irons_spellbooks")) {
            String attributeName = "irons_spellbooks:ender_spell_power";
            ResourceLocation resourceLocation = ForgeRegistries.ATTRIBUTES.getKeys()
                    .stream()
                    .filter(p -> attributeName.equals(p.toString()))
                    .findFirst()
                    .orElse(null);

            Attribute instanceAttribute = ForgeRegistries.ATTRIBUTES.getValue(resourceLocation);
            if (instanceAttribute != null) {
                builder.put(instanceAttribute, new AttributeModifier(UUID.fromString("b971c68a-6722-46e7-bd63-d2afe295486a"), "", 0.24, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
        return new RingItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }
}
