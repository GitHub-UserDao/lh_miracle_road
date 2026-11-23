package dev.lhkongyu.lhmiracleroad.event;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.items.gem.AttributeGem;
import dev.lhkongyu.lhmiracleroad.items.gem.StrengthenGem;
import dev.lhkongyu.lhmiracleroad.tool.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.SlotModifiersUpdatedEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemEvent {

    /**
     * 鼠标移到物品显示文本事件
     * @param event
     */
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        if (player != null && ItemPunishmentTool.attributePenaltiesEnabled(player.level().isClientSide)) {
            player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(attributes -> {
                for (ItemPunishmentTool.Requirement need : ItemPunishmentTool.requirements(stack, player.level().isClientSide)) {
                    String key = ResourceLocationTool.ATTRIBUTE_TOOLTIP_DETAILS_PREFIX + need.attributeType();
                    event.getToolTip().add(Component.translatable(key, need.minimumLevel())
                            .withStyle(need.isMet(attributes) ? ChatFormatting.GREEN : ChatFormatting.RED));
                }
            });
        }
        updateShowName(event, stack);
    }

    private static void updateShowName(ItemTooltipEvent event,ItemStack stack){
        CompoundTag tag = stack.getTagElement("lh_gem");
        if (tag == null) return;
        int strengthenLV = tag.getInt("strengthen_lv");
        String gemType = tag.getString("type");
        if (strengthenLV <= 0 && gemType.isEmpty()) return;

        List<Component> tooltip = event.getToolTip();
        Component nameComponent = tooltip.get(0);
        String name = nameComponent.getString();
        ChatFormatting color =
                strengthenLV >= 10 ? ChatFormatting.GOLD :
                        strengthenLV >= 7 ? ChatFormatting.LIGHT_PURPLE :
                        strengthenLV >= 4 ? ChatFormatting.AQUA :
                        ChatFormatting.GREEN;
        MutableComponent newName = Component.literal(Component.translatable(name).getString() + GemTool.getGemName(gemType,strengthenLV))
                .withStyle(color);

        tooltip.set(0, newName);
    }

    /**
     * 物品属性注册事件
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void itemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        CompoundTag gemTag = stack.getTagElement("lh_gem");
        if (LHMiracleRoadTool.itemIsArmors(stack)) {
            if (stack.getItem() instanceof ArmorItem armorItem) if (armorItem.getType().getSlot() != event.getSlotType()) return;
            else if (stack.getItem() instanceof ElytraItem elytraItem) if (elytraItem.getEquipmentSlot() != event.getSlotType()) return;
            //宝石强化 盔甲
            if (gemTag != null) {
                int strengthenLV = gemTag.getInt("strengthen_lv");
                StrengthenGem.setGemStrengthenArmorAttribute(strengthenLV,event);
            }
        } else if (event.getSlotType() == EquipmentSlot.MAINHAND){
            //宝石强化 近战武器
            if (gemTag != null && LHMiracleRoadTool.itemIsWeapons(stack)) {
                int strengthenLV = gemTag.getInt("strengthen_lv");
                StrengthenGem.setWeaponsAttribute(strengthenLV,event,gemTag);
            }

            //宝石强化 远程武器
            if (gemTag != null && LHMiracleRoadTool.itemIsRangedWeapons(stack)) {
                int strengthenLV = gemTag.getInt("strengthen_lv");
                StrengthenGem.setRangedWeaponsAttribute(strengthenLV,event,gemTag);
            }

            //宝石强化 工具
            if (gemTag != null && LHMiracleRoadTool.itemIsTool(stack)) {
                int strengthenLV = gemTag.getInt("strengthen_lv");
                StrengthenGem.setToolsAttribute(strengthenLV,event,gemTag);
            }

            //宝石强化 工具
            if (gemTag != null && LHMiracleRoadTool.itemIsMagicStaff(stack)) {
                int strengthenLV = gemTag.getInt("strengthen_lv");
                StrengthenGem.setMagicStaffAttribute(strengthenLV,event,gemTag);
            }

            //武器质变
            if (gemTag != null) {
                AttributeGem.setAttributeStrengthen(gemTag, event);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onWeightModifiers(ItemAttributeModifierEvent event) {
        ItemPunishmentTool.addWeight(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onCurioWeightModifiers(CurioAttributeModifierEvent event) {
        ItemPunishmentTool.addCurioWeight(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        refreshEquipmentPenalties(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCurioChange(CurioChangeEvent event) {
        queueCurioPenaltyRefresh(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCurioSlotCountChange(SlotModifiersUpdatedEvent event) {
        queueCurioPenaltyRefresh(event.getEntity());
    }

    private static void queueCurioPenaltyRefresh(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER)
                    .ifPresent(attributes -> attributes.queueCurioPenaltyRefresh());
        }
    }

    private static void refreshEquipmentPenalties(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER)
                    .ifPresent(attributes -> ItemPunishmentTool.refreshPenalties(player, attributes));
        }
    }

    @SubscribeEvent
    public static void onLoadTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
            // Curios fires its change event before applying the new item's equip effects.
            player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(attributes -> {
                if (attributes.consumeCurioPenaltyRefresh()) ItemPunishmentTool.refreshPenalties(player, attributes);
            });
            ItemPunishmentTool.updateLoad(player);
        }
    }
}
