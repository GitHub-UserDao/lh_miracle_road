package dev.lhkongyu.lhmiracleroad.tool;

import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.config.initItem.InitItemConfig;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationAttribute;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationConfig;
import dev.lhkongyu.lhmiracleroad.config.occupation.PointsReward;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerAttributeTool {

    public static void initOccupation(ServerPlayer player, String occupationId) {
        Occupation configured = OccupationConfig.getOccupation(occupationId);
        if (configured == null) return;

        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(attributes -> {
            if (attributes.getOccupationId() != null && !attributes.getOccupationId().isBlank()) return;
            applyConfiguredOccupation(player, configured, attributes, true);
            InitItemConfig.grantToPlayer(player, occupationId);
            LHMiracleRoadTool.playerPunishmentStateUpdate(player, attributes);
        });
    }

    public static void restoreConfiguredOccupation(ServerPlayer player, PlayerOccupationAttribute attributes) {
        Occupation occupation = OccupationConfig.getOccupation(attributes.getOccupationId());
        if (occupation != null) applyConfiguredOccupation(player, occupation, attributes, false);
    }

    private static void applyConfiguredOccupation(ServerPlayer player, Occupation occupation,
                                                  PlayerOccupationAttribute attributes, boolean resetLevels) {
        clearOccupationModifiers(player, attributes);
        player.getAttribute(LHMiracleRoadAttributes.BURDEN).setBaseValue(LHMiracleRoadConfig.COMMON.INIT_BURDEN.get());

        Map<String, Integer> levels = resetLevels ? new HashMap<>() : new HashMap<>(attributes.getOccupationAttributeLevel());
        if (occupation.getOccupationAttributes() != null) {
            for (OccupationAttribute type : occupation.getOccupationAttributes()) {
                String id = type.getType_id();
                if (id == null || id.isBlank()) continue;
                int level = levels.computeIfAbsent(id, key -> type.getInit_lv());
                applyConfiguredRewards(player, attributes, type, level, true);
            }
        }

        attributes.setOccupationId(occupation.getId());
        attributes.setOccupationAttributeLevel(levels);
        if (resetLevels) {
            int total = levels.values().stream().mapToInt(Integer::intValue).sum();
            attributes.setOccupationLevel(total - levels.size() * LHMiracleRoadConfig.COMMON.LEVEL_BASE.get() + 1);
        }
        attributes.setEmpiricalCalculationFormula(LHMiracleRoadConfig.COMMON.EMPIRICAL_CALCULATION_FORMULA.get());
        if (attributes.getAttributeMaxLevel() < 1) {
            attributes.setAttributeMaxLevel(LHMiracleRoadConfig.COMMON.ATTRIBUTE_MAX_LEVEL.get());
        }
        attributes.setMaxLevel(LHMiracleRoadConfig.COMMON.MAX_LEVEL.get());
    }

    private static OccupationAttribute configuredAttribute(Occupation occupation, String typeId) {
        if (occupation.getOccupationAttributes() == null) return null;
        for (OccupationAttribute type : occupation.getOccupationAttributes()) {
            if (typeId.equals(type.getType_id())) return type;
        }
        return null;
    }

    private static void applyConfiguredRewards(ServerPlayer player, PlayerOccupationAttribute attributes,
                                               OccupationAttribute type, int level, boolean recoverHealth) {
        int effectiveLevel = level + attributes.getCurioAttributeLevelValue(type.getType_id());
        for (PointsReward reward : type.getPointsRewards()) {
            String attributeId = reward.getAttribute_id();
            if (attributeId == null || attributeId.isBlank()) continue;

            AttributeModifier.Operation operation = LHMiracleRoadTool.stringConversionOperation(reward.getOperation());
            if (operation == null) continue;

            String namespace = reward.getMod_id();
            if (namespace == null || namespace.isBlank()) namespace = "minecraft";
            ResourceLocation id = ResourceLocation.tryParse(attributeId.contains(":") ? attributeId : namespace + ":" + attributeId);
            if (id == null) continue;

            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(id);
            if (attribute == null && id.getNamespace().equals("minecraft") && !id.getPath().contains(".")) {
                attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("minecraft", "generic." + id.getPath()));
            }
            if (attribute == null) continue;
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) continue;

            String key = ForgeRegistries.ATTRIBUTES.getKey(attribute) + "#" + type.getType_id();
            double amount = LHMiracleRoadTool.calculateTotalIncrease(effectiveLevel, reward.getValue(),
                    reward.getLv_promote_value(), reward.getLv_promote(), reward.getMin());
            AttributeModifier oldModifier = attributes.getAttributeModifier().remove(key);
            if (oldModifier != null) instance.removeModifier(oldModifier);
            if (amount != 0) {
                AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "", amount, operation);
                instance.addTransientModifier(modifier);
                attributes.addAttributeModifier(key, modifier);
            }
            if (attribute == Attributes.MAX_HEALTH) {
                player.setHealth(recoverHealth ? player.getMaxHealth() : Math.min(player.getHealth(), player.getMaxHealth()));
            }
        }
    }

    private static void clearOccupationModifiers(ServerPlayer player, PlayerOccupationAttribute attributes) {
        attributes.getAttributeModifier().forEach((key, modifier) -> {
            Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(key);
            AttributeInstance instance = attribute == null ? null : player.getAttribute(attribute);
            if (instance != null) instance.removeModifier(modifier);
        });
        attributes.clearAttributeModifier();
    }

    public static void refreshAttributeRewards(ServerPlayer player, PlayerOccupationAttribute attributes,
                                               String typeId, int level) {
        Occupation occupation = OccupationConfig.getOccupation(attributes.getOccupationId());
        if (occupation == null) return;
        OccupationAttribute type = configuredAttribute(occupation, typeId);
        if (type != null) applyConfiguredRewards(player, attributes, type, level, false);
    }

    public static void points(ServerPlayer player, String attributeTypeName, int count) {
        if (count != 1 && count != 10) return;
        Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
        if (optionalPlayerOccupationAttribute.isEmpty()) return;
        PlayerOccupationAttribute playerOccupationAttribute = optionalPlayerOccupationAttribute.get();
        String occupationId = playerOccupationAttribute.getOccupationId();
        if (occupationId == null || occupationId.isBlank()) return;

        Occupation occupation = OccupationConfig.getOccupation(occupationId);
        OccupationAttribute type = occupation == null ? null : configuredAttribute(occupation, attributeTypeName);
        if (type == null) return;

        Integer currentLevel = playerOccupationAttribute.getOccupationAttributeLevel().get(attributeTypeName);
        if (currentLevel == null) return;
        if (!isWithinAttributeLimit(playerOccupationAttribute, currentLevel, type.getMax_lv(), count)) return;

        String formula = LHMiracleRoadConfig.COMMON.EMPIRICAL_CALCULATION_FORMULA.get();
        UpgradePayment payment = quoteUpgrade(playerOccupationAttribute, count, formula);
        if (payment == null) return;

        int newLevel = currentLevel + count;
        applyConfiguredRewards(player, playerOccupationAttribute, type, newLevel, true);
        playerOccupationAttribute.putOccupationAttributeLevel(attributeTypeName, newLevel);
        playerOccupationAttribute.setPoints(payment.remainingPoints());
        playerOccupationAttribute.setOccupationExperience(payment.remainingSouls());
        playerOccupationAttribute.setOccupationLevel(playerOccupationAttribute.getOccupationLevel() + count);
        LHMiracleRoadTool.playerPunishmentStateUpdate(player, playerOccupationAttribute);
    }

    public static boolean isWithinAttributeLimit(PlayerOccupationAttribute attributes, int level, int configuredLimit, int count) {
        int override = attributes.getAttributeMaxLevel();
        return (long) level + count <= (override > 0 ? override : configuredLimit);
    }

    public static boolean canPayForUpgrade(PlayerOccupationAttribute attributes, int count) {
        return quoteUpgrade(attributes, count, attributes.getEmpiricalCalculationFormula()) != null;
    }

    private static UpgradePayment quoteUpgrade(PlayerOccupationAttribute attributes, int count, String formula) {
        int level = attributes.getOccupationLevel();
        if ((long) level + count > attributes.getMaxLevel()) return null;

        // Price every level before spending anything so a +10 request cannot partially succeed.
        int points = attributes.getPoints();
        int souls = attributes.getOccupationExperience();
        for (int i = 0; i < count; i++) {
            if (points > 0) {
                points--;
            } else {
                if (formula == null || formula.isBlank()) return null;
                int cost = Math.max(0, LHMiracleRoadTool.evaluateFormula(formula, level + i));
                if (souls < cost) return null;
                souls -= cost;
            }
        }
        return new UpgradePayment(points, souls);
    }

    private record UpgradePayment(int remainingPoints, int remainingSouls) {
    }

    public static void resetLevel(ServerPlayer player, PlayerOccupationAttribute attributes) {
        Occupation configured = OccupationConfig.getOccupation(attributes.getOccupationId());
        if (configured == null) return;
        applyConfiguredOccupation(player, configured, attributes, true);
        LHMiracleRoadTool.playerPunishmentStateUpdate(player, attributes);
    }

    public static void resetLevelReturnPoints(ServerPlayer player, PlayerOccupationAttribute attributes) {
        if (OccupationConfig.getOccupation(attributes.getOccupationId()) == null) return;
        Map<String, Integer> previousLevels = attributes.getOccupationAttributeLevel();
        resetLevel(player, attributes);
        int points = 0;
        for (Map.Entry<String, Integer> entry : attributes.getOccupationAttributeLevel().entrySet()) {
            int initial = entry.getValue();
            points += Math.max(0, previousLevels.getOrDefault(entry.getKey(), initial) - initial);
        }
        attributes.addPoints(points);
    }

    public static void resetOccupation(ServerPlayer player,PlayerOccupationAttribute playerOccupationAttribute){
        playerOccupationAttribute.setOccupationId(null);
        clearOccupationModifiers(player, playerOccupationAttribute);
        SyncTool.synchronizationClient(playerOccupationAttribute,player);
        //更新玩家奖惩状态
        LHMiracleRoadTool.playerPunishmentStateUpdate(player,playerOccupationAttribute);
    }
}
