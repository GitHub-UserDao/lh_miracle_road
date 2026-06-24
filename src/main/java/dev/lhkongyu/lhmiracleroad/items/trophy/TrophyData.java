package dev.lhkongyu.lhmiracleroad.items.trophy;

import java.util.ArrayList;
import java.util.List;

public class TrophyData {

    public String name = "";

    public int roll_count_min = 1;

    public int roll_count_max = 1;

    public float luckWeight = 0;

    public int weight;

    public boolean allowRepeat = true;

    public List<TrophyEntry> entries = new ArrayList<>();

    public int getWeight(float luck){
        if (luckWeight <= 0) return weight;

        return Math.max(1, Math.round(weight + (luck * luckWeight * 0.8F)));
    }

}
