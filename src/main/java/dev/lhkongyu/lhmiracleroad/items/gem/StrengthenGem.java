package dev.lhkongyu.lhmiracleroad.items.gem;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.attributes.ShowAttributesTypes;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.event.InteractionEvent;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StrengthenGem {

    public static Integer getStrengthenGemCount(ItemStack right,double strengthenLV){
        if (strengthenLV < 3 && right.getDescriptionId().equals(ItemsRegistry.METEORIC_IRON_FRAGMENT.get().getDescriptionId())){
            if (strengthenLV == 0) return 1;
            else if (strengthenLV == 1) return 3;
            else if (strengthenLV == 2) return 9;
            return null;
        }else if (strengthenLV < 6 && right.getDescriptionId().equals(ItemsRegistry.METEORIC_IRON_BIG_FRAGMENT.get().getDescriptionId())){
            if (strengthenLV == 3) return 3;
            else if (strengthenLV == 4) return 6;
            else if (strengthenLV == 5) return 9;
            return null;
        }else if (strengthenLV < 10 && right.getDescriptionId().equals(ItemsRegistry.METEORIC_IRON_BLOCK.get().getDescriptionId())){
            if (strengthenLV == 6) return 3;
            else if (strengthenLV == 7) return 6;
            else if (strengthenLV == 8) return 9;
            return null;
        }else if (strengthenLV == 9 && right.getDescriptionId().equals(ItemsRegistry.METEORITE_DISK.get().getDescriptionId())){
            return 1;
        }
        return null;
    }

    public static String getStrengthenGemTooltip(double strengthenLV){
        int needed = 1;
        if (strengthenLV == 1) needed = 3;
        else if (strengthenLV == 2) needed = 9;
        else if (strengthenLV == 3) needed = 3;
        else if (strengthenLV == 4) needed = 6;
        else if (strengthenLV == 5) needed = 9;
        else if (strengthenLV == 6) needed = 3;
        else if (strengthenLV == 7) needed = 6;
        else if (strengthenLV == 8) needed = 9;

        String itemName = getItemName(strengthenLV);

        return  Component.translatable("tooltip.lhmiracleroad.gem.deficiency",needed,itemName).getString();

    }

    @NotNull
    private static String getItemName(double strengthenLV) {
        String itemName = "";

        if (strengthenLV < 3){
            itemName = Component.translatable("item.lhmiracleroad.meteoric_iron_fragment").getString();
        }else if (strengthenLV < 6){
            itemName = Component.translatable("item.lhmiracleroad.meteoric_iron_big_fragment").getString();
        }else if (strengthenLV < 9){;
            itemName = Component.translatable("item.lhmiracleroad.meteoric_iron_block").getString();
        }else if (strengthenLV == 9){
            itemName = Component.translatable("item.lhmiracleroad.meteorite_disk").getString();
        }
        return itemName;
    }

    public static double getStrengthenGemDurabilityRatio(int strengthenLV){
        return getDurability(strengthenLV);
    }


    /**
     * 宝石强化 盔甲，添加强化后的属性
     * @param strengthenLV
     * @param event
     */
    public static void setGemStrengthenArmorAttribute(int strengthenLV, ItemAttributeModifierEvent event){
        UUID armorUUID = UUID.fromString("ac8ba514-3e7a-16f8-aa3c-1e50891b11c3");
        UUID toughnessUUID = UUID.fromString("f7bd3e16-9049-7c21-8abb-b6acd68d4301");

        if (strengthenLV > 0){
            double bonusArmor = getArmor(strengthenLV);
            double bonusToughness = getArmorToughness(strengthenLV);
            event.addModifier(Attributes.ARMOR,
                    new AttributeModifier(armorUUID, "lh_gem_armor", bonusArmor, AttributeModifier.Operation.ADDITION));

            event.addModifier(Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(toughnessUUID, "lh_gem_toughness", bonusToughness, AttributeModifier.Operation.ADDITION));
        }
    }

    /**
     * 宝石强化 近战武器，添加强化后的属性
     * @param strengthenLV
     * @param event
     */
    public static void setWeaponsAttribute(int strengthenLV, ItemAttributeModifierEvent event, CompoundTag gemTag){
        UUID attackUUID = UUID.fromString("429fb7b6-6906-5681-241e-58e2adfefd46");
        UUID speedUUID = UUID.fromString("ff91bd98-de53-82e8-590d-7434b81a8dd4");
        if (event.getSlotType() == EquipmentSlot.MAINHAND) {
            if (strengthenLV > 0 || gemTag.contains("type")){
                double bonusAttack = getAttack(strengthenLV);
                if (gemTag.contains("type")) {
                    bonusAttack = bonusAttack - (bonusAttack * AttributeGem.setAttributeType(gemTag));
                    bonusAttack += AttributeGem.setAttackType(gemTag);
                }

                double bonusSpeed = getAttackSpeed(strengthenLV);

                if (bonusAttack > 0) event.addModifier(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(attackUUID, "lh_gem_attack", bonusAttack, AttributeModifier.Operation.ADDITION));

                if (bonusSpeed > 0) event.addModifier(Attributes.ATTACK_SPEED,
                        new AttributeModifier(speedUUID, "lh_gem_attack_speed", bonusSpeed, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    /**
     * 宝石强化 远程武器，添加强化后的属性
     * @param strengthenLV
     * @param event
     */
    public static void setRangedWeaponsAttribute(int strengthenLV, ItemAttributeModifierEvent event,CompoundTag gemTag){
        UUID rangedUUID = UUID.fromString("bf010eff-c4fd-f908-e3ba-d96e7a0f27b5");
        if (event.getSlotType() == EquipmentSlot.MAINHAND) {
            if (strengthenLV > 0 || gemTag.contains("type")){
                double ranged = getRanged(strengthenLV);
                if (gemTag.contains("type")) {
                    ranged = ranged - (ranged * AttributeGem.setAttributeType(gemTag));
                    ranged += AttributeGem.setAttackType(gemTag) * 0.1;
                }

                if (ranged > 0) event.addModifier(LHMiracleRoadAttributes.RANGED_DAMAGE,
                        new AttributeModifier(rangedUUID, "lh_gem_ranged", ranged, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
    }

    public static double getAttack(int strengthenLV){
        double attack = 0;
        for (int i = strengthenLV; i > 0; i--) {
            attack += LHMiracleRoadTool.isAsDouble(lvGetConfigGemstoneStrengthenJson(i).get("attack"));
        }
        return attack;
    }

    public static double getAttackSpeed(int strengthenLV){
        double attack_speed = 0;
        for (int i = strengthenLV; i > 0; i--) {
            attack_speed += LHMiracleRoadTool.isAsDouble(lvGetConfigGemstoneStrengthenJson(i).get("attack_speed"));
        }
        return attack_speed;
    }

    public static double getRanged(int strengthenLV){
        double ranged = 0;
        for (int i = strengthenLV; i > 0; i--) {
            ranged += LHMiracleRoadTool.isAsDouble(lvGetConfigGemstoneStrengthenJson(i).get("ranged"));
        }
        return ranged;
    }

    public static double getArmor(int strengthenLV){
        double armor = 0;
        for (int i = strengthenLV; i > 0; i--) {
            armor += LHMiracleRoadTool.isAsDouble(lvGetConfigGemstoneStrengthenJson(i).get("armor"));
        }
        return armor;
    }

    public static double getArmorToughness(int strengthenLV){
        double armor_toughness = 0;
        for (int i = strengthenLV; i > 0; i--) {
            armor_toughness += LHMiracleRoadTool.isAsDouble(lvGetConfigGemstoneStrengthenJson(i).get("armor_toughness"));
        }
        return armor_toughness;
    }

    public static double getDurability(int strengthenLV){
        return LHMiracleRoadTool.isAsDouble(lvGetConfigGemstoneStrengthenJson(strengthenLV).get("durability"));
    }



    /**
     * 通过lv获取 config配置里的 每级强化配置
     * @param strengthenLV
     * @return
     */
    private static JsonObject lvGetConfigGemstoneStrengthenJson(int strengthenLV){
        return switch (strengthenLV){
            case 1 -> JsonParser.parseString(LHMiracleRoadConfig.COMMON.GEMSTONE_STRENGTHEN_ONE.get()).getAsJsonObject();
            case 2 -> JsonParser.parseString(LHMiracleRoadConfig.COMMON.GEMSTONE_STRENGTHEN_TWO.get()).getAsJsonObject();
            case 3 -> JsonParser.parseString(LHMiracleRoadConfig.COMMON.GEMSTONE_STRENGTHEN_THREE.get()).getAsJsonObject();
            case 4 -> JsonParser.parseString(LHMiracleRoadConfig.COMMON.GEMSTONE_STRENGTHEN_FOUR.get()).getAsJsonObject();
            case 5 -> JsonParser.parseString(LHMiracleRoadConfig.COMMON.GEMSTONE_STRENGTHEN_FIVE.get()).getAsJsonObject();
            case 6 -> JsonParser.parseString(LHMiracleRoadConfig.COMMON.GEMSTONE_STRENGTHEN_SIX.get()).getAsJsonObject();
            case 7 -> JsonParser.parseString(LHMiracleRoadConfig.COMMON.GEMSTONE_STRENGTHEN_SEVEN.get()).getAsJsonObject();
            case 8 -> JsonParser.parseString(LHMiracleRoadConfig.COMMON.GEMSTONE_STRENGTHEN_EIGHT.get()).getAsJsonObject();
            case 9 -> JsonParser.parseString(LHMiracleRoadConfig.COMMON.GEMSTONE_STRENGTHEN_NINE.get()).getAsJsonObject();
            case 10 -> JsonParser.parseString(LHMiracleRoadConfig.COMMON.GEMSTONE_STRENGTHEN_TEN.get()).getAsJsonObject();
            default -> new JsonObject();
        };
    }

}
