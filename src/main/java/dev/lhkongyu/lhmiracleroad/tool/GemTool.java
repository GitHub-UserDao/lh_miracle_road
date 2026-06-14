package dev.lhkongyu.lhmiracleroad.tool;

import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class GemTool {

    public static String getGemType(ItemStack itemStack){
        if (itemStack.is(ItemsRegistry.FLAME_GEM.get())){
            return NameTool.FLAME;
        }else if (itemStack.is(ItemsRegistry.LIGHTNING_GEM.get())){
            return NameTool.LIGHTNING;
        }else if (itemStack.is(ItemsRegistry.DARK_GEM.get())){
            return NameTool.DARK;
        }else if (itemStack.is(ItemsRegistry.BLOOD_GEM.get())){
            return NameTool.BLOOD;
        }else if (itemStack.is(ItemsRegistry.SHARP_GEM.get())){
            return NameTool.SHARP;
        }else if (itemStack.is(ItemsRegistry.ICE_GEM.get())){
            return NameTool.ICE;
        }else if (itemStack.is(ItemsRegistry.POISON_GEM.get())){
            return NameTool.POISON;
        }else if (itemStack.is(ItemsRegistry.SOUL_GEM.get())){
            return NameTool.SOUL;
        }else if (itemStack.is(ItemsRegistry.HOLY_GEM.get())){
            return NameTool.HOLY;
        }else if (itemStack.is(ItemsRegistry.HEAVY_GEM.get())){
            return NameTool.HEAVY;
        }else return null;
    }

    public static String getGemName(String gemType,int lv){
        String name = "tooltip.lhmiracleroad.gem.name.";

        if (lv <= 0 && !gemType.isEmpty()) return " " + Component.translatable(name+gemType).getString();
        else if (lv > 0 && !gemType.isEmpty())return " " + Component.translatable(name+gemType).getString() + " +" + lv;
        else if (lv > 0) return " +" + lv;
        return "";
    }


}
