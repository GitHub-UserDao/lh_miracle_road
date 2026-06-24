package dev.lhkongyu.lhmiracleroad.items.trophy;

public class TrophyEntry {

    public String item;

    public int weight;

    public int min;

    public int max;

    public String nbt = "";

    public float luckWeight = 0;

    public int getWeight(float luck) {
        if (luckWeight <= 0) return weight;

        return Math.max(1, Math.round(weight + (luck * luckWeight * 0.8F)));
    }
}
