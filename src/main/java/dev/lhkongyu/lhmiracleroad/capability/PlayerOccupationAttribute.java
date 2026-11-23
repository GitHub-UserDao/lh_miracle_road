package dev.lhkongyu.lhmiracleroad.capability;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerOccupationAttribute {

    private int occupationLevel;

    private int occupationExperience;

    private Map<String, AttributeModifier> attributeModifier = Maps.newHashMap();

    private final Map<UUID, Attribute> activeEquipmentPenalties = new HashMap<>();
    private boolean curioPenaltyRefreshPending;

    private String occupationId;

    private Map<String, Integer> occupationAttributeLevel = Maps.newHashMap();

    private Map<String, Integer> curioAttributeLevel = Maps.newHashMap();

    private String empiricalCalculationFormula;

    private int points = 0;

    private int attributeMaxLevel = 0;

    private int maxLevel = 0;

    public int getOccupationLevel() {
        return occupationLevel;
    }

    public void setOccupationLevel(int occupationLevel) {
        this.occupationLevel = occupationLevel;
    }

    public void upgrade(){
        this.occupationLevel += 1;
    }

    public int getOccupationExperience() {
        return occupationExperience;
    }

    public void setOccupationExperience(int occupationExperience) {
        this.occupationExperience = occupationExperience;
    }

    public void addOccupationExperience(int occupationExperience){
        this.occupationExperience += occupationExperience;
    }

    public void reduceOccupationExperience(int occupationExperience){
        this.occupationExperience -= occupationExperience;
    }

    public Map<String, AttributeModifier> getAttributeModifier() {
        return attributeModifier;
    }

    public void setAttributeModifier(Map<String, AttributeModifier> attributeModifier) {
        this.attributeModifier = attributeModifier;
    }

    public void addAttributeModifier(String key,AttributeModifier attributeModifier) {
        this.attributeModifier.put(key,attributeModifier);
    }

    public void clearAttributeModifier(){
        this.attributeModifier.clear();
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

    public void putOccupationAttributeLevel(String key,Integer value){
        this.occupationAttributeLevel.put(key,value);
    }

    public Map<String, Integer> getCurioAttributeLevel() {
        return curioAttributeLevel;
    }

    public void setCurioAttributeLevel(Map<String, Integer> curioAttributeLevel) {
        this.curioAttributeLevel = curioAttributeLevel;
    }

    public int getCurioAttributeLevelValue(String key) {
       return this.curioAttributeLevel.get(key) == null ? 0 : this.curioAttributeLevel.get(key);
    }

    public void addCurioAttributeLevel(String key,Integer value){
        if (this.curioAttributeLevel.get(key) != null){
            Integer originalValue = this.curioAttributeLevel.get(key);
            this.curioAttributeLevel.put(key,originalValue + value);
        }else {
            this.curioAttributeLevel.put(key,value);
        }
    }

    public void subtractCurioAttributeLevel(String key,Integer value){
        if (this.curioAttributeLevel.get(key) != null){
            Integer originalValue = this.curioAttributeLevel.get(key);
            this.curioAttributeLevel.put(key,originalValue - value);
        }
    }

    public Map<UUID, Attribute> getActiveEquipmentPenalties() {
        return activeEquipmentPenalties;
    }

    public void queueCurioPenaltyRefresh() {
        curioPenaltyRefreshPending = true;
    }

    public boolean consumeCurioPenaltyRefresh() {
        boolean pending = curioPenaltyRefreshPending;
        curioPenaltyRefreshPending = false;
        return pending;
    }

    public String getEmpiricalCalculationFormula() {
        return empiricalCalculationFormula;
    }

    public void setEmpiricalCalculationFormula(String empiricalCalculationFormula) {
        this.empiricalCalculationFormula = empiricalCalculationFormula;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public int getAttributeMaxLevel() {
        return attributeMaxLevel;
    }

    public void setAttributeMaxLevel(int attributeMaxLevel) {
        this.attributeMaxLevel = attributeMaxLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void saveNBTData(CompoundTag compoundTag){
        compoundTag.putInt("occupationLevel",occupationLevel);
        compoundTag.putInt("occupationExperience",occupationExperience);
        compoundTag.putInt("points",points);
        compoundTag.putInt("attributeMaxLevel",attributeMaxLevel);
        compoundTag.putInt("maxLevel",maxLevel);
        if (attributeModifier != null) {
            CompoundTag attributeModifierTags = new CompoundTag();
            for (Map.Entry<String, AttributeModifier> entry : attributeModifier.entrySet()) {
                attributeModifierTags.put(entry.getKey(), entry.getValue().save());
            }
            compoundTag.put("attributeModifier", attributeModifierTags);
        }

        if (occupationId != null) compoundTag.putString("occupationId", occupationId);

        if (occupationAttributeLevel != null) {
            CompoundTag levelTags = new CompoundTag();
            for (Map.Entry<String, Integer> entry : occupationAttributeLevel.entrySet()) {
                levelTags.putInt(entry.getKey(), entry.getValue());
            }
            compoundTag.put("occupationAttributeLevel", levelTags);
        }

        if (curioAttributeLevel != null) {
            CompoundTag levelTags = new CompoundTag();
            for (Map.Entry<String, Integer> entry : curioAttributeLevel.entrySet()) {
                levelTags.putInt(entry.getKey(), entry.getValue());
            }
            compoundTag.put("curioAttributeLevel", levelTags);
        }

        if (empiricalCalculationFormula != null) compoundTag.putString("empiricalCalculationFormula", empiricalCalculationFormula);
    }

    public void loadNBTData(CompoundTag compoundTag){
        occupationLevel = compoundTag.getInt("occupationLevel");
        occupationExperience = compoundTag.getInt("occupationExperience");
        points = compoundTag.getInt("points");
        attributeMaxLevel = compoundTag.getInt("attributeMaxLevel");
        maxLevel = compoundTag.getInt("maxLevel");
        if (compoundTag.contains("attributeModifier", Tag.TAG_COMPOUND)) {
            CompoundTag initAttributeTags = compoundTag.getCompound("attributeModifier");
            attributeModifier = Maps.newHashMap();
            for (String key : initAttributeTags.getAllKeys()) {
                CompoundTag value = initAttributeTags.getCompound(key);
                attributeModifier.put(key, AttributeModifier.load(value));
            }
        }

        if (compoundTag.contains("occupationId", Tag.TAG_STRING)) {
            occupationId = compoundTag.getString("occupationId");
        }

        if (compoundTag.contains("occupationAttributeLevel", Tag.TAG_COMPOUND)) {
            CompoundTag levelTags = compoundTag.getCompound("occupationAttributeLevel");
            occupationAttributeLevel = Maps.newHashMap();
            for (String key : levelTags.getAllKeys()) {
                int value = levelTags.getInt(key);
                occupationAttributeLevel.put(key, value);
            }
        }

        if (compoundTag.contains("curioAttributeLevel", Tag.TAG_COMPOUND)) {
            CompoundTag levelTags = compoundTag.getCompound("curioAttributeLevel");
            curioAttributeLevel = Maps.newHashMap();
            for (String key : levelTags.getAllKeys()) {
                int value = levelTags.getInt(key);
                curioAttributeLevel.put(key, value);
            }
        }

        if (compoundTag.contains("empiricalCalculationFormula", Tag.TAG_STRING)) {
            empiricalCalculationFormula = compoundTag.getString("empiricalCalculationFormula");
        }
    }

    public JsonObject getPlayerOccupationAttribute(UUID playerUUID){
        JsonObject playerOccupationAttributeObject = new JsonObject();
        playerOccupationAttributeObject.addProperty("occupationLevel",this.occupationLevel);
        playerOccupationAttributeObject.addProperty("occupationExperience",this.occupationExperience);
        playerOccupationAttributeObject.addProperty("points",this.points);
        playerOccupationAttributeObject.addProperty("playerUUID",playerUUID.toString());
        playerOccupationAttributeObject.addProperty("attributeMaxLevel",attributeMaxLevel);
        playerOccupationAttributeObject.addProperty("maxLevel",maxLevel);
        if (this.occupationId != null && !this.occupationId.isEmpty()) playerOccupationAttributeObject.addProperty("occupationId",this.occupationId);
        if (this.empiricalCalculationFormula != null && !this.empiricalCalculationFormula.isEmpty()) playerOccupationAttributeObject.addProperty("empiricalCalculationFormula",this.empiricalCalculationFormula);
        JsonObject occupationAttributeLevel = new JsonObject();
        this.occupationAttributeLevel.forEach(occupationAttributeLevel::addProperty);
        playerOccupationAttributeObject.add("occupationAttributeLevel",occupationAttributeLevel);

        JsonObject curioAttributeLevel = new JsonObject();
        this.curioAttributeLevel.forEach(curioAttributeLevel::addProperty);
        playerOccupationAttributeObject.add("curioAttributeLevel",curioAttributeLevel);

        return playerOccupationAttributeObject;
    }

}
