package dev.lhkongyu.lhmiracleroad.items.curio;

import com.google.common.collect.Multimap;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.CreedTalismanData;

import dev.lhkongyu.lhmiracleroad.items.curio.talisman.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class CreedTalismanItem extends TalismanItem {
    public CreedTalismanItem(Properties properties, Multimap<Attribute, AttributeModifier> defaultModifiers) {
        super(properties, defaultModifiers);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        String attribute = CreedTalismanData.getCreedTalismanData(pStack);
        if (attribute != null) {
            tooltip.add(Component.translatable("item.lhmiracleroad.creed_talisman.tooltip.details.1").withStyle(ChatFormatting.LIGHT_PURPLE));
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("item.lhmiracleroad.creed_talisman.tooltip.details.2",Component.translatable("lhmiracleroad.gui.attribute.name."+attribute),CreedTalismanData.level).withStyle(ChatFormatting.YELLOW));
        } else {
            tooltip.add(Component.translatable("tooltip.lhmiracleroad.talisman.nbt.null").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        Item item = stack.getItem();
        CreedTalisman.equipCreedTalisman(slotContext.entity(),stack,true);

    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        Item item = stack.getItem();
        CreedTalisman.equipCreedTalisman(slotContext.entity(),stack,false);
    }

//    @Override
//    public void initItem(ItemStack itemStack) {
//        var nbt = itemStack.getOrCreateTag();
//        nbt.putString("ISBEnhance", "123456");
//    }
}
