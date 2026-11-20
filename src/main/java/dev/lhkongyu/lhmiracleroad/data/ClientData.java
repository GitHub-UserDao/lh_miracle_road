package dev.lhkongyu.lhmiracleroad.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.initItem.StarterItemPreview;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ClientData {

    public static final Map<String,Map<String,JsonObject>> EQUIPMENT = new ConcurrentHashMap<>();
    private static volatile boolean equipmentSnapshot;

    public static final NavigableMap<String, Occupation> OCCUPATION_CATALOG = new TreeMap<>();

    public static final Map<String, StarterItemPreview> STARTER_ITEM_PREVIEWS = Maps.newHashMap();

    public static void resetEquipment(boolean synced) {
        EQUIPMENT.clear();
        equipmentSnapshot = synced;
    }

    public static boolean hasEquipmentSnapshot() {
        return equipmentSnapshot;
    }
}
