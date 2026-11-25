package dev.lhkongyu.lhmiracleroad.config.editor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.common.collect.Multimap;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.client.screen.weaponPodium.WeaponPodiumMenu;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationConfig;
import dev.lhkongyu.lhmiracleroad.config.punishment.PunishmentConfig;
import dev.lhkongyu.lhmiracleroad.config.strengthen.StrengthenConfig;
import dev.lhkongyu.lhmiracleroad.config.strengthen.WeaponTypeConfig;
import dev.lhkongyu.lhmiracleroad.items.trophy.TrophyManager;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributeValuesSnapshot;
import dev.lhkongyu.lhmiracleroad.tool.ItemPunishmentTool;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import dev.lhkongyu.lhmiracleroad.tool.SyncTool;
import dev.lhkongyu.lhmiracleroad.tool.data.SynchronizationData;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public final class ConfigEditorService {
    private static final ConfigEditorStorage STORAGE =
            new ConfigEditorStorage(FMLPaths.CONFIGDIR.get().resolve(LHMiracleRoad.MODID));

    private static final ConfigEditorStorage COMMON_STORAGE = new ConfigEditorStorage(FMLPaths.CONFIGDIR.get());

    private ConfigEditorService() {}

    private static ConfigEditorStorage storage(ConfigSection section) {
        return section == ConfigSection.COMMON ? COMMON_STORAGE : STORAGE;
    }

    public static int open(ServerPlayer player) {
        if (!player.hasPermissions(2)) return 0;
        read(player, ConfigSection.OCCUPATION, true);
        return 1;
    }

    static void read(ServerPlayer player, ConfigSection section, boolean open) {
        if (!player.hasPermissions(2)) return;
        try {
            ConfigEditorPackets.respond(player, section, storage(section).read(section), "", open);
        } catch (IOException | RuntimeException failure) {
            fail(player, section, failure, open);
        }
    }

    static void save(ServerPlayer player, ConfigSection section, String revision, String json) {
        edit(player, section, revision, json);
    }

    static void restore(ServerPlayer player, ConfigSection section, String revision) {
        edit(player, section, revision, null);
    }

    private static void edit(ServerPlayer player, ConfigSection section, String revision, String json) {
        if (!player.hasPermissions(2)) return;
        try {
            var current = storage(section).read(section);
            if (!revision.equals(current.revision())) {
                ConfigEditorPackets.respond(player, section, current,
                        "配置已被修改，请重新加载并对照差异 / Configuration changed. Reload to compare.", false);
                return;
            }
            JsonObject next = json == null ? storage(section).previous(section).document()
                    : JsonParser.parseString(json).getAsJsonObject();
            ConfigEditorValidation.validate(section, current.document(), next, validationRules());
            MinecraftServer server = player.getServer();
            if (section == ConfigSection.OCCUPATION) {
                checkOccupationReferences(server, current.document(), next);
            }
            var oldModifiers = capturesEquipment(section) ? equipmentModifiers(server) : Map.<ServerPlayer, Map<ModifierKey, AttributeModifier>>of();
            var updated = json == null ? storage(section).restore(section, revision, () -> reloadConfig(section))
                    : storage(section).save(section, revision, next, () -> reloadConfig(section));
            if (section != ConfigSection.COMMON || !current.document().get("base").equals(next.get("base"))) {
                publish(section, server, oldModifiers);
            }
            ConfigEditorPackets.respond(player, section, updated, "", false);
            LHMiracleRoad.LOGGER.info("{} {} {} configuration", player.getGameProfile().getName(),
                    json == null ? "restored" : "edited", section);
        } catch (IOException | RuntimeException failure) {
            fail(player, section, failure, false);
        }
    }

    private static void fail(ServerPlayer player, ConfigSection section, Exception failure, boolean open) {
        if (failure instanceof IOException && !(failure instanceof ConfigEditorStorage.ConflictException)) {
            LHMiracleRoad.LOGGER.warn("Could not update " + section + " configuration", failure);
        }
        ConfigEditorStorage.Snapshot latest = null;
        try {
            latest = storage(section).read(section);
        } catch (IOException | RuntimeException ignored) {
            // An unreadable configuration cannot be sent back as a snapshot.
        }
        String reason = failure.getMessage();
        ConfigEditorPackets.respond(player, section, latest,
                "操作失败 / Configuration error: " + (reason == null ? failure.getClass().getSimpleName() : reason), open);
    }

    private static ConfigEditorValidation.Rules validationRules() {
        return new ConfigEditorValidation.Rules(id -> {
            ResourceLocation key = ResourceLocation.tryParse(id);
            return key != null && ForgeRegistries.ITEMS.containsKey(key) && !"minecraft:air".equals(key.toString());
        }, id -> LHMiracleRoadTool.stringConversionAttribute(id) != null,
                Set.copyOf(OccupationConfig.getAttributeTypeIds()), snbt -> {
            try {
                if (!snbt.isBlank()) TagParser.parseTag(snbt);
                return true;
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException invalid) {
                return false;
            }
        });
    }

    private static void checkOccupationReferences(MinecraftServer server, JsonObject oldDocument,
                                                   JsonObject nextDocument) throws IOException {
        Set<String> removedJobs = new HashSet<>(oldDocument.keySet());
        removedJobs.removeAll(nextDocument.keySet());
        Map<String, Map<String, Integer>> removedTypes = new HashMap<>();
        for (String id : oldDocument.keySet()) {
            if (!nextDocument.has(id)) continue;
            Map<String, Integer> removed = initialLevels(oldDocument.get(id));
            removed.keySet().removeAll(initialLevels(nextDocument.get(id)).keySet());
            if (!removed.isEmpty()) removedTypes.put(id, removed);
        }
        if (removedJobs.isEmpty() && removedTypes.isEmpty()) return;
        Set<String> validTypes = new HashSet<>();
        nextDocument.entrySet().forEach(entry -> validTypes.addAll(typeIds(entry.getValue())));
        var configuredEquipment = STORAGE.read(ConfigSection.EQUIPMENT).document().getAsJsonObject("items");
        for (var entry : configuredEquipment.entrySet()) {
            if (!(entry.getValue() instanceof JsonObject item)
                    || !item.has("attribute_need") || !item.get("attribute_need").isJsonArray()) continue;
            for (JsonElement rule : item.getAsJsonArray("attribute_need")) {
                if (!(rule instanceof JsonObject need) || !need.has("attribute_id")) continue;
                String type = need.get("attribute_id").getAsString();
                if (!validTypes.contains(type) && existedType(oldDocument, type)) {
                    throw new IllegalArgumentException(entry.getKey() + " uses occupation attribute " + type);
                }
            }
        }
        Set<String> oldTypes = new HashSet<>();
        oldDocument.entrySet().forEach(entry -> oldTypes.addAll(typeIds(entry.getValue())));
        if (oldTypes.contains("power") && !validTypes.contains("power")) {
            throw new IllegalArgumentException("Automatic equipment requirements use power");
        }

        Set<UUID> online = new HashSet<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            online.add(player.getUUID());
            player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER)
                    .ifPresent(data -> checkPlayerReference(player.getGameProfile().getName(), data.getOccupationId(),
                            data.getOccupationAttributeLevel()::get, removedJobs, removedTypes));
        }
        var playerData = server.getWorldPath(LevelResource.PLAYER_DATA_DIR);
        if (!Files.isDirectory(playerData)) return;
        try (var files = Files.list(playerData)) {
            for (var file : files.filter(path -> path.getFileName().toString().endsWith(".dat")).toList()) {
                String fileName = file.getFileName().toString();
                String id = fileName.substring(0, fileName.length() - 4);
                UUID playerId;
                try {
                    playerId = UUID.fromString(id);
                } catch (IllegalArgumentException invalidId) {
                    continue;
                }
                if (online.contains(playerId)) continue;
                var saved = NbtIo.readCompressed(file.toFile())
                        .getCompound("ForgeCaps").getCompound(LHMiracleRoad.MODID + ":player_occupation_attribute");
                var levels = saved.getCompound("occupationAttributeLevel");
                checkPlayerReference(id, saved.getString("occupationId"),
                        type -> levels.contains(type) ? levels.getInt(type) : null, removedJobs, removedTypes);
            }
        }
    }

    private static boolean existedType(JsonObject occupations, String type) {
        return occupations.entrySet().stream().anyMatch(entry -> typeIds(entry.getValue()).contains(type));
    }

    private static Set<String> typeIds(JsonElement value) {
        return initialLevels(value).keySet();
    }

    private static Map<String, Integer> initialLevels(JsonElement value) {
        Map<String, Integer> levels = new HashMap<>();
        if (value instanceof JsonObject object && object.has("occupationAttributes")
                && object.get("occupationAttributes").isJsonArray()) {
            for (JsonElement entry : object.getAsJsonArray("occupationAttributes")) {
                if (entry instanceof JsonObject type && type.has("type_id") && type.get("type_id").isJsonPrimitive()) {
                    int initial = type.has("init_lv") ? type.get("init_lv").getAsInt() : 0;
                    levels.put(type.get("type_id").getAsString(), initial);
                }
            }
        }
        return levels;
    }

    private static void checkPlayerReference(String player, String occupation, Function<String, Integer> level,
                                             Set<String> removedJobs, Map<String, Map<String, Integer>> removedTypes) {
        if (occupation == null) return;
        if (removedJobs.contains(occupation)) throw new IllegalArgumentException(player + " uses occupation " + occupation);
        Map<String, Integer> removed = removedTypes.get(occupation);
        if (removed == null) return;
        removed.forEach((type, initial) -> {
            Integer saved = level.apply(type);
            if (saved != null && !initial.equals(saved)) {
                throw new IllegalArgumentException(player + " has earned levels in " + type);
            }
        });
    }

    private static boolean capturesEquipment(ConfigSection section) {
        return section == ConfigSection.EQUIPMENT || section == ConfigSection.STRENGTHEN || section == ConfigSection.WEAPON_TYPES;
    }

    private static Map<ServerPlayer, Map<ModifierKey, AttributeModifier>> equipmentModifiers(MinecraftServer server) {
        Map<ServerPlayer, Map<ModifierKey, AttributeModifier>> result = new HashMap<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) result.put(player, equipmentModifiers(player));
        return result;
    }

    private static Map<ModifierKey, AttributeModifier> equipmentModifiers(ServerPlayer player) {
        Map<ModifierKey, AttributeModifier> result = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            var stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            Multimap<Attribute, AttributeModifier> modifiers = stack.getAttributeModifiers(slot);
            modifiers.forEach((attribute, modifier) -> result.put(new ModifierKey(attribute, modifier.getId()), modifier));
        }
        return result;
    }

    private static void refreshEquipment(ServerPlayer player, Map<ModifierKey, AttributeModifier> previous) {
        var current = equipmentModifiers(player);
        previous.forEach((key, old) -> {
            if (sameModifier(old, current.get(key))) return;
            var attribute = player.getAttribute(key.attribute());
            if (attribute != null) attribute.removeModifier(key.id());
        });
        current.forEach((key, newModifier) -> {
            if (sameModifier(newModifier, previous.get(key))) return;
            var attribute = player.getAttribute(key.attribute());
            if (attribute != null && !attribute.hasModifier(newModifier)) attribute.addTransientModifier(newModifier);
        });
    }

    private static boolean sameModifier(AttributeModifier one, AttributeModifier other) {
        return other != null && one.getAmount() == other.getAmount() && one.getOperation() == other.getOperation();
    }

    private static void reloadConfig(ConfigSection section) throws IOException {
        switch (section) {
            case OCCUPATION -> OccupationConfig.reloadFromEditor();
            case STRENGTHEN -> StrengthenConfig.reloadFromEditor();
            case WEAPON_TYPES -> WeaponTypeConfig.reloadFromEditor();
            case EQUIPMENT -> PunishmentConfig.reloadFromEditor();
            case TROPHY -> TrophyManager.reloadFromEditor();
            case COMMON -> CommonConfigEditor.reload();
        }
    }

    private static void publish(ConfigSection section, MinecraftServer server,
                                Map<ServerPlayer, Map<ModifierKey, AttributeModifier>> previousModifiers) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            try {
                refreshPlayer(section, player, previousModifiers.getOrDefault(player, Map.of()));
            } catch (RuntimeException failure) {
                LHMiracleRoad.LOGGER.error("Saved {} configuration but could not refresh {}", section,
                        player.getGameProfile().getName(), failure);
            }
        }
    }

    private static void refreshPlayer(ConfigSection section, ServerPlayer player,
                                      Map<ModifierKey, AttributeModifier> previousModifiers) {
        if (capturesEquipment(section)) refreshEquipment(player, previousModifiers);
        if (section == ConfigSection.COMMON) ItemPunishmentTool.refreshEquipmentWeights(player);
        if (section == ConfigSection.EQUIPMENT || section == ConfigSection.COMMON) ItemPunishmentTool.refreshCurioWeights(player);
        if (section == ConfigSection.EQUIPMENT || section == ConfigSection.OCCUPATION || section == ConfigSection.COMMON) {
            player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER)
                    .ifPresent(data -> {
                        if (section == ConfigSection.OCCUPATION || section == ConfigSection.COMMON) {
                            PlayerAttributeTool.refreshConfiguredOccupation(player, data);
                            SyncTool.synchronizationClient(data, player);
                        }
                        ItemPunishmentTool.refreshPenalties(player, data);
                        ItemPunishmentTool.updateLoad(player);
                    });
        }
        SynchronizationData.synchronization(player);
        if (section != ConfigSection.TROPHY) PlayerAttributeValuesSnapshot.send(player);
        if (capturesEquipment(section) && player.containerMenu instanceof WeaponPodiumMenu podium) {
            podium.createResult();
            podium.broadcastChanges();
        }
    }

    private record ModifierKey(Attribute attribute, UUID id) {}
}
