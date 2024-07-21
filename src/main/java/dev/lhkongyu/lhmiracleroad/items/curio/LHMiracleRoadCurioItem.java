package dev.lhkongyu.lhmiracleroad.items.curio;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class LHMiracleRoadCurioItem extends Item implements ICurioItem {
    protected final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public LHMiracleRoadCurioItem(Properties properties, Multimap<Attribute, AttributeModifier> defaultModifiers) {
        super(properties);
        this.defaultModifiers = defaultModifiers;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = new ImmutableMultimap.Builder<>();
        if (defaultModifiers != null) {
            for (Attribute attribute : defaultModifiers.keySet()) {
                var modifiers = defaultModifiers.get(attribute);
                for (AttributeModifier attributeModifier : modifiers) {
                    attributeBuilder.put(attribute, new AttributeModifier(uuid, attributeModifier.getName(), attributeModifier.getAmount(), attributeModifier.getOperation()));
                }
            }
        }
        return attributeBuilder.build();
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
