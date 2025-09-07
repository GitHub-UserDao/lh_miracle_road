package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import com.google.common.collect.ImmutableMultimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class CossetRing {



    public static RingItem addAttributeModifier(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.MAX_HEALTH, new AttributeModifier(UUID.fromString("ccb6a9aa-1ed4-1ddf-0f00-3ffca81a0f84"), "", .08, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.BURDEN, new AttributeModifier(UUID.fromString("81029488-7e04-251a-fceb-08151222fb1f"), "", .08, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.DAMAGE_ADDITION, new AttributeModifier(UUID.fromString("b53b2766-18dc-fd21-a48f-37317a4d373f"), "", .08, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(LHMiracleRoadAttributes.DAMAGE_REDUCTION, new AttributeModifier(UUID.fromString("ee13804d-af33-15ca-e1e7-cb2efdbb0bdf"), "", .08, AttributeModifier.Operation.MULTIPLY_TOTAL));
        builder.put(Attributes.LUCK, new AttributeModifier(UUID.fromString("a9aa539b-7500-31f1-3fff-e6d53c7e1257"), "", 8, AttributeModifier.Operation.ADDITION));
//        builder.put(LHMiracleRoadAttributes.CRITICAL_HIT_RATE, new AttributeModifier(UUID.fromString("7d1105bf-d69c-85ed-8f4a-50fd5386ba7c"), "", 10, AttributeModifier.Operation.ADDITION));
//        builder.put(LHMiracleRoadAttributes.CRITICAL_HIT_DAMAGE, new AttributeModifier(UUID.fromString("79c20141-e336-dcc5-583a-c21d43f6d1fe"), "", .1, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("07266f03-2624-1dac-fd42-e41b3d4a0be7"), "", .08, AttributeModifier.Operation.MULTIPLY_BASE));

        if (LHMiracleRoadTool.isModExist("irons_spellbooks") || LHMiracleRoadTool.isModExist("epicfight")) {
            Map<UUID,String> attributeNameMap = new LinkedHashMap<>();
            attributeNameMap.put(UUID.fromString("43299b68-d3fc-fde0-0ab5-9e16dd83ef63"),"irons_spellbooks:max_mana");
            attributeNameMap.put(UUID.fromString("d7819143-2685-93da-5828-d1d46a8c8f99"),"irons_spellbooks:mana_regen");
            attributeNameMap.put(UUID.fromString("880be39e-80d5-9010-11e7-d65ab9ead990"),"irons_spellbooks:cooldown_reduction");
            attributeNameMap.put(UUID.fromString("3183ddb2-efde-60c9-5577-d104342bac44"),"epicfight:staminar");


            for (UUID key : attributeNameMap.keySet()) {
                String attributeName = attributeNameMap.get(key);
                ResourceLocation resourceLocation = ForgeRegistries.ATTRIBUTES.getKeys()
                        .stream()
                        .filter(p -> attributeName.equals(p.toString()))
                        .findFirst()
                        .orElse(null);
                Attribute instanceAttribute = ForgeRegistries.ATTRIBUTES.getValue(resourceLocation);
                if (instanceAttribute != null) {
                    builder.put(instanceAttribute, new AttributeModifier(key, "", .08, AttributeModifier.Operation.MULTIPLY_BASE));
                }
            }
        }

        return new RingItem(new Item.Properties().rarity(Rarity.UNCOMMON),builder.build());
    }
}
