package dev.lhkongyu.lhmiracleroad.data.reloader;

import com.google.common.collect.Maps;
import com.google.gson.*;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Map;

public class EquipmentReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).create();

    public static final Map<String,JsonObject> EQUIPMENT = Maps.newHashMap();

    public EquipmentReloadListener() {
        super(GSON, "lh_miracle_road_equipment");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> obj, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        EQUIPMENT.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : obj.entrySet()) {
            String key = entry.getKey().toString();
            JsonElement jsonElement = entry.getValue();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(key));
            if (item == null) continue;
            EQUIPMENT.put(item.getDescriptionId(),jsonObject);
        }
    }
}
