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

public class ShowGuiAttributeReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).create();

    public static final List<JsonObject> SHOW_GUI_ATTRIBUTE = new ArrayList<>();

    public ShowGuiAttributeReloadListener() {
        super(GSON, "lh_miracle_road_occupation/player/attribute/show_gui_attribute");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> obj, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        SHOW_GUI_ATTRIBUTE.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : obj.entrySet()){
            JsonArray jsonArray  = entry.getValue().getAsJsonArray();
            for (JsonElement element : jsonArray){
                JsonObject jsonObject = element.getAsJsonObject();
                String condition = LHMiracleRoadTool.isAsString(jsonObject.get("condition"));
                if (condition != null)
                    if (!LHMiracleRoadTool.isModExist(condition)) continue;
                SHOW_GUI_ATTRIBUTE.add(jsonObject);
            }
        }
    }
}
