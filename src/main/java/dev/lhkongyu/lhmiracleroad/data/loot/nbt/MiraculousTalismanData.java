package dev.lhkongyu.lhmiracleroad.data.loot.nbt;

import com.google.gson.*;
import dev.lhkongyu.lhmiracleroad.data.reloader.AttributeReloadListener;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class MiraculousTalismanData {

    public static final int level = 6;

    public static final String NBT_MIRACULOUS_ATTRIBUTE = "NBTMiraculousAttribute";

    public static void setMiraculousTalismanData(ItemStack stack) {
        var spellTag = stack.getOrCreateTag();
        Map<String, JsonArray> attributeTypes = AttributeReloadListener.ATTRIBUTE_TYPES;
        JsonArray randomKeys = getRandomKey(attributeTypes,3);

        if (randomKeys != null && !randomKeys.isEmpty()) spellTag.putString(NBT_MIRACULOUS_ATTRIBUTE, randomKeys.toString());
    }

    public static JsonArray getMiraculousTalismanData(ItemStack stack){
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(NBT_MIRACULOUS_ATTRIBUTE)) {
            return new JsonArray();
        }

        String jsonString = tag.getString(NBT_MIRACULOUS_ATTRIBUTE);
        try {
            return JsonParser.parseString(jsonString).getAsJsonArray();
        } catch (JsonSyntaxException | IllegalStateException e) {
            return new JsonArray();
        }
    }

    public static JsonArray getRandomKey(Map<String, JsonArray> attributeTypes,int count) {
        if (attributeTypes == null || attributeTypes.isEmpty()) {
            return null;
        }

        List<String> keyPool = new ArrayList<>(attributeTypes.keySet());
        int maxPossible = Math.min(count, keyPool.size());
        JsonArray randomKeys = new JsonArray();

        for (int i = 0; i < maxPossible; i++) {
            Random random = new Random();
            int randomIndex = random.nextInt(keyPool.size());
            randomKeys.add(keyPool.get(randomIndex));
            keyPool.remove(randomIndex);
        }

        return randomKeys;
    }
}
