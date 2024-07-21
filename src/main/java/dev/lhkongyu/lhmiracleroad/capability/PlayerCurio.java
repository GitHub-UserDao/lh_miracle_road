package dev.lhkongyu.lhmiracleroad.capability;

import net.minecraft.nbt.CompoundTag;

public class PlayerCurio {

    private boolean isEquipRadianceRing = false;

    private boolean isVigilanceRingDistant = false;

    private boolean isVigilanceRingNear = false;

    private boolean isEquipHunterMark = false;

    private int hunterMarkKillAmount = 0;

    public boolean isEquipRadianceRing() {
        return isEquipRadianceRing;
    }

    public void setEquipRadianceRing(boolean equipRadianceRing) {
        isEquipRadianceRing = equipRadianceRing;
    }

    public boolean isVigilanceRingDistant() {
        return isVigilanceRingDistant;
    }

    public void setVigilanceRingDistant(boolean vigilanceRingDistant) {
        isVigilanceRingDistant = vigilanceRingDistant;
    }

    public boolean isVigilanceRingNear() {
        return isVigilanceRingNear;
    }

    public void setVigilanceRingNear(boolean vigilanceRingNear) {
        isVigilanceRingNear = vigilanceRingNear;
    }

    public boolean isEquipHunterMark() {
        return isEquipHunterMark;
    }

    public void setEquipHunterMark(boolean equipHunterMark) {
        isEquipHunterMark = equipHunterMark;
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

    public void saveNBTData(CompoundTag compoundTag){
        compoundTag.putBoolean("isEquipRadianceRing",isEquipRadianceRing);
        compoundTag.putBoolean("isVigilanceRingDistant",isVigilanceRingDistant);
        compoundTag.putBoolean("isVigilanceRingNear",isVigilanceRingNear);
        compoundTag.putBoolean("isEquipHunterMark",isEquipHunterMark);
        compoundTag.putInt("hunterMarkKillAmount",hunterMarkKillAmount);
    }

    public void loadNBTData(CompoundTag compoundTag){
        isEquipRadianceRing = compoundTag.getBoolean("isEquipRadianceRing");
        isVigilanceRingDistant = compoundTag.getBoolean("isVigilanceRingDistant");
        isVigilanceRingNear = compoundTag.getBoolean("isVigilanceRingNear");
        isEquipHunterMark = compoundTag.getBoolean("isEquipHunterMark");
        hunterMarkKillAmount = compoundTag.getInt("hunterMarkKillAmount");
    }
}
