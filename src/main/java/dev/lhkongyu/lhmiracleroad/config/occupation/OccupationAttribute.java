package dev.lhkongyu.lhmiracleroad.config.occupation;

import java.util.List;

public class OccupationAttribute {
    String type_id;
    int init_lv;
    int max_lv;
    List<PointsReward> pointsRewards;

    public String getType_id() {
        return type_id;
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
