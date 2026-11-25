package dev.lhkongyu.lhmiracleroad.config.initItem;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;
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

            replace(readAll(files));
        } catch (Exception e) {
            LHMiracleRoad.LOGGER.error("Failed load init item config", e);
        }
    }

    private static Map<String, OccupationInitItem> readAll(List<Path> files) throws IOException {
        Map<String, OccupationInitItem> loaded = new HashMap<>();
        Map<String, OccupationInitItem> canonical = new HashMap<>();
        for (Path file : files) {
            for (OccupationInitItem initItem : readFile(file)) {
                if (initItem == null || initItem.occupation_id == null || initItem.occupation_id.isBlank()) continue;
                loaded.put(initItem.occupation_id, initItem);
                if (PATH.equals(file.getParent()) && file.getFileName().toString().equals(initItem.occupation_id + ".json")) {
                    canonical.put(initItem.occupation_id, initItem);
                }
            }
        }
        loaded.putAll(canonical);
        return loaded;
    }

    private static void replace(Map<String, OccupationInitItem> loaded) {
        INIT_ITEM_BY_OCCUPATION_ID.clear();
        INIT_ITEM_BY_OCCUPATION_ID.putAll(loaded);
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
            writer.write(GSON.toJson(initItem));
        }
    }

    public static void saveFromPlayer(ServerPlayer player, String occupationId) throws IOException {
        if (!occupationId.matches("[a-zA-Z0-9_-][a-zA-Z0-9_.-]*")) {
            throw new IllegalArgumentException("Invalid occupation ID");
        }
        List<OccupationItem> items = new ArrayList<>();
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.items.size(); i++) {
            capture(items, inventory.items.get(i), i == inventory.selected ? EquipmentSlot.MAINHAND : null);
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot != EquipmentSlot.MAINHAND) capture(items, player.getItemBySlot(slot), slot);
        }
        OccupationInitItem entry = createInitItem(occupationId, items.toArray(OccupationItem[]::new));
        Files.createDirectories(PATH);
        Path target = PATH.resolve(occupationId + ".json");
        Path temp = Files.createTempFile(PATH, "init-item-", ".tmp");
        try {
            writeFile(temp, entry);
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
        INIT_ITEM_BY_OCCUPATION_ID.put(occupationId, entry);
    }

    public static void copyOutcastItemsIfAbsent(String occupationId) throws IOException {
        OccupationInitItem template = getInitItem("outcast");
        if (template == null || getInitItem(occupationId) != null) return;
        Path target = PATH.resolve(occupationId + ".json");
        if (Files.exists(target)) return;
        OccupationInitItem copy = GSON.fromJson(GSON.toJsonTree(template), OccupationInitItem.class);
        copy.occupation_id = occupationId;
        Files.createDirectories(PATH);
        Path temp = Files.createTempFile(PATH, "init-item-", ".tmp");
        try {
            writeFile(temp, copy);
            Files.move(temp, target);
        } finally {
            Files.deleteIfExists(temp);
        }
        INIT_ITEM_BY_OCCUPATION_ID.put(occupationId, copy);
    }

    private static void capture(List<OccupationItem> items, ItemStack stack, EquipmentSlot slot) {
        if (stack.isEmpty()) return;
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) return;
        items.add(item(id.toString(), stack.getCount(), stack.hasTag() ? stack.getTag().toString() : null,
                false, slot != null && slot.getType() == EquipmentSlot.Type.ARMOR,
                slot == EquipmentSlot.OFFHAND, slot == EquipmentSlot.MAINHAND));
    }

    private static ItemStack toStack(OccupationItem entry, String occupationId) {
        if (entry == null || entry.item == null || entry.quantity <= 0) return ItemStack.EMPTY;
        ResourceLocation id = ResourceLocation.tryParse(entry.item);
        Item item = id == null ? null : ForgeRegistries.ITEMS.getValue(id);
        if (item == null || item == Items.AIR) return ItemStack.EMPTY;
        ItemStack stack = new ItemStack(item, entry.quantity);
        if (entry.tag != null && !entry.tag.isBlank()) {
            try {
                stack.setTag(TagParser.parseTag(entry.tag));
            } catch (CommandSyntaxException e) {
                LHMiracleRoad.LOGGER.warn("Invalid starter item tag for {} in occupation {}", id, occupationId, e);
            }
        }
        return stack;
    }

    public static Map<String, StarterItemPreview> previewItems() {
        Map<String, StarterItemPreview> preview = new HashMap<>();
        INIT_ITEM_BY_OCCUPATION_ID.keySet().forEach(id -> preview.put(id, previewItems(id)));
        return preview;
    }

    public static StarterItemPreview previewItems(String occupationId) {
        Map<EquipmentSlot, ItemStack> equipment = new EnumMap<>(EquipmentSlot.class);
        List<ItemStack> inventory = new ArrayList<>();
        distributeStarterItems(occupationId, equipment::put, (stack, limit) -> inventory.add(stack));
        return new StarterItemPreview(equipment, inventory);
    }

    public static void grantToPlayer(ServerPlayer player, String occupationId) {
        distributeStarterItems(occupationId, (slot, stack) -> {
            ItemStack previous = player.getItemBySlot(slot);
            player.setItemSlot(slot, stack);
            if (!previous.isEmpty()) player.getInventory().placeItemBackInInventory(previous);
        }, (stack, limit) -> {
            while (!stack.isEmpty()) player.getInventory().placeItemBackInInventory(stack.split(limit));
        });
    }

    private static void distributeStarterItems(String occupationId, BiConsumer<EquipmentSlot, ItemStack> equip,
                                              ObjIntConsumer<ItemStack> store) {
        OccupationInitItem entry = getInitItem(occupationId);
        if (entry == null || entry.occupationItems == null) return;
        EnumSet<EquipmentSlot> equipped = EnumSet.noneOf(EquipmentSlot.class);
        for (OccupationItem item : entry.occupationItems) {
            ItemStack stack = toStack(item, occupationId);
            if (stack.isEmpty()) continue;
            EquipmentSlot slot = null;
            if (item.is_main_hand) slot = EquipmentSlot.MAINHAND;
            else if (item.is_deputy) slot = EquipmentSlot.OFFHAND;
            else if (item.is_wear) {
                EquipmentSlot preferred = LivingEntity.getEquipmentSlotForItem(stack);
                if (preferred.getType() == EquipmentSlot.Type.ARMOR) slot = preferred;
            }
            int limit = item.is_split ? 1 : stack.getMaxStackSize();
            if (slot != null && equipped.add(slot)) {
                equip.accept(slot, stack.split(slot.getType() == EquipmentSlot.Type.ARMOR ? 1 : limit));
            }
            if (!stack.isEmpty()) store.accept(stack, limit);
        }
    }

    private static List<OccupationInitItem> createDefaults() {
        return List.of(
                createInitItem("assassin",
                        item("minecraft:iron_sword", 1, false, false, false,true),
                        item("epicfight:iron_dagger", 2, true, false, false),
                        item("minecraft:bread", 12, false, false, false),
                        item("minecraft:chainmail_helmet", 1, false, true, false),
                        item("minecraft:chainmail_chestplate", 1, false, true, false),
                        item("minecraft:chainmail_leggings", 1, false, true, false),
                        item("minecraft:chainmail_boots", 1, false, true, false)
                ),
                createInitItem("knight",
                        item("minecraft:iron_sword", 1, false, false, false,true),
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
                        item("irons_spellbooks:copper_spell_book", 1, "{ISB_spellbook:{activeSpellIndex:0,spellSlots:4,spells:[{level:1,sid:\"irons_spellbooks:firebolt\",slot:0}]}}", false, false, false,true),
                        item("ars_nouveau:novice_spell_book", 1, false, false, false)
                ),
                createInitItem("outcast",
                        item("minecraft:bread", 6, false, false, false)
                ),
                createInitItem("samurai",
                        item("minecraft:iron_sword", 1, false, false, false,true),
                        item("epicfight:iron_tachi", 1, false, false, false),
                        item("minecraft:bread", 12, false, false, false),
                        item("minecraft:iron_helmet", 1, false, true, false),
                        item("minecraft:iron_chestplate", 1, false, true, false),
                        item("minecraft:iron_leggings", 1, false, true, false),
                        item("minecraft:iron_boots", 1, false, true, false)
                ),
                createInitItem("ranger",
                        item("minecraft:bow", 1, false, false, false,true),
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
        return item(item, quantity, null, isSplit, isWear, isDeputy,false);
    }

    private static OccupationItem item(String item, int quantity, String tag, boolean isSplit, boolean isWear, boolean isDeputy) {
        return item(item, quantity, tag, isSplit, isWear, isDeputy,false);
    }

    private static OccupationItem item(String item, int quantity, boolean isSplit, boolean isWear, boolean isDeputy,boolean isMainHand) {
        return item(item, quantity, null, isSplit, isWear, isDeputy,isMainHand);
    }

    private static OccupationItem item(String item, int quantity, String tag, boolean isSplit, boolean isWear, boolean isDeputy,boolean isMainHand) {
        OccupationItem occupationItem = new OccupationItem();
        occupationItem.item = item;
        occupationItem.quantity = quantity <= 0 ? 1 : quantity;
        occupationItem.tag = tag;
        occupationItem.is_split = isSplit;
        occupationItem.is_wear = isWear;
        occupationItem.is_deputy = isDeputy;
        occupationItem.is_main_hand = isMainHand;
        return occupationItem;
    }

    public static OccupationInitItem getInitItem(String occupationId) {
        return INIT_ITEM_BY_OCCUPATION_ID.get(occupationId);
    }
}
