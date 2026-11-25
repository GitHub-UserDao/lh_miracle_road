package dev.lhkongyu.lhmiracleroad.config.strengthen;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class StrengthenConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve(LHMiracleRoad.MODID).resolve("strengthen");

    private static final Type WEAPONS = new TypeToken<List<ItemWeapons>>(){}.getType();
    private static final Type TOOLS = new TypeToken<List<ItemTool>>(){}.getType();
    private static final Type RANGED = new TypeToken<List<ItemRangedWeapons>>(){}.getType();
    private static final Type ARMOR = new TypeToken<List<ItemArmor>>(){}.getType();
    private static final Type MAGIC = new TypeToken<List<ItemMagicStaff>>(){}.getType();

    private static volatile Tables server = tables(createWeaponDefaults(), createToolDefaults(), createRangedDefaults(),
            createArmorDefaults(), createMagicDefaults());
    private static volatile Tables client;

    public static void load() {
        try {
            reloadFromEditor();
        } catch (Exception e) {
            LHMiracleRoad.LOGGER.error("Failed load strengthen config", e);
        }
    }

    public static void reloadFromEditor() throws IOException {
        Files.createDirectories(PATH);
        server = tables(
                    loadFile("weapon.json", WEAPONS, StrengthenConfig::createWeaponDefaults),
                    loadFile("tool.json", TOOLS, StrengthenConfig::createToolDefaults),
                    loadFile("ranged_weapon.json", RANGED, StrengthenConfig::createRangedDefaults),
                    loadFile("armor.json", ARMOR, StrengthenConfig::createArmorDefaults),
                    loadFile("magic_staffs.json", MAGIC, StrengthenConfig::createMagicDefaults));
    }

    public static JsonObject snapshot() {
        Tables data = server;
        JsonObject json = new JsonObject();
        json.add("weapon", GSON.toJsonTree(data.weapons().values()));
        json.add("tool", GSON.toJsonTree(data.tools().values()));
        json.add("ranged_weapon", GSON.toJsonTree(data.ranged().values()));
        json.add("armor", GSON.toJsonTree(data.armor().values()));
        json.add("magic_staffs", GSON.toJsonTree(data.magic().values()));
        return json;
    }

    public static void setClientSnapshot(JsonObject json) {
        client = json == null ? null : tables(
                GSON.fromJson(json.get("weapon"), WEAPONS),
                GSON.fromJson(json.get("tool"), TOOLS),
                GSON.fromJson(json.get("ranged_weapon"), RANGED),
                GSON.fromJson(json.get("armor"), ARMOR),
                GSON.fromJson(json.get("magic_staffs"), MAGIC));
    }

    private static Tables current() {
        Tables synced = client;
        return EffectiveSide.get().isClient() && synced != null ? synced : server;
    }

    private static Tables tables(List<ItemWeapons> weapons, List<ItemTool> tools, List<ItemRangedWeapons> ranged,
                                 List<ItemArmor> armor, List<ItemMagicStaff> magic) {
        return new Tables(index(weapons, ItemWeapons::getLv), index(tools, ItemTool::getLv),
                index(ranged, ItemRangedWeapons::getLv), index(armor, ItemArmor::getLv),
                index(magic, ItemMagicStaff::getLv));
    }

    private static <T> Map<Integer, T> index(List<T> entries, ToIntFunction<T> level) {
        Map<Integer, T> result = new HashMap<>();
        for (T entry : entries) result.put(level.applyAsInt(entry), entry);
        return Map.copyOf(result);
    }

    private record Tables(Map<Integer, ItemWeapons> weapons, Map<Integer, ItemTool> tools,
                          Map<Integer, ItemRangedWeapons> ranged, Map<Integer, ItemArmor> armor,
                          Map<Integer, ItemMagicStaff> magic) {}

    private static <T> List<T> loadFile(String fileName, Type type, Supplier<List<T>> defaultSupplier) throws IOException {
        Path file = PATH.resolve(fileName);

        if (!Files.exists(file)) {
            List<T> defaults = defaultSupplier.get();
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(defaults, writer);
            }
            return defaults;
        }

        try (Reader reader = Files.newBufferedReader(file)) {
            List<T> list = GSON.fromJson(reader, type);
            return list == null ? defaultSupplier.get() : list;
        }
    }

    private static List<ItemWeapons> createWeaponDefaults() {
        List<ItemWeapons> list = new ArrayList<>();
        for (int lv = 1; lv <= 3; lv++) {
            ItemWeapons data = new ItemWeapons();
            data.lv = lv;
            data.durability_magnification = 1.1;
            data.attack = 0.25;
            data.attack_speed = 0.02;
            list.add(data);
        }

        for (int lv = 4; lv <= 6; lv++) {
            ItemWeapons data = new ItemWeapons();
            data.lv = lv;
            data.durability_magnification = 1.25;
            data.attack = 0.5;
            data.attack_speed = 0.025;
            list.add(data);
        }

        for (int lv = 7; lv <= 9; lv++) {
            ItemWeapons data = new ItemWeapons();
            data.lv = lv;
            data.durability_magnification = 1.5;
            data.attack = 0.5;
            data.attack_speed = 0.03;
            list.add(data);
        }

        ItemWeapons data = new ItemWeapons();
        data.lv = 10;
        data.durability_magnification = 2;
        data.attack = 1.25;
        data.attack_speed = 0.075;
        list.add(data);

        return list;
    }

    private static List<ItemTool> createToolDefaults() {
        List<ItemTool> list = new ArrayList<>();
        for (int lv = 1; lv <= 3; lv++) {
            ItemTool data = new ItemTool();
            data.lv = lv;
            data.durability_magnification = 1.1;
            data.mining_speed = 0.025;
            list.add(data);
        }

        for (int lv = 4; lv <= 6; lv++) {
            ItemTool data = new ItemTool();
            data.lv = lv;
            data.durability_magnification = 1.25;
            data.mining_speed = 0.05;
            list.add(data);
        }

        for (int lv = 7; lv <= 9; lv++) {
            ItemTool data = new ItemTool();
            data.lv = lv;
            data.durability_magnification = 1.5;
            data.mining_speed = 0.075;
            list.add(data);
        }

        ItemTool data = new ItemTool();
        data.lv = 10;
        data.durability_magnification = 2;
        data.mining_speed = 0.2;
        list.add(data);

        return list;
    }

    private static List<ItemRangedWeapons> createRangedDefaults() {
        List<ItemRangedWeapons> list = new ArrayList<>();
        for (int lv = 1; lv <= 3; lv++) {
            ItemRangedWeapons data = new ItemRangedWeapons();
            data.lv = lv;
            data.durability_magnification = 1.1;
            data.ranged_attack = 0.02;
            list.add(data);
        }

        for (int lv = 4; lv <= 6; lv++) {
            ItemRangedWeapons data = new ItemRangedWeapons();
            data.lv = lv;
            data.durability_magnification = 1.25;
            data.ranged_attack = 0.03;
            list.add(data);
        }

        for (int lv = 7; lv <= 9; lv++) {
            ItemRangedWeapons data = new ItemRangedWeapons();
            data.lv = lv;
            data.durability_magnification = 1.5;
            data.ranged_attack = 0.05;
            list.add(data);
        }

        ItemRangedWeapons data = new ItemRangedWeapons();
        data.lv = 10;
        data.durability_magnification = 2;
        data.ranged_attack = 0.1;
        list.add(data);

        return list;
    }

    private static List<ItemArmor> createArmorDefaults() {
        List<ItemArmor> list = new ArrayList<>();
        for (int lv = 1; lv <= 3; lv++) {
            ItemArmor data = new ItemArmor();
            data.lv = lv;
            data.durability_magnification = 1.1;
            data.armor = 0.25;
            data.armor_toughness = 0.2;
            list.add(data);
        }

        for (int lv = 4; lv <= 6; lv++) {
            ItemArmor data = new ItemArmor();
            data.lv = lv;
            data.durability_magnification = 1.25;
            data.armor = 0.5;
            data.armor_toughness = 0.3;
            list.add(data);
        }

        for (int lv = 7; lv <= 9; lv++) {
            ItemArmor data = new ItemArmor();
            data.lv = lv;
            data.durability_magnification = 1.5;
            data.armor = 0.5;
            data.armor_toughness = 0.5;
            list.add(data);
        }

        ItemArmor data = new ItemArmor();
        data.lv = 10;
        data.durability_magnification = 2;
        data.armor = 1.25;
        data.armor_toughness = 1;
        list.add(data);

        return list;
    }

    private static List<ItemMagicStaff> createMagicDefaults() {
        List<ItemMagicStaff> list = new ArrayList<>();
        for (int lv = 1; lv <= 3; lv++) {
            ItemMagicStaff data = new ItemMagicStaff();
            data.lv = lv;
            data.durability_magnification = 1.1;
            data.magic_damage = 0.015;
            list.add(data);
        }

        for (int lv = 4; lv <= 6; lv++) {
            ItemMagicStaff data = new ItemMagicStaff();
            data.lv = lv;
            data.durability_magnification = 1.25;
            data.magic_damage = 0.025;
            list.add(data);
        }

        for (int lv = 7; lv <= 9; lv++) {
            ItemMagicStaff data = new ItemMagicStaff();
            data.lv = lv;
            data.durability_magnification = 1.5;
            data.magic_damage = 0.04;
            list.add(data);
        }

        ItemMagicStaff data = new ItemMagicStaff();
        data.lv = 10;
        data.durability_magnification = 2;
        data.magic_damage = 0.11;
        list.add(data);

        return list;
    }

    public static ItemWeapons getWeapon(int lv) {
        return atLevel(current().weapons(), lv);
    }

    public static ItemTool getTool(int lv) {
        return atLevel(current().tools(), lv);
    }

    public static ItemRangedWeapons getRangedWeapon(int lv) {
        return atLevel(current().ranged(), lv);
    }

    public static ItemArmor getArmor(int lv) {
        return atLevel(current().armor(), lv);
    }

    public static ItemMagicStaff getMagicStaff(int lv) {
        return atLevel(current().magic(), lv);
    }

    private static <T> T atLevel(Map<Integer, T> table, int level) {
        return table.getOrDefault(level, table.get(1));
    }
}
