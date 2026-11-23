package dev.lhkongyu.lhmiracleroad.config.strengthen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class WeaponTypeConfig {
    private static final Gson GSON = new Gson();
    private static final String FILE_NAME = "weapon_types.json";
    private static final String DEFAULT_RESOURCE = "/defaults/" + LHMiracleRoad.MODID + "/strengthen/" + FILE_NAME;
    private static volatile WeaponTypes localTypes = new WeaponTypes(Set.of(), Set.of(), Set.of());

    private WeaponTypeConfig() {}

    public static void load() {
        Path file = FMLPaths.CONFIGDIR.get().resolve(LHMiracleRoad.MODID).resolve("strengthen").resolve(FILE_NAME);
        try {
            Files.createDirectories(file.getParent());
            if (Files.notExists(file)) {
                try (InputStream defaults = openDefaults()) {
                    Files.copy(defaults, file);
                }
            }
            try (Reader reader = Files.newBufferedReader(file)) {
                localTypes = read(reader);
            }
        } catch (IOException | JsonParseException ex) {
            LHMiracleRoad.LOGGER.warn("Could not read {}. Using bundled weapon types without changing the file.", file, ex);
            try (Reader reader = new InputStreamReader(openDefaults(), StandardCharsets.UTF_8)) {
                localTypes = read(reader);
            } catch (IOException | JsonParseException defaultsError) {
                LHMiracleRoad.LOGGER.error("Could not read bundled weapon types", defaultsError);
            }
        }
    }

    private static InputStream openDefaults() throws IOException {
        InputStream stream = WeaponTypeConfig.class.getResourceAsStream(DEFAULT_RESOURCE);
        if (stream == null) throw new IOException("Missing resource " + DEFAULT_RESOURCE);
        return stream;
    }

    private static WeaponTypes read(Reader reader) {
        JsonObject data = GSON.fromJson(reader, JsonObject.class);
        if (data == null) throw new JsonParseException("Expected an object of weapon types");
        return new WeaponTypes(readIds(data, "melee"), readIds(data, "ranged"), readIds(data, "magic_staff"));
    }

    private static Set<ResourceLocation> readIds(JsonObject data, String key) {
        JsonElement ids = data.get(key);
        if (ids == null) return Set.of();
        if (!ids.isJsonArray()) throw new JsonParseException("Expected an array for weapon type " + key);
        Set<ResourceLocation> result = new HashSet<>();
        for (JsonElement id : ids.getAsJsonArray()) {
            String name = id.isJsonPrimitive() && id.getAsJsonPrimitive().isString() ? id.getAsString() : "";
            ResourceLocation parsed = name.isBlank() ? null : ResourceLocation.tryParse(name);
            if (parsed == null || parsed.getPath().isEmpty()) {
                LHMiracleRoad.LOGGER.warn("Ignoring invalid item ID {} in weapon type {}", id, key);
                continue;
            }
            result.add(parsed);
        }
        return result;
    }

    public static WeaponTypes forSide(boolean client) {
        WeaponTypes synced = client ? ClientData.getWeaponTypes() : null;
        return synced == null ? localTypes : synced;
    }

    public static boolean contains(ItemStack stack, Type type) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) return false;
        WeaponTypes types = forSide(EffectiveSide.get().isClient());
        return switch (type) {
            case MELEE -> types.melee().contains(id);
            case RANGED -> types.ranged().contains(id);
            case MAGIC_STAFF -> types.magicStaff().contains(id);
        };
    }

    public enum Type { MELEE, RANGED, MAGIC_STAFF }

    public record WeaponTypes(Set<ResourceLocation> melee, Set<ResourceLocation> ranged, Set<ResourceLocation> magicStaff) {
        public WeaponTypes {
            melee = Set.copyOf(melee);
            ranged = Set.copyOf(ranged);
            magicStaff = Set.copyOf(magicStaff);
        }
    }
}
