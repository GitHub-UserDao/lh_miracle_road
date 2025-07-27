package dev.lhkongyu.lhmiracleroad.items.curio;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.CreedTalismanData;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.MiraculousTalismanData;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.CreedTalisman;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.MiraculousTalisman;
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

public class MiraculousTalismanItem extends TalismanItem{
    public MiraculousTalismanItem(Properties properties, Multimap<Attribute, AttributeModifier> defaultModifiers) {
        super(properties, defaultModifiers);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        JsonArray keys = MiraculousTalismanData.getMiraculousTalismanData(pStack);
        if (keys != null  && !keys.isEmpty()) {
            tooltip.add(Component.translatable("item.lhmiracleroad.miraculous_talisman.tooltip.details.1").withStyle(ChatFormatting.YELLOW));
            tooltip.add(Component.empty());
            for (JsonElement attribute : keys){
                tooltip.add(Component.translatable("item.lhmiracleroad.miraculous_talisman.tooltip.details.2",Component.translatable("lhmiracleroad.gui.attribute.name."+attribute.getAsString()),MiraculousTalismanData.level).withStyle(ChatFormatting.YELLOW));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.lhmiracleroad.talisman.nbt.null").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        Item item = stack.getItem();
        MiraculousTalisman.equipMiraculousTalisman(slotContext.entity(),stack,true);

    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        Item item = stack.getItem();
        MiraculousTalisman.equipMiraculousTalisman(slotContext.entity(),stack,false);
    }
}
