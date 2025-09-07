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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 *古老咒术戒指 的饰品功能
 */
public class AncientSpellCraftRing {

    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(LHMiracleRoadAttributes.DAMAGE_ADDITION, new AttributeModifier(UUID.fromString("d051e99d-c3ea-56dc-b6cb-f60f5623322e"), "", .16, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.MAGIC_DAMAGE_ADDITION, new AttributeModifier(UUID.fromString("382fa8b3-8cbe-ae9b-4945-6bb58e894f2f"), "", .10, AttributeModifier.Operation.MULTIPLY_BASE));

        if (LHMiracleRoadTool.isModExist("irons_spellbooks")) {
            Map<UUID,String> attributeNameMap = new LinkedHashMap<>();
            attributeNameMap.put(UUID.fromString("c6222d0a-0bf0-9086-1c9f-0459241ab8aa"),"irons_spellbooks:spell_power");

            for (UUID key : attributeNameMap.keySet()) {
                String attributeName = attributeNameMap.get(key);
                ResourceLocation resourceLocation = ForgeRegistries.ATTRIBUTES.getKeys()
                        .stream()
                        .filter(p -> attributeName.equals(p.toString()))
                        .findFirst()
                        .orElse(null);
                Attribute instanceAttribute = ForgeRegistries.ATTRIBUTES.getValue(resourceLocation);
                if (instanceAttribute != null) {
                    builder.put(instanceAttribute, new AttributeModifier(key, "", .10, AttributeModifier.Operation.MULTIPLY_BASE));
                }
            }
        }

        return new RingItem(new Item.Properties().rarity(Rarity.EPIC),builder.build());
    }
}
