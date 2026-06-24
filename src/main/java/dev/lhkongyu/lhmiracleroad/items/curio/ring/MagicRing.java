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

public class MagicRing {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.MAGIC_ATTRIBUTE_DAMAGE, new AttributeModifier(UUID.fromString("9861f5dc-d42b-4293-997d-de85f32fa474"), "", 0.24, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.DAMAGE_REDUCTION, new AttributeModifier(UUID.fromString("7e3ae3b3-3fb4-4113-aa58-8ea98ec81d42"), "", -.08, AttributeModifier.Operation.MULTIPLY_TOTAL));
        if (LHMiracleRoadTool.isModExist("irons_spellbooks")) {
            String attributeName = "irons_spellbooks:ice_spell_power";
            ResourceLocation resourceLocation = ForgeRegistries.ATTRIBUTES.getKeys()
                    .stream()
                    .filter(p -> attributeName.equals(p.toString()))
                    .findFirst()
                    .orElse(null);

            Attribute instanceAttribute = ForgeRegistries.ATTRIBUTES.getValue(resourceLocation);
            if (instanceAttribute != null) {
                builder.put(instanceAttribute, new AttributeModifier(UUID.fromString("89939908-8925-4c39-a6ab-087389962aa9"), "", 0.24, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
        return new RingItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }

}
