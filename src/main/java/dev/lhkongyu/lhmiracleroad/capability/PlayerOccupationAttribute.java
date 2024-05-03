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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerOccupationAttribute {

    private int occupationLevel;

    private int occupationExperience;

    private Map<String, AttributeModifier> attributeModifier = Maps.newHashMap();

    private Map<String, AttributeModifier> punishmentAttributeModifier = Maps.newHashMap();

    private String occupationId;

    private Map<String, Integer> occupationAttributeLevel = Maps.newHashMap();

    private AttributeModifier heavyAttributeModifier = null;

    private double offhandHeavy;

    private Map<String, JsonObject> showAttribute = Maps.newLinkedHashMap();

    private String empiricalCalculationFormula;

    private int points = 0;

    private int burden = 0;

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

    public Map<String, AttributeModifier> getPunishmentAttributeModifier() {
        return punishmentAttributeModifier;
    }

    public void setPunishmentAttributeModifier(Map<String, AttributeModifier> punishmentAttributeModifier) {
        this.punishmentAttributeModifier = punishmentAttributeModifier;
    }

    public void addPunishmentAttributeModifier(String key,AttributeModifier punishmentAttributeModifier) {
        this.punishmentAttributeModifier.put(key,punishmentAttributeModifier);
    }

    public void removePunishmentAttributeModifier(String key){
        this.punishmentAttributeModifier.remove(key);
    }

    public AttributeModifier getHeavyAttributeModifier() {
        return heavyAttributeModifier;
    }

    public void setHeavyAttributeModifier(AttributeModifier heavyAttributeModifier) {
        this.heavyAttributeModifier = heavyAttributeModifier;
    }

    public double getOffhandHeavy() {
        return offhandHeavy;
    }

    public void setOffhandHeavy(double offhandHeavy) {
        this.offhandHeavy = offhandHeavy;
    }

    public Map<String, JsonObject> getShowAttribute() {
        return showAttribute;
    }

    public void setShowAttribute(Map<String, JsonObject> showAttribute) {
        this.showAttribute = showAttribute;
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

    public int getBurden() {
        return burden;
    }

    public void setBurden(int burden) {
        this.burden = burden;
    }

    public void saveNBTData(CompoundTag compoundTag){
        compoundTag.putInt("occupationLevel",occupationLevel);
        compoundTag.putInt("occupationExperience",occupationExperience);
        compoundTag.putDouble("offhandHeavy",offhandHeavy);
        compoundTag.putInt("points",points);
        compoundTag.putInt("burden",burden);
        if (attributeModifier != null) {
            CompoundTag attributeModifierTags = new CompoundTag();
            for (Map.Entry<String, AttributeModifier> entry : attributeModifier.entrySet()) {
                attributeModifierTags.put(entry.getKey(), entry.getValue().save());
            }
            compoundTag.put("attributeModifier", attributeModifierTags);
        }

        if (punishmentAttributeModifier != null) {
            CompoundTag punishmentAttributeModifierTags = new CompoundTag();
            for (Map.Entry<String, AttributeModifier> entry : punishmentAttributeModifier.entrySet()) {
                punishmentAttributeModifierTags.put(entry.getKey(), entry.getValue().save());
            }
            compoundTag.put("punishmentAttributeModifier", punishmentAttributeModifierTags);
        }

        if (occupationId != null) compoundTag.putString("occupationId", occupationId);

        if (occupationAttributeLevel != null) {
            CompoundTag levelTags = new CompoundTag();
            for (Map.Entry<String, Integer> entry : occupationAttributeLevel.entrySet()) {
                levelTags.putInt(entry.getKey(), entry.getValue());
            }
            compoundTag.put("occupationAttributeLevel", levelTags);
        }

        if (heavyAttributeModifier != null){
            compoundTag.put("heavyAttributeModifier",heavyAttributeModifier.save());
        }

        if (showAttribute != null) {
            CompoundTag showAttributeTags = new CompoundTag();
            for (Map.Entry<String, JsonObject> entry : showAttribute.entrySet()) {
                showAttributeTags.putString(entry.getKey(), entry.getValue().toString());
            }
            compoundTag.put("showAttribute", showAttributeTags);
        }

        if (empiricalCalculationFormula != null) compoundTag.putString("empiricalCalculationFormula", empiricalCalculationFormula);
    }

    public void loadNBTData(CompoundTag compoundTag){
        occupationLevel = compoundTag.getInt("occupationLevel");
        occupationExperience = compoundTag.getInt("occupationExperience");
        offhandHeavy = compoundTag.getDouble("offhandHeavy");
        points = compoundTag.getInt("points");
        burden = compoundTag.getInt("burden");
        if (compoundTag.contains("attributeModifier", Tag.TAG_COMPOUND)) {
            CompoundTag initAttributeTags = compoundTag.getCompound("attributeModifier");
            attributeModifier = Maps.newHashMap();
            for (String key : initAttributeTags.getAllKeys()) {
                CompoundTag value = initAttributeTags.getCompound(key);
                attributeModifier.put(key, AttributeModifier.load(value));
            }
        }

        if (compoundTag.contains("punishmentAttributeModifier", Tag.TAG_COMPOUND)) {
            CompoundTag punishmentAttributeModifierTags = compoundTag.getCompound("punishmentAttributeModifier");
            punishmentAttributeModifier = Maps.newHashMap();
            for (String key : punishmentAttributeModifierTags.getAllKeys()) {
                CompoundTag value = punishmentAttributeModifierTags.getCompound(key);
                punishmentAttributeModifier.put(key, AttributeModifier.load(value));
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

        if (compoundTag.contains("heavyAttributeModifier", Tag.TAG_STRING)) {
            CompoundTag heavyAttributeModifierTags = compoundTag.getCompound("heavyAttributeModifier");
            heavyAttributeModifier = AttributeModifier.load(heavyAttributeModifierTags);
        }

        if (compoundTag.contains("showAttribute", Tag.TAG_COMPOUND)) {
            CompoundTag showAttributeTags = compoundTag.getCompound("showAttribute");
            showAttribute = Maps.newHashMap();
            for (String key : showAttributeTags.getAllKeys()) {
                String value = showAttributeTags.getString(key);
                showAttribute.put(key,JsonParser.parseString(value).getAsJsonObject());
            }
        }

        if (compoundTag.contains("empiricalCalculationFormula", Tag.TAG_STRING)) {
            empiricalCalculationFormula = compoundTag.getString("empiricalCalculationFormula");
        }
    }

    public JsonObject getPlayerOccupationAttribute(){
        JsonObject playerOccupationAttributeObject = new JsonObject();
        playerOccupationAttributeObject.addProperty("occupationLevel",this.occupationLevel);
        playerOccupationAttributeObject.addProperty("occupationExperience",this.occupationExperience);
        playerOccupationAttributeObject.addProperty("points",this.points);
        playerOccupationAttributeObject.addProperty("offhandHeavy",this.offhandHeavy);
        playerOccupationAttributeObject.addProperty("burden",this.burden);
        if (this.occupationId != null && !this.occupationId.isEmpty()) playerOccupationAttributeObject.addProperty("occupationId",this.occupationId);
        if (this.empiricalCalculationFormula != null && !this.empiricalCalculationFormula.isEmpty()) playerOccupationAttributeObject.addProperty("empiricalCalculationFormula",this.empiricalCalculationFormula);

//        JsonObject attributeModifier = new JsonObject();
//
//        this.attributeModifier.forEach((key,value)->{
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("uuid", value.getId().toString());
//            jsonObject.addProperty("name", value.getName());
//            jsonObject.addProperty("amount", value.getAmount());
//            jsonObject.addProperty("operation", value.getOperation().toValue());
//            attributeModifier.add(key,jsonObject);
//        });
//        playerOccupationAttributeObject.add("attributeModifier",attributeModifier);

//        JsonObject punishmentAttributeModifier = new JsonObject();
//        this.punishmentAttributeModifier.forEach((key,value)->{
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("uuid", value.getId().toString());
//            jsonObject.addProperty("name", value.getName());
//            jsonObject.addProperty("amount", value.getAmount());
//            jsonObject.addProperty("operation", value.getOperation().toValue());
//            punishmentAttributeModifier.add(key,jsonObject);
//        });
//        playerOccupationAttributeObject.add("punishmentAttributeModifier",punishmentAttributeModifier);

        JsonObject occupationAttributeLevel = new JsonObject();
        this.occupationAttributeLevel.forEach(occupationAttributeLevel::addProperty);
        playerOccupationAttributeObject.add("occupationAttributeLevel",occupationAttributeLevel);

//        if (heavyAttributeModifier  != null) {
//            JsonObject heavyAttributeModifier = new JsonObject();
//            heavyAttributeModifier.addProperty("uuid", this.heavyAttributeModifier.getId().toString());
//            heavyAttributeModifier.addProperty("name", this.heavyAttributeModifier.getName());
//            heavyAttributeModifier.addProperty("amount", this.heavyAttributeModifier.getAmount());
//            heavyAttributeModifier.addProperty("operation", this.heavyAttributeModifier.getOperation().toValue());
//            playerOccupationAttributeObject.add("heavyAttributeModifier", heavyAttributeModifier);
//        }

        JsonObject showAttribute = new JsonObject();
        this.showAttribute.forEach(showAttribute::add);
        playerOccupationAttributeObject.add("showAttribute",showAttribute);

        return playerOccupationAttributeObject;
    }

}
