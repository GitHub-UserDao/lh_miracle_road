package dev.lhkongyu.lhmiracleroad.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SoulItemConfig {
    public static final ResourceLocation DEATH_SOUL_ID = new ResourceLocation(LHMiracleRoad.MODID, "death_soul");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Rewards DEFAULTS = createDefaults();
    private static volatile Rewards localRewards = DEFAULTS;

    private SoulItemConfig() {}

    public static void load() {
        Path file = FMLPaths.CONFIGDIR.get().resolve(LHMiracleRoad.MODID).resolve("soul.json");
        try {
            Files.createDirectories(file.getParent());
            if (Files.notExists(file)) {
                Files.writeString(file, GSON.toJson(toJson(DEFAULTS)) + "\n",
                        StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            }
            try (Reader reader = Files.newBufferedReader(file)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                if (json == null) throw new JsonParseException("Expected an object of soul item rewards");
                localRewards = fromJson(json);
            }
        } catch (IOException | JsonParseException ex) {
            LHMiracleRoad.LOGGER.warn("Could not read soul item config {}. Using defaults.", file, ex);
            localRewards = DEFAULTS;
        }
    }

    public static Rewards forSide(boolean client) {
        Rewards synced = client ? ClientData.getSoulRewards() : null;
        return synced == null ? localRewards : synced;
    }

    public static JsonObject toJson(Rewards rewards) {
        JsonObject items = new JsonObject();
        rewards.soulItems().forEach(items::addProperty);
        JsonObject deathSoul = new JsonObject();
        deathSoul.addProperty("soul_multiplier", rewards.deathSoul().multiplier());
        deathSoul.addProperty("skill_points", rewards.deathSoul().skillPoints());
        JsonObject json = new JsonObject();
        json.add("soul_items", items);
        json.add("death_soul", deathSoul);
        return json;
    }

    public static Rewards fromJson(JsonObject json) {
        if (json == null) return DEFAULTS;
        JsonObject items = section(json, "soul_items");
        Map<String, Integer> amounts = new LinkedHashMap<>(DEFAULTS.soulItems());
        amounts.replaceAll((id, amount) -> readInt(items.get(id), amount));

        JsonObject deathSoul = section(json, "death_soul");
        DeathSoul defaults = DEFAULTS.deathSoul();
        return new Rewards(amounts, new DeathSoul(
                readMultiplier(deathSoul.get("soul_multiplier"), defaults.multiplier()),
                readInt(deathSoul.get("skill_points"), defaults.skillPoints())));
    }

    private static JsonObject section(JsonObject json, String key) {
        JsonElement value = json.get(key);
        return value != null && value.isJsonObject() ? value.getAsJsonObject() : new JsonObject();
    }

    private static boolean isNumber(JsonElement value) {
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber();
    }

    private static int readInt(JsonElement value, int fallback) {
        if (!isNumber(value)) return fallback;
        try {
            int amount = value.getAsBigDecimal().intValueExact();
            return amount >= 0 ? amount : fallback;
        } catch (ArithmeticException | NumberFormatException ex) {
            return fallback;
        }
    }

    private static double readMultiplier(JsonElement value, double fallback) {
        if (!isNumber(value)) return fallback;
        double multiplier = value.getAsDouble();
        return Double.isFinite(multiplier) && multiplier >= 0 ? multiplier : fallback;
    }

    private static Rewards createDefaults() {
        Map<String, Integer> items = new LinkedHashMap<>();
        String prefix = LHMiracleRoad.MODID + ":";
        items.put(prefix + "soon_elapse_soul", 100);
        items.put(prefix + "incomplete_soul", 300);
        items.put(prefix + "large_block_soul", 1000);
        items.put(prefix + "stray_large_block_soul", 1500);
        items.put(prefix + "adventurer_large_block_soul", 2000);
        items.put(prefix + "unknown_soldier_soul", 4000);
        items.put(prefix + "unknown_soldier_large_block_soul", 8000);
        items.put(prefix + "exhausted_knight_soul", 15000);
        items.put(prefix + "exhausted_general_soul", 30000);
        items.put(prefix + "liege_soul", 50000);
        items.put(prefix + "king_soul", 100000);
        return new Rewards(items, new DeathSoul(2.5, 2));
    }

    public record Rewards(Map<String, Integer> soulItems, DeathSoul deathSoul) {
        public Rewards {
            soulItems = Collections.unmodifiableMap(new LinkedHashMap<>(soulItems));
        }

        public Integer amount(ResourceLocation itemId) {
            return itemId == null ? null : soulItems.get(itemId.toString());
        }
    }

    public record DeathSoul(double multiplier, int skillPoints) {
        public int successTotal(int currentSouls) {
            return (int) Math.min(Integer.MAX_VALUE, Math.round(currentSouls * multiplier));
        }
    }
}
