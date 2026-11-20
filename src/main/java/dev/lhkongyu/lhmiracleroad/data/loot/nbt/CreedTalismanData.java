package dev.lhkongyu.lhmiracleroad.data.loot.nbt;

import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationConfig;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CreedTalismanData {

    public static final int level = 8;

    public static final String NBT_CREED_ATTRIBUTE = "NBTCreedAttribute";

    public static void setCreedTalismanData(ItemStack stack) {
        List<String> ids = OccupationConfig.getAttributeTypeIds();
        if (ids.isEmpty()) return;
        String id = ids.get(ThreadLocalRandom.current().nextInt(ids.size()));
        stack.getOrCreateTag().putString(NBT_CREED_ATTRIBUTE, id);
    }

    public static String getCreedTalismanData(ItemStack stack){
        if (stack.getTag() != null && stack.getTag().contains(NBT_CREED_ATTRIBUTE)) {
            return stack.getTag().getString(NBT_CREED_ATTRIBUTE);
        } else {
            return null;
        }
    }

}
