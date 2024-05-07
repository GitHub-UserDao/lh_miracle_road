package dev.lhkongyu.lhmiracleroad.data.reloader;

import com.google.common.collect.Maps;
import com.google.gson.*;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AttributePointsRewardsReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).create();

    public static final Map<String,JsonObject> ATTRIBUTE_POINTS_REWARDS = Maps.newHashMap();

    public static final Map<String, Attribute> recordAttribute = Maps.newHashMap();

    public static final Map<String, JsonObject> POINTS_REWARDS = Maps.newHashMap();

    public AttributePointsRewardsReloadListener() {
        super(GSON, "lh_miracle_road_occupation/player/attribute/attribute_points_rewards");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> obj, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        ATTRIBUTE_POINTS_REWARDS.clear();
        recordAttribute.clear();
        POINTS_REWARDS.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : obj.entrySet()) {
            JsonElement jsonElement = entry.getValue();
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element:jsonArray){
                JsonObject jsonObject = element.getAsJsonObject();
                String id = jsonObject.get("id").getAsString();
                JsonObject data = jsonObject.get("data").getAsJsonObject();
                ATTRIBUTE_POINTS_REWARDS.put(id, data);
                //获取在数据包里设置的属性对象
                for (JsonElement pointsRewardElement : LHMiracleRoadTool.isAsJsonArray(data.get("points_rewards"))) {
                    JsonObject pointsRewardObj = pointsRewardElement.getAsJsonObject();
                    String attributeName = LHMiracleRoadTool.isAsString(pointsRewardObj.get("attribute"));
                    if (LHMiracleRoadTool.stringConversionAttribute(attributeName) == null) {
                        ResourceLocation resourceLocation = ForgeRegistries.ATTRIBUTES.getKeys()
                                .stream()
                                .filter(p -> attributeName.equals(p.toString()))
                                .findFirst()
                                .orElse(null);
                        if (resourceLocation == null) continue;
                        Attribute instanceAttribute = ForgeRegistries.ATTRIBUTES.getValue(resourceLocation);
                        if (instanceAttribute == null) continue;
                        recordAttribute.put(attributeName, instanceAttribute);
                    }
                    POINTS_REWARDS.put(attributeName,pointsRewardObj);
                }
            }

        }
    }
}
