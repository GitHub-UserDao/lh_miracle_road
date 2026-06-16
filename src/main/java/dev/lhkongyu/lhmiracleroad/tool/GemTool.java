package dev.lhkongyu.lhmiracleroad.tool;

import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.registry.TagsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

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

    public static int getAttributeGemSoulCount(ItemStack gemItemStack){
        if (gemItemStack.is(ItemsRegistry.WHETSTONE.get())) return 2500;
        else return 5000;
    }

    public static int getStrengthenGemSoulCount(int strengthenLV){
        return switch (strengthenLV){
            case 0 -> 200;
            case 1 -> 500;
            case 2 -> 800;
            case 3 -> 1200;
            case 4 -> 1800;
            case 5 -> 2500;
            case 6 -> 3200;
            case 7 -> 4000;
            case 8 -> 5000;
            case 9 -> 7500;
            default -> 0;
        };
    }

    public static boolean isSoulSufficient(Player player,int soulCount){
        Optional<PlayerOccupationAttribute> optional =
                player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
        if (optional.isEmpty()) return true;
        PlayerOccupationAttribute playerOccupationAttribute = optional.get();
        int playerSoulCount = playerOccupationAttribute.getOccupationExperience();
        return playerSoulCount < soulCount;
    }

    public static void deductSoul(Player player,int soulCount){
        if (player instanceof ServerPlayer serverPlayer) {
            Optional<PlayerOccupationAttribute> optional =
                    serverPlayer.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
            if (optional.isEmpty()) return;
            PlayerOccupationAttribute playerOccupationAttribute = optional.get();
            int pSoulStart = playerOccupationAttribute.getOccupationExperience();
            playerOccupationAttribute.addOccupationExperience(-soulCount);
            SyncTool.synchronizationSoul(playerOccupationAttribute.getOccupationExperience(), serverPlayer, pSoulStart);
        }
    }


}
