package dev.lhkongyu.lhmiracleroad.data.reloader;

import com.google.common.collect.Maps;
import com.google.gson.*;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).create();

//    public static final List<JsonObject> ATTRIBUTE_TYPES = new ArrayList<>();

    public static final Map<String,JsonObject> ATTRIBUTE_TYPES =  Maps.newLinkedHashMap();

    public AttributeReloadListener() {
        super(GSON, "lh_miracle_road_occupation/player/attribute/attribute_types");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> obj, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ATTRIBUTE_TYPES.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : obj.entrySet()) {
            JsonElement jsonElement = entry.getValue();
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element:jsonArray){
                JsonObject jsonObject = element.getAsJsonObject();
                if(LHMiracleRoadTool.isJsonArrayModIdsExist(LHMiracleRoadTool.isAsJsonArray(jsonObject.get("conditions")))) continue;
                ATTRIBUTE_TYPES.put(jsonObject.get("id").getAsString(),jsonObject);
            }
        }
    }
}
