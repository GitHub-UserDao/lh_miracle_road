package dev.lhkongyu.lhmiracleroad.tool;

import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationConfig;
import dev.lhkongyu.lhmiracleroad.config.punishment.PunishmentConfig;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ItemPunishmentTool {
    private static final UUID LOAD_MODIFIER = modifierId("load");
    private static final EquipmentProfile EMPTY = new EquipmentProfile(0, List.of());

    private ItemPunishmentTool() {}

    public static void addWeight(ItemAttributeModifierEvent event) {
        boolean client = EffectiveSide.get().isClient();
        if (!equipmentWeightEnabled(client)) return;
        EquipmentSlot slot = event.getSlotType();
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        if (slot == EquipmentSlot.OFFHAND && (!offhandWeightEnabled(client)
                || !(LHMiracleRoadTool.itemIsShield(stack) || stack.canPerformAction(ToolActions.SHIELD_BLOCK)))) return;
        if (slot.getType() == EquipmentSlot.Type.ARMOR && LivingEntity.getEquipmentSlotForItem(stack) != slot) return;
        if (slot == EquipmentSlot.MAINHAND && LHMiracleRoadTool.itemIsArmors(stack)) return;

        double heavy = profile(stack, client).heavy();
        if (heavy <= 0) return;
        event.addModifier(LHMiracleRoadAttributes.HEAVY, weightModifier(slot.getName(), heavy));
    }

    public static void addCurioWeight(CurioAttributeModifierEvent event) {
        boolean client = EffectiveSide.get().isClient();
        if (!equipmentWeightEnabled(client)) return;
        var slot = event.getSlotContext();
        if (slot.cosmetic()) return;
        double heavy = profile(event.getItemStack(), client).heavy();
        if (heavy > 0) {
            event.addModifier(LHMiracleRoadAttributes.HEAVY, weightModifier(curioSlot(slot.identifier(), slot.index()), heavy));
        }
    }

    public static boolean equipmentWeightEnabled(boolean client) {
        var rules = client ? ClientData.getEquipmentRules() : null;
        return rules == null ? LHMiracleRoadConfig.COMMON.ENABLE_EQUIPMENT_WEIGHT.get() : rules.weightEnabled();
    }

    private static boolean offhandWeightEnabled(boolean client) {
        var rules = client ? ClientData.getEquipmentRules() : null;
        return rules == null ? LHMiracleRoadConfig.COMMON.IS_OFFHAND_CALCULATE_HEAVY.get() : rules.offhandWeightEnabled();
    }

    public static boolean attributePenaltiesEnabled(boolean client) {
        var rules = client ? ClientData.getEquipmentRules() : null;
        return rules == null ? LHMiracleRoadConfig.COMMON.IS_SKILL_POINTS_RESTRICT.get() : rules.attributePenaltiesEnabled();
    }

    private static AttributeModifier weightModifier(String slot, double heavy) {
        return new AttributeModifier(modifierId("weight/" + slot), "Equipment weight", heavy, AttributeModifier.Operation.ADDITION);
    }

    private static String curioSlot(String type, int index) {
        return "curio/" + type + "/" + index;
    }

    public static List<Requirement> requirements(ItemStack stack, boolean client) {
        return profile(stack, client).requirements();
    }

    private static EquipmentProfile profile(ItemStack stack, boolean client) {
        if (stack.isEmpty()) return EMPTY;
        Item item = stack.getItem();
        Map<String, Map<String, JsonObject>> equipment = client && ClientData.hasEquipmentSnapshot()
                ? ClientData.EQUIPMENT : PunishmentConfig.getEquipment();
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        JsonObject data = id == null ? null : equipment.getOrDefault(id.getNamespace(), Map.of()).get(id.getPath());
        if (data != null) {
            List<Requirement> requirements = new ArrayList<>();
            for (JsonElement entry : LHMiracleRoadTool.isAsJsonArray(data.get("attribute_need"))) {
                if (!entry.isJsonObject()) continue;
                JsonObject need = entry.getAsJsonObject();
                String type = LHMiracleRoadTool.isAsString(need.get("attribute_id"));
                if (type == null || type.isBlank()) continue;
                int level = LHMiracleRoadTool.isAsInt(need.get("need_points"));
                String preset = LHMiracleRoadTool.isAsString(need.get("punishment_id"));
                requirements.add(new Requirement(type, level, preset == null || preset.isBlank() ? ResourceLocationTool.DEFAULT : preset));
            }
            return new EquipmentProfile(Math.max(0, LHMiracleRoadTool.isAsInt(data.get("heavy"))), requirements);
        }
        return defaultProfile(stack);
    }

    public static double defaultHeavy(ItemStack stack) {
        return defaultProfile(stack).heavy();
    }

    public static List<Requirement> defaultRequirements(ItemStack stack) {
        return defaultProfile(stack).requirements();
    }

    private static EquipmentProfile defaultProfile(ItemStack stack) {
        if (stack.isEmpty()) return EMPTY;
        Item item = stack.getItem();
        if (item instanceof ArmorItem armor) {
            return fallback(item.getDefaultAttributeModifiers(armor.getEquipmentSlot()), Attributes.ARMOR, 2.5);
        }
        if (item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem) {
            return fallback(item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND), Attributes.ATTACK_DAMAGE, 2);
        }
        if (item instanceof BowItem || item instanceof ShieldItem) return new EquipmentProfile(12, List.of());
        return EMPTY;
    }

    private static EquipmentProfile fallback(Multimap<Attribute, AttributeModifier> modifiers, Attribute attribute, double scale) {
        if (modifiers.containsKey(LHMiracleRoadAttributes.BURDEN)) return EMPTY;
        double heavy = 0;
        for (AttributeModifier modifier : modifiers.get(attribute)) heavy = Math.max(0, modifier.getAmount() * scale);
        List<Requirement> requirements = attribute == Attributes.ATTACK_DAMAGE && heavy > 0
                ? List.of(new Requirement("power", Math.min((int) (heavy * 1.25), 80), ResourceLocationTool.DEFAULT))
                : List.of();
        return new EquipmentProfile(heavy, requirements);
    }

    public static void refreshEquipmentWeights(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(LHMiracleRoadAttributes.HEAVY);
        if (attribute == null) return;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            UUID id = modifierId("weight/" + slot.getName());
            attribute.removeModifier(id);
            for (AttributeModifier modifier : player.getItemBySlot(slot).getAttributeModifiers(slot).get(LHMiracleRoadAttributes.HEAVY)) {
                if (id.equals(modifier.getId())) attribute.addTransientModifier(modifier);
            }
        }
    }

    public static void refreshCurioWeights(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(LHMiracleRoadAttributes.HEAVY);
        if (attribute == null) return;
        CuriosApi.getCuriosInventory(player).ifPresent(inventory ->
                inventory.getCurios().forEach((type, handler) -> {
                    var slots = handler.getStacks();
                    for (int index = 0; index < slots.getSlots(); index++) {
                        String slot = curioSlot(type, index);
                        attribute.removeModifier(modifierId("weight/" + slot));
                        double heavy = profile(slots.getStackInSlot(index), false).heavy();
                        if (heavy > 0 && equipmentWeightEnabled(false)) attribute.addTransientModifier(weightModifier(slot, heavy));
                    }
                }));
    }

    public static void refreshPenalties(ServerPlayer player, PlayerOccupationAttribute attributes) {
        Map<UUID, ActivePenalty> desired = new HashMap<>();
        if (attributePenaltiesEnabled(false)) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                collectPenalties(player.getItemBySlot(slot), attributes, slot.getName(), desired);
            }
            CuriosApi.getCuriosInventory(player).ifPresent(inventory ->
                    inventory.getCurios().forEach((type, handler) -> {
                        var slots = handler.getStacks();
                        for (int index = 0; index < slots.getSlots(); index++) {
                            collectPenalties(slots.getStackInSlot(index), attributes, curioSlot(type, index), desired);
                        }
                    }));
        }

        Map<UUID, Attribute> applied = attributes.getActiveEquipmentPenalties();
        applied.forEach((id, attribute) -> {
            if (desired.containsKey(id)) return;
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance != null) instance.removeModifier(id);
        });
        applied.clear();
        desired.forEach((id, penalty) -> {
            AttributeInstance instance = player.getAttribute(penalty.attribute());
            if (instance == null) return;
            applyTransient(instance, penalty.modifier());
            applied.put(id, penalty.attribute());
        });
        if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
    }

    private static void collectPenalties(ItemStack stack, PlayerOccupationAttribute attributes, String slot,
                                         Map<UUID, ActivePenalty> desired) {
        for (Requirement need : requirements(stack, false)) {
            if (need.isMet(attributes)) continue;
            var effects = PunishmentConfig.getPunishment(need.preset());
            if (effects == null || effects.isEmpty()) continue;
            for (JsonElement effect : effects) {
                if (!effect.isJsonObject()) continue;
                JsonObject rule = effect.getAsJsonObject();
                Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(LHMiracleRoadTool.isAsString(rule.get("attribute")));
                AttributeModifier.Operation operation = LHMiracleRoadTool.stringConversionOperation(
                        LHMiracleRoadTool.isAsString(rule.get("operation")));
                double value = LHMiracleRoadTool.isAsDouble(rule.get("value"));
                if (attribute == null || operation == null || !Double.isFinite(value) || value == 0) continue;
                UUID id = modifierId("penalty/" + slot + "/" + ForgeRegistries.ATTRIBUTES.getKey(attribute));
                desired.putIfAbsent(id, new ActivePenalty(attribute,
                        new AttributeModifier(id, "Equipment requirement", value, operation)));
            }
            // A slot uses only the first unmet requirement with a configured penalty.
            return;
        }
    }

    public static void updateLoad(ServerPlayer player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        if (!equipmentWeightEnabled(false)) {
            speed.removeModifier(LOAD_MODIFIER);
            return;
        }
        AttributeInstance heavy = player.getAttribute(LHMiracleRoadAttributes.HEAVY);
        AttributeInstance burden = player.getAttribute(LHMiracleRoadAttributes.BURDEN);
        if (heavy == null || burden == null) return;

        LHMiracleRoadConfig.Common config = LHMiracleRoadConfig.COMMON;
        double amount = switch (loadState(heavy.getValue(), burden.getValue())) {
            case "overweight" -> config.PUNISHMENT_OVERWEIGHT.get();
            case "biased_weight" -> config.PUNISHMENT_BIASED_WEIGHT.get();
            case "normal" -> config.PUNISHMENT_NORMAL.get();
            default -> config.PUNISHMENT_LIGHT.get();
        };
        if (amount == 0) speed.removeModifier(LOAD_MODIFIER);
        else applyTransient(speed, new AttributeModifier(LOAD_MODIFIER, "Equipment load", amount, AttributeModifier.Operation.MULTIPLY_BASE));
    }

    public static String loadState(double heavy, double burden) {
        if (heavy > burden) return "overweight";
        if (burden > 0 && heavy >= burden * 0.75) return "biased_weight";
        if (burden > 0 && heavy >= burden * 0.40) return "normal";
        return "light";
    }

    private static void applyTransient(AttributeInstance instance, AttributeModifier modifier) {
        AttributeModifier current = instance.getModifier(modifier.getId());
        if (current != null) {
            if (current.getAmount() == modifier.getAmount() && current.getOperation() == modifier.getOperation()) return;
            instance.removeModifier(current);
        }
        instance.addTransientModifier(modifier);
    }

    private static UUID modifierId(String key) {
        return UUID.nameUUIDFromBytes((LHMiracleRoad.MODID + "/equipment/" + key).getBytes(StandardCharsets.UTF_8));
    }

    public record Requirement(String attributeType, int minimumLevel, String preset) {
        public boolean isMet(PlayerOccupationAttribute attributes) {
            long baseLevel = attributes.getOccupationAttributeLevel().getOrDefault(attributeType, 0);
            String jobId = attributes.getOccupationId();
            if (jobId != null) {
                var job = EffectiveSide.get().isClient() ? ClientData.OCCUPATION_CATALOG.get(jobId)
                        : OccupationConfig.getOccupation(jobId);
                if (job != null && job.getOccupationAttributes() != null && job.getOccupationAttributes().stream()
                        .noneMatch(attribute -> attributeType.equals(attribute.getType_id()))) baseLevel = 0;
            }
            return baseLevel + attributes.getCurioAttributeLevelValue(attributeType) >= minimumLevel;
        }
    }

    private record EquipmentProfile(double heavy, List<Requirement> requirements) {}
    private record ActivePenalty(Attribute attribute, AttributeModifier modifier) {}
}
