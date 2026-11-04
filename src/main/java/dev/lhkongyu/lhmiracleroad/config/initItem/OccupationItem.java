package dev.lhkongyu.lhmiracleroad.config.initItem;

public class OccupationItem {
    String item;
    int quantity;
    String tag;
    boolean is_split;
    boolean is_wear;
    boolean is_deputy;

    public String getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getTag() {
        return tag;
    }

    public boolean isSplit() {
        return is_split;
    }

    public boolean isWear() {
        return is_wear;
    }

    public boolean isDeputy() {
        return is_deputy;
    }
}
