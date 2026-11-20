package dev.lhkongyu.lhmiracleroad.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.initItem.StarterItemPreview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ClientData {

    public static final Map<String, JsonObject> ATTRIBUTE_POINTS_REWARDS = Maps.newHashMap();

    public static final Map<String,JsonArray> ATTRIBUTE_TYPES =  Maps.newLinkedHashMap();

    public static final Map<String,Map<String,JsonObject>> EQUIPMENT = Maps.newHashMap();

    public static final List<JsonObject> OCCUPATION = new ArrayList<>();
    public static final NavigableMap<String, Occupation> OCCUPATION_CATALOG = new TreeMap<>();

    public static final List<JsonObject> SHOW_GUI_ATTRIBUTE = new ArrayList<>();

    public static final Map<String, Double> SHOW_ATTRIBUTE_VALUE = Maps.newHashMap();

    public static final Map<String, JsonArray> INIT_ITEM = Maps.newHashMap();
    public static final Map<String, StarterItemPreview> STARTER_ITEM_PREVIEWS = Maps.newHashMap();
}
