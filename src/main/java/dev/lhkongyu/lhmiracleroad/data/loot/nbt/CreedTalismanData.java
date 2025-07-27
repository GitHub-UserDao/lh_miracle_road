package dev.lhkongyu.lhmiracleroad.data.loot.nbt;

import com.google.gson.JsonArray;
import dev.lhkongyu.lhmiracleroad.data.reloader.AttributeReloadListener;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CreedTalismanData {

    public static final int level = 8;

    public static final String NBT_CREED_ATTRIBUTE = "NBTCreedAttribute";

    public static void setCreedTalismanData(ItemStack stack) {
        var spellTag = stack.getOrCreateTag();
        Map<String, JsonArray> attributeTypes = AttributeReloadListener.ATTRIBUTE_TYPES;
        String randomKey = getRandomKey(attributeTypes);

        spellTag.putString(NBT_CREED_ATTRIBUTE, randomKey);
    }

    public static String getCreedTalismanData(ItemStack stack){
        if (stack.getTag() != null && stack.getTag().contains(NBT_CREED_ATTRIBUTE)) {
            return stack.getTag().getString(NBT_CREED_ATTRIBUTE);
        } else {
            return null;
        }
    }

    public static String getRandomKey(Map<String, JsonArray> attributeTypes) {
        if (attributeTypes == null || attributeTypes.isEmpty()) {
            return null;
        }
        List<String> keys = new ArrayList<>(attributeTypes.keySet());

        Random random = new Random();
        int randomIndex =random.nextInt(keys.size());

        return keys.get(randomIndex);
    }

}
