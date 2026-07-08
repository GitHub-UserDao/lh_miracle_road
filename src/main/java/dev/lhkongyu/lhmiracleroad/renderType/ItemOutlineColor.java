package dev.lhkongyu.lhmiracleroad.renderType;

import dev.lhkongyu.lhmiracleroad.tool.NameTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class ItemOutlineColor {

    private ItemOutlineColor() {
    }

    public static int getColor(ItemStack stack) {
        if (stack.isEmpty() || stack.getTag() == null) {
            return 0;
        }

        CompoundTag gemTag = stack.getTag().getCompound("lh_gem");
        if (!gemTag.contains("type")) {
            return 0;
        }

        return switch (gemTag.getString("type")) {
            case NameTool.FLAME -> 0xFFFF3A00;
            case NameTool.LIGHTNING -> 0xFFFFA000;
            case NameTool.DARK -> 0xFFB000FF;
            case NameTool.BLOOD -> 0xFFFF0000;
            case NameTool.POISON -> 0xFF00FF00;
            case NameTool.ICE -> 0xFF009DFF;
            case NameTool.SHARP -> 0xFFE6F2FF;
            case NameTool.HEAVY -> 0xFFFFB000;
            case NameTool.SOUL -> 0xFFB8B8B8;
            case NameTool.HOLY -> 0xFFFFFF99;
            default -> 0;
        };
    }

    public static boolean hasOutline(int argb) {
        return (argb >>> 24) != 0;
    }
}
