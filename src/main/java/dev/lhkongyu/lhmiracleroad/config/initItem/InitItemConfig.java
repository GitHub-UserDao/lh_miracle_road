package dev.lhkongyu.lhmiracleroad.config.initItem;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class InitItemConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type INIT_ITEM_LIST_TYPE = new TypeToken<List<OccupationInitItem>>(){}.getType();
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve(LHMiracleRoad.MODID).resolve("initItem");

    private static final Map<String, OccupationInitItem> INIT_ITEM_BY_OCCUPATION_ID = new HashMap<>();

    public static void load() {
        try {
            Files.createDirectories(PATH);

            List<Path> files = findJsonFiles();
            if (files.isEmpty()) {
                for (OccupationInitItem initItem : createDefaults()) {
                    if (initItem == null || initItem.occupation_id == null || initItem.occupation_id.isBlank()) {
                        continue;
                    }
                    writeFile(PATH.resolve(initItem.occupation_id + ".json"), initItem);
                }
                files = findJsonFiles();
            }

            INIT_ITEM_BY_OCCUPATION_ID.clear();

            for (Path file : files) {
                for (OccupationInitItem initItem : readFile(file)) {
                    if (initItem == null || initItem.occupation_id == null || initItem.occupation_id.isBlank()) {
                        continue;
                    }
                    INIT_ITEM_BY_OCCUPATION_ID.put(initItem.occupation_id, initItem);
                }
            }
        } catch (Exception e) {
            LHMiracleRoad.LOGGER.error("Failed load init item config", e);
        }
    }

    private static List<Path> findJsonFiles() throws IOException {
        try (Stream<Path> paths = Files.walk(PATH)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        }
    }

    private static List<OccupationInitItem> readFile(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonElement element = GSON.fromJson(reader, JsonElement.class);
            if (element == null || element.isJsonNull()) {
                return Collections.emptyList();
            }
            if (element.isJsonArray()) {
                List<OccupationInitItem> initItems = GSON.fromJson(element, INIT_ITEM_LIST_TYPE);
                return initItems == null ? Collections.emptyList() : initItems;
            }
            if (element.isJsonObject()) {
                OccupationInitItem initItem = GSON.fromJson(element, OccupationInitItem.class);
                return initItem == null ? Collections.emptyList() : List.of(initItem);
            }
            throw new JsonParseException("Init item config must be a json object or array: " + file);
        }
    }

    private static void writeFile(Path file, OccupationInitItem initItem) throws IOException {
        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(initItem, writer);
        }
    }

    private static List<OccupationInitItem> createDefaults() {
        return List.of(
                createInitItem("assassin",
                        item("minecraft:iron_sword", 1, false, false, false),
                        item("epicfight:iron_dagger", 2, true, false, false),
                        item("minecraft:bread", 12, false, false, false),
                        item("minecraft:chainmail_helmet", 1, false, true, false),
                        item("minecraft:chainmail_chestplate", 1, false, true, false),
                        item("minecraft:chainmail_leggings", 1, false, true, false),
                        item("minecraft:chainmail_boots", 1, false, true, false)
                ),
                createInitItem("knight",
                        item("minecraft:iron_sword", 1, false, false, false),
                        item("epicfight:iron_longsword", 1, false, false, false),
                        item("minecraft:bread", 12, false, false, false),
                        item("minecraft:iron_helmet", 1, false, true, false),
                        item("minecraft:iron_chestplate", 1, false, true, false),
                        item("minecraft:iron_leggings", 1, false, true, false),
                        item("minecraft:iron_boots", 1, false, true, false),
                        item("minecraft:shield", 1, false, false, true)
                ),
                createInitItem("mage",
                        item("minecraft:leather_chestplate", 1, "{Damage:0}", false, true, false),
                        item("minecraft:leather_boots", 1, "{Damage:0}", false, true, false),
                        item("minecraft:leather_leggings", 1, "{Damage:0}", false, true, false),
                        item("minecraft:leather_helmet", 1, "{Damage:0}", false, true, false),
                        item("minecraft:bread", 12, false, false, false),
                        item("irons_spellbooks:copper_spell_book", 1, "{ISB_spellbook:{activeSpellIndex:0,spellSlots:4,spells:[{level:1,sid:\"irons_spellbooks:firebolt\",slot:0}]}}", false, false, false),
                        item("ars_nouveau:novice_spell_book", 1, false, false, false)
                ),
                createInitItem("outcast",
                        item("minecraft:bread", 6, false, false, false)
                ),
                createInitItem("samurai",
                        item("minecraft:iron_sword", 1, false, false, false),
                        item("epicfight:iron_tachi", 1, false, false, false),
                        item("minecraft:bread", 12, false, false, false),
                        item("minecraft:iron_helmet", 1, false, true, false),
                        item("minecraft:iron_chestplate", 1, false, true, false),
                        item("minecraft:iron_leggings", 1, false, true, false),
                        item("minecraft:iron_boots", 1, false, true, false)
                ),
                createInitItem("ranger",
                        item("minecraft:bow", 1, false, false, false),
                        item("minecraft:arrow", 24, false, false, false),
                        item("minecraft:bread", 12, false, false, false),
                        item("minecraft:leather_helmet", 1, false, true, false),
                        item("minecraft:leather_chestplate", 1, false, true, false),
                        item("minecraft:leather_leggings", 1, false, true, false),
                        item("minecraft:leather_boots", 1, false, true, false)
                )
        );
    }

    private static OccupationInitItem createInitItem(String occupationId, OccupationItem... items) {
        OccupationInitItem initItem = new OccupationInitItem();
        initItem.occupation_id = occupationId;
        initItem.occupationItems = List.of(items);
        return initItem;
    }

    private static OccupationItem item(String item, int quantity, boolean isSplit, boolean isWear, boolean isDeputy) {
        return item(item, quantity, null, isSplit, isWear, isDeputy);
    }

    private static OccupationItem item(String item, int quantity, String tag, boolean isSplit, boolean isWear, boolean isDeputy) {
        OccupationItem occupationItem = new OccupationItem();
        occupationItem.item = item;
        occupationItem.quantity = quantity <= 0 ? 1 : quantity;
        occupationItem.tag = tag;
        occupationItem.is_split = isSplit;
        occupationItem.is_wear = isWear;
        occupationItem.is_deputy = isDeputy;
        return occupationItem;
    }

    public static OccupationInitItem getInitItem(String occupationId) {
        return INIT_ITEM_BY_OCCUPATION_ID.get(occupationId);
    }
}
