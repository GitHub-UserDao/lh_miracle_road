package dev.lhkongyu.lhmiracleroad.config.occupation;

public class PointsReward {
    String mod_id;
    String attribute_id;
    double value;
    int lv_promote;
    double lv_promote_value;
    String operation;
    double min;

    public String getMod_id() {
        return mod_id;
    }

    public String getAttribute_id() {
        return attribute_id;
    }

    public double getValue() {
        return value;
    }

    public int getLv_promote() {
        return lv_promote;
    }

    public double getLv_promote_value() {
        return lv_promote_value;
    }

    public String getOperation() {
        return operation;
    }

    public double getMin() {
        return min;
    }
}
