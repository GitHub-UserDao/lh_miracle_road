package dev.lhkongyu.lhmiracleroad.entity;

import com.google.gson.JsonObject;

import java.util.Map;

public class PlayerAttribute {

    private JsonObject initAttribute;

    private String occupationId;

    private Map<String, Integer> occupationAttributeLevel;

    private int init_difficulty_level;

    private int level;

    private int points;

    public JsonObject getInitAttribute() {
        return initAttribute;
    }

    public void setInitAttribute(JsonObject initAttribute) {
        this.initAttribute = initAttribute;
    }

    public String getOccupationId() {
        return occupationId;
    }

    public void setOccupationId(String occupationId) {
        this.occupationId = occupationId;
    }

    public Map<String, Integer> getOccupationAttributeLevel() {
        return occupationAttributeLevel;
    }

    public void setOccupationAttributeLevel(Map<String, Integer> occupationAttributeLevel) {
        this.occupationAttributeLevel = occupationAttributeLevel;
    }

    public int getInit_difficulty_level() {
        return init_difficulty_level;
    }

    public void setInit_difficulty_level(int init_difficulty_level) {
        this.init_difficulty_level = init_difficulty_level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
