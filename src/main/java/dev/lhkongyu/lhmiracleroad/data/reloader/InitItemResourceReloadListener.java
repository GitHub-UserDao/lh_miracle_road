package dev.lhkongyu.lhmiracleroad.data.reloader;

import com.google.common.collect.Maps;
import com.google.gson.*;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class InitItemResourceReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).create();

    public static final Map<String,JsonArray> INIT_ITEM = Maps.newHashMap();

    public InitItemResourceReloadListener() {
        super(GSON, "lh_miracle_road_occupation/player/init_item");
    }
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> obj, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        INIT_ITEM.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : obj.entrySet()) {
            JsonElement jsonElement = entry.getValue();
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element:jsonArray) {
                JsonObject object = element.getAsJsonObject();
                String id = LHMiracleRoadTool.isAsString(object.get("id"));
                JsonArray items = LHMiracleRoadTool.isAsJsonArray(object.get("items"));
                INIT_ITEM.put(id, items);
            }
        }
    }
}
