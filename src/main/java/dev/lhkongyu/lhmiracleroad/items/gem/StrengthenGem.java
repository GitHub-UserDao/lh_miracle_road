package dev.lhkongyu.lhmiracleroad.items.gem;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.config.strengthen.StrengthenConfig;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class StrengthenGem {

    public static ItemStack strengthen(ItemStack baseItemStack){
        CompoundTag tag = baseItemStack.getOrCreateTag().getCompound("lh_gem");
        int strengthenLV = tag.getInt("strengthen_lv");

        ItemStack out = baseItemStack.copy();
        CompoundTag compoundTag = tag.copy();

        int vanillaMaxDurability = baseItemStack.getMaxDamage();
        int customMaxDurability = compoundTag.contains("max_durability")
                ? compoundTag.getInt("max_durability")
                : vanillaMaxDurability;

        int maxDurability = (int) (customMaxDurability * StrengthenGem.getStrengthenGemDurabilityRatio(strengthenLV + 1,baseItemStack));
        compoundTag.putDouble("max_durability", maxDurability);
        compoundTag.putInt("strengthen_lv", strengthenLV + 1);
        out.getOrCreateTag().put("lh_gem", compoundTag);
        return out;
    }

    public static Integer getStrengthenGemCount(ItemStack right,double strengthenLV){
        if (strengthenLV < 3 && right.getDescriptionId().equals(ItemsRegistry.METEORIC_IRON_FRAGMENT.get().getDescriptionId())){
            if (strengthenLV == 0) return 1;
            else if (strengthenLV == 1) return 3;
            else if (strengthenLV == 2) return 6;
            return null;
        }else if (strengthenLV < 6 && right.getDescriptionId().equals(ItemsRegistry.METEORIC_IRON_BIG_FRAGMENT.get().getDescriptionId())){
            if (strengthenLV == 3) return 1;
            else if (strengthenLV == 4) return 3;
            else if (strengthenLV == 5) return 5;
            return null;
        }else if (strengthenLV < 10 && right.getDescriptionId().equals(ItemsRegistry.METEORIC_IRON_BLOCK.get().getDescriptionId())){
            if (strengthenLV == 6) return 1;
            else if (strengthenLV == 7) return 2;
            else if (strengthenLV == 8) return 3;
            return null;
        }else if (strengthenLV == 9 && right.getDescriptionId().equals(ItemsRegistry.METEORITE_DISK.get().getDescriptionId())){
            return 1;
        }
        return null;
    }

    public static double getStrengthenGemDurabilityRatio(int strengthenLV,ItemStack stack){
        if (LHMiracleRoadTool.itemIsWeapons(stack))
            return StrengthenConfig.getWeapon(strengthenLV).getDurability_magnification();
        else if (LHMiracleRoadTool.itemIsRangedWeapons(stack))
            return StrengthenConfig.getRangedWeapon(strengthenLV).getDurability_magnification();
        else if (LHMiracleRoadTool.itemIsArmors(stack))
            return StrengthenConfig.getArmor(strengthenLV).getDurability_magnification();
        else if (LHMiracleRoadTool.itemIsTool(stack))
            return StrengthenConfig.getTool(strengthenLV).getDurability_magnification();
        else if (LHMiracleRoadTool.itemIsMagicStaff(stack))
            return StrengthenConfig.getMagicStaff(strengthenLV).getDurability_magnification();

        return 1;
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
                    ranged += AttributeGem.setAttackType(gemTag) * 0.1;
                }

                if (ranged > 0) event.addModifier(LHMiracleRoadAttributes.RANGED_DAMAGE,
                        new AttributeModifier(rangedUUID, "lh_gem_ranged", ranged, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
    }

    /**
     * 宝石强化 工具，添加强化后的属性
     * @param strengthenLV
     * @param event
     */
    public static void setToolsAttribute(int strengthenLV, ItemAttributeModifierEvent event,CompoundTag gemTag){
        UUID miningSpeedUUID = UUID.fromString("2a48a663-f409-443f-9441-f7ff21e399a8");
        if (event.getSlotType() == EquipmentSlot.MAINHAND) {
            if (strengthenLV > 0 || gemTag.contains("type")){
                double miningSpeed = getMiningSpeed(strengthenLV);

                if (miningSpeed > 0) event.addModifier(LHMiracleRoadAttributes.MINING_SPEED,
                        new AttributeModifier(miningSpeedUUID, "lh_gem_mining_speed", miningSpeed, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
    }

    /**
     * 宝石强化 法杖，添加强化后的属性
     * @param strengthenLV
     * @param event
     */
    public static void setMagicStaffAttribute(int strengthenLV, ItemAttributeModifierEvent event,CompoundTag gemTag){
        UUID magicDamageUUID = UUID.fromString("2a11e4ed-5262-49e3-9dd9-584d2efe881f");
        if (event.getSlotType() == EquipmentSlot.MAINHAND) {
            if (strengthenLV > 0 || gemTag.contains("type")){
                double magicDamage = getMagicDamage(strengthenLV);
                if (magicDamage <= 0)return;
                if (LHMiracleRoadTool.isModExist("irons_spellbooks")) {
                    String attributeName = "irons_spellbooks:spell_power";
                    ResourceLocation resourceLocation = ForgeRegistries.ATTRIBUTES.getKeys()
                            .stream()
                            .filter(p -> attributeName.equals(p.toString()))
                            .findFirst()
                            .orElse(null);

                    Attribute instanceAttribute = ForgeRegistries.ATTRIBUTES.getValue(resourceLocation);
                    if (instanceAttribute != null) {
                        event.addModifier(instanceAttribute,
                                new AttributeModifier(magicDamageUUID, "lh_gem_magic_staff", magicDamage, AttributeModifier.Operation.MULTIPLY_TOTAL));
                    }
                }
            }
        }
    }

    public static double getAttack(int strengthenLV){
        double attack = 0;
        for (int i = strengthenLV; i > 0; i--) {
            attack += StrengthenConfig.getWeapon(i).getAttack();
        }
        return attack;
    }

    public static double getAttackSpeed(int strengthenLV){
        double attack_speed = 0;
        for (int i = strengthenLV; i > 0; i--) {
            attack_speed += StrengthenConfig.getWeapon(i).getAttack_speed();
        }
        return attack_speed;
    }

    public static double getRanged(int strengthenLV){
        double ranged = 0;
        for (int i = strengthenLV; i > 0; i--) {
            ranged += StrengthenConfig.getRangedWeapon(i).getRanged_attack();
        }
        return ranged;
    }

    public static double getArmor(int strengthenLV){
        double armor = 0;
        for (int i = strengthenLV; i > 0; i--) {
            armor += StrengthenConfig.getArmor(i).getArmor();
        }
        return armor;
    }

    public static double getArmorToughness(int strengthenLV){
        double armor_toughness = 0;
        for (int i = strengthenLV; i > 0; i--) {
            armor_toughness += StrengthenConfig.getArmor(i).getArmor_toughness();
        }
        return armor_toughness;
    }

    public static double getMiningSpeed(int strengthenLV){
        double mining_speed = 0;
        for (int i = strengthenLV; i > 0; i--) {
            mining_speed += StrengthenConfig.getTool(i).getMining_speed();
        }
        return mining_speed;
    }

    public static double getMagicDamage(int strengthenLV){
        double magic_damage = 0;
        for (int i = strengthenLV; i > 0; i--) {
            magic_damage += StrengthenConfig.getMagicStaff(i).getMagic_damage();
        }
        return magic_damage;
    }

}
