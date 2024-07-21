package dev.lhkongyu.lhmiracleroad.items.curio;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.lhkongyu.lhmiracleroad.items.curio.bracelet.HunterMark;
import dev.lhkongyu.lhmiracleroad.items.curio.ring.*;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class BraceletItem extends LHMiracleRoadCurioItem {

    public BraceletItem(Properties properties, Multimap<Attribute, AttributeModifier> defaultModifiers) {
        super(properties,defaultModifiers);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {

        Item item = stack.getItem();
        switch (item.getDescriptionId()) {
            case "item.lhmiracleroad.hunter_mark" -> HunterMark.setHunterMark(slotContext.entity(),true);
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        Item item = stack.getItem();
        switch (item.getDescriptionId()) {
            case "item.lhmiracleroad.hunter_mark" -> HunterMark.setHunterMark(slotContext.entity(),true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        Item item = stack.getItem();
        switch (item.getDescriptionId()) {
            case "item.lhmiracleroad.hunter_mark" -> HunterMark.setHunterMark(slotContext.entity(),false);
        }
    }

    /**
     *
     * @param stack
     * @param pLevel
     * @param lines
     * @param pIsAdvanced
     */
    @Override
    public void appendHoverText(ItemStack stack, Level pLevel, List<Component> lines, TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, lines, pIsAdvanced);
        Item item = stack.getItem();
        Font font = Minecraft.getInstance().font;
        int baseMaxWidth = font.width("K");

        if (item.getDescriptionId().equals("item.lhmiracleroad.hunter_mark")){
            MutableComponent detailsA = Component.translatable(item.getDescriptionId() + ".tooltip.details.1");
            List<String> strings = LHMiracleRoadTool.baseTextWidthSplitText(font,detailsA,baseMaxWidth * 32,0,0);
            for (String string : strings){
                lines.add(Component.literal(string));
            }
            MutableComponent detailsB = Component.translatable(item.getDescriptionId() + ".tooltip.details.2");
            strings = LHMiracleRoadTool.baseTextWidthSplitText(font,detailsB,baseMaxWidth * 32,0,0);
            for (String string : strings){
                lines.add(Component.literal(string).withStyle(ChatFormatting.DARK_PURPLE));
            }
        }else {
            MutableComponent mutableComponent = Component.translatable(item.getDescriptionId() + ".tooltip.details");
            List<String> strings = LHMiracleRoadTool.baseTextWidthSplitText(font,mutableComponent,baseMaxWidth * 32,0,0);
            for (String string : strings){
                lines.add(Component.literal(string));
            }
        }
    }
}
