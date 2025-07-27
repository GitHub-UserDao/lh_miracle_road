package dev.lhkongyu.lhmiracleroad.capability;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;

public class PlayerCurio {

    private boolean isEquipRadianceRing = false;

    private boolean isEquipWhisperRing = false;

    private boolean isEquipHunterMark = false;

    private boolean isEquipHuntingBowTalisman = false;

    private boolean isEquipConsecratedCombatPlume = false;

    private boolean isEquipSpanningWings = false;

    private boolean isEquipHeartOfBloodLust = false;

    private int hunterMarkKillAmount = 0;

    private Map<String,Integer> curioAttributes = Maps.newHashMap();

    public boolean isEquipRadianceRing() {
        return isEquipRadianceRing;
    }

    public void setEquipRadianceRing(boolean equipRadianceRing) {
        isEquipRadianceRing = equipRadianceRing;
    }

    public boolean isEquipWhisperRing() {
        return isEquipWhisperRing;
    }

    public void setEquipWhisperRing(boolean equipWhisperRing) {
        isEquipWhisperRing = equipWhisperRing;
    }

    public boolean isEquipHunterMark() {
        return isEquipHunterMark;
    }

    public void setEquipHunterMark(boolean equipHunterMark) {
        isEquipHunterMark = equipHunterMark;
    }

    public boolean isEquipHuntingBowTalisman() {
        return isEquipHuntingBowTalisman;
    }

    public void setEquipHuntingBowTalisman(boolean equipHuntingBowTalisman) {
        isEquipHuntingBowTalisman = equipHuntingBowTalisman;
    }

    public boolean isEquipConsecratedCombatPlume() {
        return isEquipConsecratedCombatPlume;
    }

    public void setEquipConsecratedCombatPlume(boolean equipConsecratedCombatPlume) {
        isEquipConsecratedCombatPlume = equipConsecratedCombatPlume;
    }

    public boolean isEquipSpanningWings() {
        return isEquipSpanningWings;
    }

    public void setEquipSpanningWings(boolean equipSpanningWings) {
        isEquipSpanningWings = equipSpanningWings;
    }

    public boolean isEquipHeartOfBloodLust() {
        return isEquipHeartOfBloodLust;
    }

    public void setEquipHeartOfBloodLust(boolean equipHeartOfBloodLust) {
        isEquipHeartOfBloodLust = equipHeartOfBloodLust;
    }

    public int getHunterMarkKillAmount() {
        return hunterMarkKillAmount;
    }

    public void setHunterMarkKillAmount(int hunterMarkKillAmount) {
        this.hunterMarkKillAmount = hunterMarkKillAmount;
    }

    public void addHunterMarkKillAmount() {
        this.hunterMarkKillAmount++;
    }

    public Map<String, Integer> getCurioAttributes() {
        return curioAttributes;
    }

    public void setCurioAttributes(Map<String, Integer> curioAttributes) {
        this.curioAttributes = curioAttributes;
    }

    public void putCurioAttributes(String key,Integer value){
        this.curioAttributes.put(key,value);
    }

    public void saveNBTData(CompoundTag compoundTag){
        compoundTag.putBoolean("isEquipRadianceRing",isEquipRadianceRing);
        compoundTag.putBoolean("isEquipWhisperRing",isEquipWhisperRing);
        compoundTag.putBoolean("isEquipHunterMark",isEquipHunterMark);
        compoundTag.putBoolean("isEquipHuntingBowTalisman",isEquipHuntingBowTalisman);
        compoundTag.putBoolean("isEquipConsecratedCombatPlume",isEquipConsecratedCombatPlume);
        compoundTag.putBoolean("isEquipSpanningWings",isEquipSpanningWings);
        compoundTag.putBoolean("isEquipHeartOfBloodLust",isEquipHeartOfBloodLust);
        compoundTag.putInt("hunterMarkKillAmount",hunterMarkKillAmount);
    }

    public void loadNBTData(CompoundTag compoundTag){
        isEquipRadianceRing = compoundTag.getBoolean("isEquipRadianceRing");
        isEquipWhisperRing = compoundTag.getBoolean("isEquipWhisperRing");
        isEquipHunterMark = compoundTag.getBoolean("isEquipHunterMark");
        isEquipHuntingBowTalisman = compoundTag.getBoolean("isEquipHuntingBowTalisman");
        isEquipConsecratedCombatPlume = compoundTag.getBoolean("isEquipConsecratedCombatPlume");
        isEquipSpanningWings = compoundTag.getBoolean("isEquipSpanningWings");
        isEquipHeartOfBloodLust = compoundTag.getBoolean("isEquipHeartOfBloodLust");
        hunterMarkKillAmount = compoundTag.getInt("hunterMarkKillAmount");
    }
}
