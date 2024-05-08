package dev.lhkongyu.lhmiracleroad.data.reloader;

import com.google.common.collect.Maps;
import com.google.gson.*;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class PunishmentReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).create();

    public static final Map<String,JsonArray> DEFAULT_PUNISHMENT = Maps.newHashMap();

    public PunishmentReloadListener() {
        super(GSON, "lh_miracle_road_occupation/player/punishment");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> obj, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        DEFAULT_PUNISHMENT.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : obj.entrySet()) {
            JsonElement jsonElement = entry.getValue();
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element:jsonArray) {
                JsonObject object = element.getAsJsonObject();
                String id = LHMiracleRoadTool.isAsString(object.get("id"));
                JsonArray punishments = LHMiracleRoadTool.isAsJsonArray(object.get("punishments"));
                DEFAULT_PUNISHMENT.put(id, punishments);
            }
        }
    }
}
