package dev.lhkongyu.lhmiracleroad.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.config.SoulItemConfig.Rewards;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.initItem.StarterItemPreview;
import dev.lhkongyu.lhmiracleroad.config.strengthen.WeaponTypeConfig.WeaponTypes;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ClientData {

    public static final Map<String,Map<String,JsonObject>> EQUIPMENT = new ConcurrentHashMap<>();
    private static volatile EquipmentRules equipmentRules;
    private static volatile WeaponTypes weaponTypes;
    private static volatile Rewards soulRewards;
    private static Integer occupationLevelBase;

    public static final NavigableMap<String, Occupation> OCCUPATION_CATALOG = new TreeMap<>();

    public static final Map<String, StarterItemPreview> STARTER_ITEM_PREVIEWS = Maps.newHashMap();

    public static void resetEquipment(EquipmentRules rules) {
        EQUIPMENT.clear();
        equipmentRules = rules;
    }

    public static boolean hasEquipmentSnapshot() {
        return equipmentRules != null;
    }

    public static EquipmentRules getEquipmentRules() {
        return equipmentRules;
    }

    public static WeaponTypes getWeaponTypes() {
        return weaponTypes;
    }

    public static void setWeaponTypes(WeaponTypes types) {
        weaponTypes = types;
    }

    public static Rewards getSoulRewards() {
        return soulRewards;
    }

    public static void setSoulRewards(Rewards rewards) {
        soulRewards = rewards;
    }

    public static int occupationLevelBase() {
        return occupationLevelBase == null ? LHMiracleRoadConfig.COMMON.LEVEL_BASE.get() : occupationLevelBase;
    }

    public static void setOccupationLevelBase(Integer value) {
        occupationLevelBase = value;
    }

    public record EquipmentRules(boolean weightEnabled, boolean offhandWeightEnabled, boolean attributePenaltiesEnabled) {}
}
