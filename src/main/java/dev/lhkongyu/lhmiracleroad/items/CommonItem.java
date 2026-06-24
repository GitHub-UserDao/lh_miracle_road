package dev.lhkongyu.lhmiracleroad.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CommonItem extends Item {
    public CommonItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Item item = stack.getItem();
        tooltip.add(Component.translatable(item.getDescriptionId() + ".tooltip.details"));
        super.appendHoverText(stack,worldIn,tooltip,flagIn);
    }
}
