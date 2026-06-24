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

public class FlameRing {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.FLAME_ATTRIBUTE_DAMAGE, new AttributeModifier(UUID.fromString("af4df519-c7b3-4e7c-83cc-9322789574a3"), "", 0.24, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.DAMAGE_REDUCTION, new AttributeModifier(UUID.fromString("b8dd6bbb-dff9-45ba-b2a8-59911d754cd3"), "", -.08, AttributeModifier.Operation.MULTIPLY_TOTAL));
        if (LHMiracleRoadTool.isModExist("irons_spellbooks")) {
            String attributeName = "irons_spellbooks:fire_spell_power";
            ResourceLocation resourceLocation = ForgeRegistries.ATTRIBUTES.getKeys()
                    .stream()
                    .filter(p -> attributeName.equals(p.toString()))
                    .findFirst()
                    .orElse(null);

            Attribute instanceAttribute = ForgeRegistries.ATTRIBUTES.getValue(resourceLocation);
            if (instanceAttribute != null) {
                builder.put(instanceAttribute, new AttributeModifier(UUID.fromString("c6ca0ef4-31b2-45e3-96bb-9748ffc36ee7"), "", 0.24, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
        return new RingItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }
}
