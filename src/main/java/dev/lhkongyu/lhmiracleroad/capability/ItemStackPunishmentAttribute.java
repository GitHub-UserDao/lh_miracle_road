package dev.lhkongyu.lhmiracleroad.capability;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;

import java.util.Map;
import java.util.UUID;

public class ItemStackPunishmentAttribute {

    private Map<String, AttributeModifier> attribute = Maps.newHashMap();

    private Map<String, AttributeModifier> recordPunishmentAttributeModifier = Maps.newHashMap();

    private JsonArray attributeNeed;

    private double heavy = 0.0;

    public Map<Attribute, AttributeModifier> getAttribute() {
        Map<Attribute, AttributeModifier> map = Maps.newHashMap();
        for (String key : attribute.keySet()){
            map.put(LHMiracleRoadTool.stringConversionAttribute(key),attribute.get(key));
        }
        return map;
    }

    public void setAttribute(Map<String, AttributeModifier> multimap) {
        this.attribute = multimap;
    }

    public JsonArray getAttributeNeed() {
        return attributeNeed;
    }

    public void setAttributeNeed(JsonArray equipmentPunishment) {
        this.attributeNeed = equipmentPunishment;
    }

    public double getHeavy() {
        return heavy;
    }

    public void setHeavy(double heavy) {
        this.heavy = heavy;
    }

    public Map<String, AttributeModifier> getRecordPunishmentAttributeModifier() {
        return recordPunishmentAttributeModifier;
    }

    public void setRecordPunishmentAttributeModifier(Map<String, AttributeModifier> recordPunishmentAttributeModifier) {
        this.recordPunishmentAttributeModifier = recordPunishmentAttributeModifier;
    }

    public void addRecordPunishmentAttributeModifier(String key, AttributeModifier recordPunishmentAttributeModifier) {
        this.recordPunishmentAttributeModifier.put(key,recordPunishmentAttributeModifier);
    }

    public void cleanRecordPunishmentAttributeModifier(){
        this.recordPunishmentAttributeModifier.clear();
    }

    public void saveNBTData(CompoundTag compoundTag){
        compoundTag.putDouble("heavy",heavy);
        if (attribute != null) {
            CompoundTag attributeTags = new CompoundTag();
            for (Map.Entry<String, AttributeModifier> modifierEntry : attribute.entrySet()) {
                attributeTags.put(modifierEntry.getKey(), modifierEntry.getValue().save());
            }
            compoundTag.put("attribute", attributeTags);
        }

        if (recordPunishmentAttributeModifier != null) {
            CompoundTag recordPunishmentAttributeModifierTags = new CompoundTag();
            for (Map.Entry<String, AttributeModifier> entry : recordPunishmentAttributeModifier.entrySet()) {
                recordPunishmentAttributeModifierTags.put(entry.getKey(), entry.getValue().save());
            }
            compoundTag.put("recordPunishmentAttributeModifier", recordPunishmentAttributeModifierTags);
        }

        if (attributeNeed != null){
            compoundTag.putString("attributeNeed",attributeNeed.toString());
        }
    }

    public void loadNBTData(CompoundTag compoundTag){
        heavy = compoundTag.getDouble("heavy");

        if (compoundTag.contains("attribute", Tag.TAG_COMPOUND)) {
            CompoundTag attributeTags = compoundTag.getCompound("attribute");
            attribute = Maps.newHashMap();
            for (String key : attributeTags.getAllKeys()) {
                CompoundTag value = attributeTags.getCompound(key);
                attribute.put(key, AttributeModifier.load(value));
            }
        }

        if (compoundTag.contains("recordPunishmentAttributeModifier", Tag.TAG_COMPOUND)) {
            CompoundTag recordPunishmentAttributeModifierTags = compoundTag.getCompound("recordPunishmentAttributeModifier");
            recordPunishmentAttributeModifier = Maps.newHashMap();
            for (String key : recordPunishmentAttributeModifierTags.getAllKeys()) {
                CompoundTag value = recordPunishmentAttributeModifierTags.getCompound(key);
                recordPunishmentAttributeModifier.put(key, AttributeModifier.load(value));
            }
        }

        if (compoundTag.contains("attributeNeed", Tag.TAG_STRING)) {
            String attributeNeedTags = compoundTag.getString("attributeNeed");
            attributeNeed = JsonParser.parseString(attributeNeedTags).getAsJsonArray();
        }
    }
}
