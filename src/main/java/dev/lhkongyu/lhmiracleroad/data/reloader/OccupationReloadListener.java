package dev.lhkongyu.lhmiracleroad.data.reloader;

import com.google.gson.*;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OccupationReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).create();

    public static final List<JsonObject> OCCUPATION = new ArrayList<>();

    public OccupationReloadListener() {
        super(GSON, "lh_miracle_road_occupation/player/occupation");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> obj, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        OCCUPATION.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : obj.entrySet()) {
            JsonElement jsonElement = entry.getValue();
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element:jsonArray){
                JsonObject object = element.getAsJsonObject();
                if(LHMiracleRoadTool.isJsonArrayModIdsExist(LHMiracleRoadTool.isAsJsonArray(object.get("conditions")))) continue;
                OCCUPATION.add(object);
            }
        }
    }
}
