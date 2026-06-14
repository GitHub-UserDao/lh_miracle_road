package dev.lhkongyu.lhmiracleroad.event;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.attributes.AttributeInstanceAccess;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.ItemStackPunishmentAttribute;
import dev.lhkongyu.lhmiracleroad.capability.ItemStackPunishmentAttributeProvider;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import dev.lhkongyu.lhmiracleroad.data.reloader.EquipmentReloadListener;
import dev.lhkongyu.lhmiracleroad.items.gem.AttributeGem;
import dev.lhkongyu.lhmiracleroad.items.gem.StrengthenGem;
import dev.lhkongyu.lhmiracleroad.tool.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemEvent {

    /**
     * 鼠标移到物品显示文本事件
     * @param event
     */
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (!LHMiracleRoadConfig.COMMON.IS_SKILL_POINTS_RESTRICT.get()) return;
        Player player = event.getEntity();
        if(player == null) return;
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
        if (optionalPlayerOccupationAttribute.isEmpty()) return;
        PlayerOccupationAttribute playerOccupationAttribute = optionalPlayerOccupationAttribute.get();

        stack.getCapability(ItemStackPunishmentAttributeProvider.ITEM_STACK_PUNISHMENT_ATTRIBUTE_PROVIDER).ifPresent(itemStackPunishmentAttribute -> {
           JsonArray attributeNeed = itemStackPunishmentAttribute.getAttributeNeed();
           if (attributeNeed == null || attributeNeed.isEmpty()) return;
           for (JsonElement jsonElement : attributeNeed){
               JsonObject jsonObject = LHMiracleRoadTool.isAsJsonObject(jsonElement);
               if (jsonObject == null) continue;
               String attributeId = LHMiracleRoadTool.isAsString(jsonObject.get("attribute_id"));
               String describeText = ResourceLocationTool.ATTRIBUTE_TOOLTIP_DETAILS_PREFIX + attributeId;
               int needPoints = LHMiracleRoadTool.isAsInt(jsonObject.get("need_points"));
               Map<String, Integer> occupationAttributeLevel = playerOccupationAttribute.getOccupationAttributeLevel();
               Integer attributeLevel = 0;
               for (String key : occupationAttributeLevel.keySet()){
                   if (attributeId.equals(key)) {
                       attributeLevel = occupationAttributeLevel.get(key);
                       attributeLevel += playerOccupationAttribute.getCurioAttributeLevelValue(key);
                   }
               }
               if (attributeLevel < needPoints) {
                   tooltip.add(Component.translatable(describeText, needPoints).withStyle(ChatFormatting.RED));
               }else {
                   tooltip.add(Component.translatable(describeText, needPoints).withStyle(ChatFormatting.GREEN));
               }
           }
        });

        updateShowName(event,stack);
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

//    /**
//     * 删除原版转换属性Tooltip
//     */
//    private static void showConvertTooltip(ItemStack stack,Player player,ItemTooltipEvent event,List<Component> tooltip){
//        CompoundTag gemTag = stack.getTagElement("lh_gem");
//        if (gemTag == null) return;
//
//        Multimap<Attribute, AttributeModifier> modifiers =
//                stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
//
//        double convert = 0;
//        double convertDamage = 0;
//        String value;
//
//        AttributeInstance attack = player.getAttribute(Attributes.ATTACK_DAMAGE);
//        var attackAccess = ((AttributeInstanceAccess) attack);
//
//        if (attackAccess == null)  return;
//        double attackAmount = (float) attackAccess.computeIncreasedValueForInitial(attack.getBaseValue() > 0 ? 0 : 1);
//        attackAmount -= attack.getBaseValue() > 0 ? 0 : 1;
//
//        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
//
//            Attribute attribute = entry.getKey();
//            AttributeModifier modifier = entry.getValue();
//
//            if (LHMiracleRoadTool.isMagicAttributes(attribute)) {
//                convert += modifier.getAmount();
//                double convertValue = modifier.getAmount();
//                // 没有转换属性
//                if (convert <= 0)
//                    continue;
//
//                // 删除原版Tooltip
//                removeVanillaConvertTooltip(event.getToolTip(),attribute.getDescriptionId());
//
//                // 显示的 魔法转换值
//                convertDamage = attackAmount * convertValue;
//                value =
//                        String.format("%.2f", convertDamage);
////                tooltip.add(Component.empty());
//                tooltip.add(
//                        Component.literal("+" + value + Component.translatable(attribute.getDescriptionId()))
//                                .withStyle(ChatFormatting.BLUE)
//                );
//
//            }
//        }
//
//        if (convertDamage > 0) {
//            // 显示的 攻击力
//            convertDamage = attackAmount * convert;
//            value =
//                    String.format("%.2f", Math.min(convertDamage, attackAmount));
//            tooltip.add(
//                    Component.literal("-" + value + " 攻击力")
//                            .withStyle(ChatFormatting.RED)
//            );
//        }
//    }
//
//    /**
//     * 删除原版转换属性Tooltip
//     */
//    private static void removeVanillaConvertTooltip(
//            List<Component> tooltip,
//            String id
//    ) {
//
//        tooltip.removeIf(component -> {
//
//            if (!(component.getContents() instanceof TranslatableContents contents))
//                return false;
//            Object[] args = contents.getArgs();
//            if (args.length > 1) {
//                Object attributeObj = args[1];
//                if (attributeObj instanceof Component attributeComponent) {
//                    if (attributeComponent.getContents() instanceof TranslatableContents attributeContents) {
//                        // 获取真正的 translation key
//                        String attributeKey = attributeContents.getKey();
//                        return attributeKey.equals(id);
//                    }
//                }
//            }
//            return false;
//        });
//    }

    /**
     * 物品能力注册事件
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void setUpItemStackCapabilitiesEvent(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject() != null) {
            ItemStack stack = event.getObject();
            ItemStackPunishmentAttribute itemStackPunishmentAttribute = new ItemStackPunishmentAttribute();
            Item item = stack.getItem();
            JsonObject equipment = LHMiracleRoadTool.getEquipment(EquipmentReloadListener.EQUIPMENT,item.getDescriptionId());
            equipment = equipment != null ? equipment : LHMiracleRoadTool.getEquipment(ClientData.EQUIPMENT,item.getDescriptionId());
            if (equipment != null){
                int heavy = LHMiracleRoadTool.isAsInt(equipment.get(NameTool.HEAVY));
                JsonArray attributeNeed = LHMiracleRoadTool.isAsJsonArray(equipment.get("attribute_need"));
                itemStackPunishmentAttribute.setHeavy(heavy);
                ItemPunishmentTool.setHeavyAttributeModifier(itemStackPunishmentAttribute,attributeNeed);
            }else {
                //  stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem || stack.getItem() instanceof PickaxeItem || stack.getItem() instanceof ShovelItem || stack.getItem() instanceof HoeItem
                if (item instanceof SwordItem swordItem) {
                    Multimap<Attribute, AttributeModifier> modifierMultimap = swordItem.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
                    ItemPunishmentTool.injectionItemStackPunishmentAttribute(modifierMultimap, itemStackPunishmentAttribute, Attributes.ATTACK_DAMAGE,2);
                } else if (item instanceof ArmorItem armorItem) {
                    Multimap<Attribute, AttributeModifier> modifierMultimap = armorItem.getDefaultAttributeModifiers(armorItem.getType().getSlot());
                    ItemPunishmentTool.injectionItemStackPunishmentAttribute(modifierMultimap, itemStackPunishmentAttribute, Attributes.ARMOR,2.5);
                } else if (item instanceof AxeItem axeItem) {
                    Multimap<Attribute, AttributeModifier> modifierMultimap = axeItem.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
                    ItemPunishmentTool.injectionItemStackPunishmentAttribute(modifierMultimap, itemStackPunishmentAttribute, Attributes.ATTACK_DAMAGE,2);
                } else if (item instanceof BowItem || item instanceof ShieldItem) {
                    itemStackPunishmentAttribute.setHeavy(12);
                    ItemPunishmentTool.setHeavyAttributeModifier(itemStackPunishmentAttribute,null);
                } else if (item instanceof TridentItem tridentItem) {
                    Multimap<Attribute, AttributeModifier> modifierMultimap = tridentItem.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
                    ItemPunishmentTool.injectionItemStackPunishmentAttribute(modifierMultimap, itemStackPunishmentAttribute, Attributes.ATTACK_DAMAGE,2);
                }
            }

            if (equipment != null || item instanceof SwordItem || item instanceof ArmorItem || item instanceof AxeItem ||
                    item instanceof BowItem || item instanceof ShieldItem || item instanceof TridentItem){
                event.addCapability(new ResourceLocation(LHMiracleRoad.MODID, "punishment_cap"), new ItemStackPunishmentAttributeProvider(itemStackPunishmentAttribute));
            }
        }
    }

    /**
     * 物品属性注册事件
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void itemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        CompoundTag gemTag = stack.getTagElement("lh_gem");
        if (item instanceof ArmorItem armorItem) {
            if (armorItem.getType().getSlot() != event.getSlotType()) return;
            //武器附加重量
            ItemPunishmentTool.itemStackAddPunishmentAttribute(stack, event);

            //宝石强化 盔甲
            if (gemTag != null) {
                int strengthenLV = gemTag.getInt("strengthen_lv");
                StrengthenGem.setGemStrengthenArmorAttribute(strengthenLV,event);
            }
        } else if (event.getSlotType() == EquipmentSlot.MAINHAND){
            //武器附加重量
            ItemPunishmentTool.itemStackAddPunishmentAttribute(stack, event);

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

            //武器质变
            if (gemTag != null) {
                AttributeGem.setAttributeStrengthen(gemTag, event);
            }


        }else if (item instanceof ElytraItem elytraItem && gemTag != null){
            if (elytraItem.getEquipmentSlot() != event.getSlotType()) return;
            //宝石强化 鞘翅
            int strengthenLV = gemTag.getInt("strengthen_lv");
            StrengthenGem.setGemStrengthenArmorAttribute(strengthenLV,event);
        }
    }

    /**
     * 装备切换事件
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EquipmentSlot slot = event.getSlot();
            //获取变化前和变化后的装备
            ItemStack itemFrom = event.getFrom();
            ItemStack itemTo = event.getTo();

            //获取玩家能力和物品能力
            Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();

            if (optionalPlayerOccupationAttribute.isEmpty()) return;

            PlayerOccupationAttribute playerOccupationAttribute = optionalPlayerOccupationAttribute.get();

            Optional<ItemStackPunishmentAttribute> itemFromPunishmentAttribute = itemFrom
                    .getCapability(ItemStackPunishmentAttributeProvider.ITEM_STACK_PUNISHMENT_ATTRIBUTE_PROVIDER)
                    .resolve();
            Optional<ItemStackPunishmentAttribute> itemToPunishmentAttribute = itemTo
                    .getCapability(ItemStackPunishmentAttributeProvider.ITEM_STACK_PUNISHMENT_ATTRIBUTE_PROVIDER)
                    .resolve();

            //沉重值计算
            ItemPunishmentTool.heavyPunishmentAttributeModifier(itemFrom,itemTo,itemFromPunishmentAttribute,itemToPunishmentAttribute,player,playerOccupationAttribute,slot);

            //清除前物品所设置的惩罚
            if (itemFromPunishmentAttribute.isPresent() && !itemFrom.isEmpty()){
                ItemPunishmentTool.cleanItemFromPunishmentAttributeModifier(player, playerOccupationAttribute, itemFromPunishmentAttribute.get());
            }

            //设置切换后的惩罚
            if (itemToPunishmentAttribute.isPresent() && !itemTo.isEmpty()) {
                ItemPunishmentTool.cleanItemFromPunishmentAttributeModifier(player, playerOccupationAttribute, itemToPunishmentAttribute.get());
                ItemPunishmentTool.setItemToPunishmentAttributeModifier(player,playerOccupationAttribute,itemToPunishmentAttribute.get());
            }
        }
    }

}
