package dev.lhkongyu.lhmiracleroad.config.strengthen;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraftforge.fml.loading.FMLPaths;

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

public class StrengthenConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve(LHMiracleRoad.MODID).resolve("strengthen");

    private static final Map<Integer, ItemWeapons> WEAPONS = new HashMap<>();
    private static final Map<Integer, ItemTool> TOOLS = new HashMap<>();
    private static final Map<Integer, ItemRangedWeapons> RANGED_WEAPONS = new HashMap<>();
    private static final Map<Integer, ItemArmor> ARMORS = new HashMap<>();
    private static final Map<Integer, ItemMagicStaff> MAGIC_STAFFS = new HashMap<>();

    public static void load() {
        try {
            Files.createDirectories(PATH);

            loadWeapon();
            loadTool();
            loadRangedWeapon();
            loadArmor();
            loadMagic();

        } catch (Exception e) {
            LHMiracleRoad.LOGGER.error("Failed load strengthen config", e);
        }
    }

    private static void loadWeapon() throws IOException {
        List<ItemWeapons> list = loadFile(
                "weapon.json",
                new TypeToken<List<ItemWeapons>>(){}.getType(),
                StrengthenConfig::createWeaponDefaults
        );

        WEAPONS.clear();

        for (ItemWeapons data : list) {
            WEAPONS.put(data.lv, data);
        }
    }

    private static void loadTool() throws IOException {
        List<ItemTool> list = loadFile(
                "tool.json",
                new TypeToken<List<ItemTool>>(){}.getType(),
                StrengthenConfig::createToolDefaults
        );

        TOOLS.clear();

        for (ItemTool data : list) {
            TOOLS.put(data.lv, data);
        }
    }

    private static void loadRangedWeapon() throws IOException {
        List<ItemRangedWeapons> list = loadFile(
                "ranged_weapon.json",
                new TypeToken<List<ItemRangedWeapons>>(){}.getType(),
                StrengthenConfig::createRangedDefaults
        );

        RANGED_WEAPONS.clear();

        for (ItemRangedWeapons data : list) {
            RANGED_WEAPONS.put(data.lv, data);
        }
    }

    private static void loadArmor() throws IOException {
        List<ItemArmor> list = loadFile(
                "armor.json",
                new TypeToken<List<ItemArmor>>(){}.getType(),
                StrengthenConfig::createArmorDefaults
        );

        ARMORS.clear();

        for (ItemArmor data : list) {
            ARMORS.put(data.lv, data);
        }
    }

    private static void loadMagic() throws IOException {
        List<ItemMagicStaff> list = loadFile(
                "magic_staffs.json",
                new TypeToken<List<ItemMagicStaff>>(){}.getType(),
                StrengthenConfig::createMagicDefaults
        );

        MAGIC_STAFFS.clear();

        for (ItemMagicStaff data : list) {
            MAGIC_STAFFS.put(data.lv, data);
        }
    }

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
        return WEAPONS.getOrDefault(lv, WEAPONS.get(1));
    }

    public static ItemTool getTool(int lv) {
        return TOOLS.getOrDefault(lv, TOOLS.get(1));
    }

    public static ItemRangedWeapons getRangedWeapon(int lv) {
        return RANGED_WEAPONS.getOrDefault(lv, RANGED_WEAPONS.get(1));
    }

    public static ItemArmor getArmor(int lv) {
        return ARMORS.getOrDefault(lv, ARMORS.get(1));
    }

    public static ItemMagicStaff getMagicStaff(int lv) {
        return MAGIC_STAFFS.getOrDefault(lv, MAGIC_STAFFS.get(1));
    }
}
