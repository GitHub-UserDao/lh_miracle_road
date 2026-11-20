package dev.lhkongyu.lhmiracleroad.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.initItem.StarterItemPreview;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ClientData {

    public static final Map<String,Map<String,JsonObject>> EQUIPMENT = Maps.newHashMap();

    public static final NavigableMap<String, Occupation> OCCUPATION_CATALOG = new TreeMap<>();

    public static final Map<String, StarterItemPreview> STARTER_ITEM_PREVIEWS = Maps.newHashMap();
}
