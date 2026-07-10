package dev.lhkongyu.lhmiracleroad.renderType;

import dev.lhkongyu.lhmiracleroad.tool.NameTool;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class ItemOutlineColor {

    private static final long BREATH_PERIOD_MILLIS = 2000L;

    private static final float BREATH_MIN_BRIGHTNESS = 0.7F;

    private ItemOutlineColor() {
    }

    public static int[] getColors(ItemStack stack) {
        if (stack.isEmpty() || stack.getTag() == null) {
            return new int[0];
        }

        CompoundTag gemTag = stack.getTag().getCompound("lh_gem");
        if (!gemTag.contains("type")) {
            return new int[0];
        }

        return switch (gemTag.getString("type")) {
            case NameTool.FLAME -> new int[]{breathe(rgb(0xFF5D1D))};
            case NameTool.LIGHTNING -> new int[]{breathe(rgb(0xFF8A1D))};
            case NameTool.DARK -> new int[]{breathe(rgb(0xB000FF))};
            case NameTool.BLOOD -> new int[]{breathe(rgb(0xFF0000))};
            case NameTool.POISON -> new int[]{breathe(rgb(0x00FF00))};
            case NameTool.ICE -> new int[]{breathe(rgb(0x7FD5FF))};
            case NameTool.SHARP, NameTool.HEAVY -> new int[]{breathe(rgb(0x8A8A8A))};
            case NameTool.SOUL -> new int[]{breathe(rgb(0xDBD9BF))};
            case NameTool.HOLY -> new int[]{breathe(rgb(0xFFFF80))};
            default -> new int[0];
        };
    }

    private static int rgb(int rgb) {
        return 0xFF000000 | rgb;
    }

    private static int breathe(int argb) {
        double phase = (Util.getMillis() % BREATH_PERIOD_MILLIS) / (double) BREATH_PERIOD_MILLIS * Math.PI * 2.0D;
        float brightness = BREATH_MIN_BRIGHTNESS + (float) ((Math.sin(phase) + 1.0D) * 0.5D) * (1.0F - BREATH_MIN_BRIGHTNESS);
        int alpha = argb & 0xFF000000;
        int red = Math.round(((argb >> 16) & 255) * brightness);
        int green = Math.round(((argb >> 8) & 255) * brightness);
        int blue = Math.round((argb & 255) * brightness);
        return alpha | red << 16 | green << 8 | blue;
    }

    public static boolean hasOutline(int[] argbColors) {
        return argbColors.length > 0;
    }
}
