package dev.lhkongyu.lhmiracleroad.items.trophy;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.items.TrophyItem;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.NameTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TrophyManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve(LHMiracleRoad.MODID).resolve("trophy");

    private static final Map<String, List<TrophyData>> DATA = new HashMap<>();

    private static final Map<String,List<ItemStack>> DISPLAY_CACHE = new HashMap<>();

    public static void load() {
        DATA.clear();
        try {
            Files.createDirectories(PATH);
            loadType(NameTool.COMMON);
            loadType(NameTool.EXQUISITE);
            loadType(NameTool.RARE);
            loadType(NameTool.EPIC);
            loadType(NameTool.LEGEND);
        } catch (Exception e) {
            LHMiracleRoad.LOGGER.error("Invalid trophy File", e);
        }
    }

    //初始化读取数据
    private static void loadType(String type) throws IOException {
        Path file = PATH.resolve(type + ".json");
        if (!Files.exists(file)) {
            saveDefault(type, file);
        }
        try (Reader reader = Files.newBufferedReader(file)) {
            Type listType = new TypeToken<List<TrophyData>>() {}.getType();
            List<TrophyData> data = GSON.fromJson(reader, listType);
            if (data == null)
                data = new ArrayList<>();
            for (TrophyData trophyData : data) {
                if (trophyData.entries == null)
                    trophyData.entries = new ArrayList<>();
            }
            DATA.put(type, data);
        }
    }

    //初始生成 json文件
    private static void saveDefault(String type, Path file) throws IOException {
        List<TrophyData> data = switch (type){
            case NameTool.COMMON -> TrophyTool.setCommon();
            case NameTool.EXQUISITE -> TrophyTool.setExquisite();
            case NameTool.RARE -> TrophyTool.setRare();
            case NameTool.EPIC -> TrophyTool.setEpic();
            case NameTool.LEGEND -> TrophyTool.setLegend();
            default -> new ArrayList<>();
        };

        if (data.isEmpty()) return;

        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(data, writer);
        }
    }

    //获取物品
    public static List<ItemStack> getRewards(String type, float luck) {

        List<TrophyData> groups = DATA.get(type);

        if (groups == null || groups.isEmpty()) return Collections.emptyList();

        // 第一层随机
        TrophyData data = randomGroup(groups, luck);
        if (data == null) return Collections.emptyList();

        int min = Math.max(0, data.roll_count_min);
        int max = Math.max(min, data.roll_count_max);
        int rollCount = Mth.nextInt(LHMiracleRoadTool.random, min, max);

        List<ItemStack> result = new ArrayList<>();
        List<TrophyEntry> pool = new ArrayList<>(data.entries);

        for (int i = 0; i < rollCount; i++) {
            ItemStack stack = randomEntry(pool, type, luck);
            if (!stack.isEmpty()) {
                result.add(stack);
                // 不允许重复
                if (!stack.isEmpty() && !data.allowRepeat) {
                    pool.removeIf(entry ->
                            entry.item.equals(
                                    ForgeRegistries.ITEMS.getKey(stack.getItem()).toString()));
                }
            }
        }

        return result;
    }

    //根据权重随机获取组
    private static TrophyData randomGroup(List<TrophyData> groups,float luck){
        int totalWeight = 0;

        for (TrophyData data : groups) {
            if (data.weight <= 0) continue;
            totalWeight += data.getWeight(luck);
        }

        if (totalWeight <= 0) return null;

        int random = ThreadLocalRandom.current().nextInt(totalWeight);
        int current = 0;

        for (TrophyData data : groups) {

            if (data.weight <= 0) continue;
            current += data.getWeight(luck);

            if (random < current) return data;
        }

        return null;
    }

    //根据权重随机获取 item
    private static ItemStack randomEntry(List<TrophyEntry> entries, String type, float luck) {

        if (entries.isEmpty()) return ItemStack.EMPTY;

        // 计算总权重（考虑幸运）
        int totalWeight = 0;
        for (TrophyEntry entry : entries) {
            if (entry.weight <= 0) continue;
            totalWeight += entry.getWeight(luck);
        }

        if (totalWeight <= 0) return ItemStack.EMPTY;
        int random = ThreadLocalRandom.current().nextInt(totalWeight);
        int current = 0;
        for (TrophyEntry entry : entries) {
            if (entry.weight <= 0) continue;
            current += entry.getWeight(luck);
            if (random >= current) continue;

            ResourceLocation id;
            try {
                id = new ResourceLocation(entry.item);
            }
            catch (Exception e) {
                LHMiracleRoad.LOGGER.error("Invalid Trophy ItemID File:{} item id:{}", type + ".json", entry.item);
                return ItemStack.EMPTY;
            }

            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) {
                LHMiracleRoad.LOGGER.error("Invalid Trophy Item File:{} item id:{}", type + ".json", entry.item);
                return ItemStack.EMPTY;
            }

            int minCount = Math.max(1, entry.min);
            int maxCount = Math.max(minCount, entry.max);

            int count = Mth.nextInt(LHMiracleRoadTool.random, minCount, maxCount);

            ItemStack stack = new ItemStack(item, count);

            // 读取NBT
            if (!entry.nbt.isBlank()) {
                try {
                    CompoundTag tag = TagParser.parseTag(entry.nbt);
                    stack.setTag(tag);
                }
                catch (Exception e) {
                    LHMiracleRoad.LOGGER.error("Invalid Trophy NBT File:{} item id:{} nbt:{}", type + ".json", entry.item, entry.nbt);
                    return ItemStack.EMPTY;
                }
            }
            return stack;
        }

        return ItemStack.EMPTY;
    }

    //预览池
    public static ItemStack getDisplayStack(TrophyItem trophy) {

        String type = trophy.getType();

        List<ItemStack> list = DISPLAY_CACHE.computeIfAbsent(type, TrophyManager::buildDisplayList);

        if (list.isEmpty()) return ItemStack.EMPTY;

        long index = System.currentTimeMillis() / 2000;

        return list.get((int)(index % list.size()));
    }

    private static List<ItemStack> buildDisplayList(String type) {
        List<ItemStack> result = new ArrayList<>();
        List<TrophyData> dataList = DATA.get(type);
        if (dataList == null) return result;

        for (TrophyData data : dataList) {
            for (TrophyEntry entry : data.entries) {
                ResourceLocation id;
                try {
                    id = new ResourceLocation(entry.item);
                }
                catch (Exception e) {
                    continue;
                }
                Item item = ForgeRegistries.ITEMS.getValue(id);
                if (item == null) continue;

                ItemStack stack = new ItemStack(item);
                if (!entry.nbt.isBlank()) {
                    try {
                        stack.setTag(TagParser.parseTag(entry.nbt));
                    }
                    catch (Exception ignored) {
                        continue;
                    }
                }
                result.add(stack);
            }
        }

        return result;
    }
}
