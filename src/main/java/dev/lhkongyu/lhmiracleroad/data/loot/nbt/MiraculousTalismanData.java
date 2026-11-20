package dev.lhkongyu.lhmiracleroad.data.loot.nbt;

import com.google.gson.*;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiraculousTalismanData {

    public static final int level = 7;

    public static final String NBT_MIRACULOUS_ATTRIBUTE = "NBTMiraculousAttribute";

    public static void setMiraculousTalismanData(ItemStack stack) {
        List<String> ids = new ArrayList<>(OccupationConfig.getAttributeTypeIds());
        if (ids.isEmpty()) return;
        Collections.shuffle(ids);
        JsonArray selected = new JsonArray();
        ids.subList(0, Math.min(3, ids.size())).forEach(selected::add);
        stack.getOrCreateTag().putString(NBT_MIRACULOUS_ATTRIBUTE, selected.toString());
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

}
