package dev.lhkongyu.lhmiracleroad.config.occupation;

import dev.lhkongyu.lhmiracleroad.tool.ResourceLocationTool;
import net.minecraft.network.chat.Component;

import java.util.List;

public class OccupationAttribute {
    String type_id;
    String name;
    int init_lv;
    int max_lv;
    List<PointsReward> pointsRewards;

    public String getType_id() {
        return type_id;
    }

    public Component displayName() {
        return name == null || name.isBlank()
                ? Component.translatableWithFallback(ResourceLocationTool.ATTRIBUTE_NAME_PREFIX + type_id, type_id)
                : Component.literal(name);
    }

    public int getInit_lv() {
        return init_lv;
    }

    public int getMax_lv() {
        return max_lv;
    }

    public List<PointsReward> getPointsRewards() {
        return pointsRewards;
    }
}
