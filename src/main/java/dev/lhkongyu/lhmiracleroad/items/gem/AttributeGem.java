package dev.lhkongyu.lhmiracleroad.items.gem;

import dev.lhkongyu.lhmiracleroad.attributes.AttributeInstanceAccess;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypeTagGenerator;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypes;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.NameTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.UUID;

public class AttributeGem {

    public static void setAttributeStrengthen(CompoundTag gemTag,ItemAttributeModifierEvent event){
        String type = gemTag.getString("type");
        if (type.isEmpty()) return;
        UUID attributeUUID = UUID.fromString("4f45d87b-87c7-a2e3-8a92-7647175709c8");
        UUID convertUUID = UUID.fromString("9f80734f-52b2-d5af-8b40-34223e482b12");
        double bonusAttack = StrengthenGem.getAttack(gemTag.getInt("strengthen_lv"));
        bonusAttack = bonusAttack * setAttributeType(gemTag);
        switch (type){
            case NameTool.FLAME:
                if (bonusAttack > 0) event.addModifier(LHMiracleRoadAttributes.FLAME_ATTRIBUTE_DAMAGE,
                        new AttributeModifier(attributeUUID, "lh_gem_" + NameTool.FLAME_ATTRIBUTE_DAMAGE, bonusAttack, AttributeModifier.Operation.ADDITION));

                event.addModifier(LHMiracleRoadAttributes.ATTACK_CONVERT_FLAME,
                        new AttributeModifier(convertUUID, "lh_gem_" + NameTool.ATTACK_CONVERT_FLAME, 0.25, AttributeModifier.Operation.MULTIPLY_BASE));
                break;
            case NameTool.LIGHTNING:
                if (bonusAttack > 0) event.addModifier(LHMiracleRoadAttributes.LIGHTNING_ATTRIBUTE_DAMAGE,
                        new AttributeModifier(attributeUUID, "lh_gem_" + NameTool.LIGHTNING_ATTRIBUTE_DAMAGE, bonusAttack, AttributeModifier.Operation.ADDITION));
                event.addModifier(LHMiracleRoadAttributes.ATTACK_CONVERT_LIGHTNING,
                        new AttributeModifier(convertUUID, "lh_gem_" + NameTool.ATTACK_CONVERT_LIGHTNING, 0.25, AttributeModifier.Operation.MULTIPLY_BASE));
                break;
            case NameTool.DARK:
                if (bonusAttack > 0) event.addModifier(LHMiracleRoadAttributes.DARK_ATTRIBUTE_DAMAGE,
                        new AttributeModifier(attributeUUID,  "lh_gem_" + NameTool.DARK_ATTRIBUTE_DAMAGE, bonusAttack, AttributeModifier.Operation.ADDITION));
                event.addModifier(LHMiracleRoadAttributes.ATTACK_CONVERT_DARK,
                        new AttributeModifier(convertUUID,  "lh_gem_" + NameTool.ATTACK_CONVERT_DARK, 0.25, AttributeModifier.Operation.MULTIPLY_BASE));
                break;
            case NameTool.BLOOD:
                break;
            case NameTool.MAGIC:
                if (bonusAttack > 0) event.addModifier(LHMiracleRoadAttributes.MAGIC_ATTRIBUTE_DAMAGE,
                        new AttributeModifier(attributeUUID,  "lh_gem_" + NameTool.MAGIC, bonusAttack, AttributeModifier.Operation.ADDITION));

                event.addModifier(LHMiracleRoadAttributes.ATTACK_CONVERT_MAGIC,
                        new AttributeModifier(convertUUID,  "lh_gem_" + NameTool.ATTACK_CONVERT_MAGIC, 0.3, AttributeModifier.Operation.MULTIPLY_BASE));
                break;
            case NameTool.SHARP:
                break;
            case NameTool.HOLY:
                if (bonusAttack > 0) event.addModifier(LHMiracleRoadAttributes.HOLY_ATTRIBUTE_DAMAGE,
                        new AttributeModifier(attributeUUID,  "lh_gem_" + NameTool.HOLY_ATTRIBUTE_DAMAGE, bonusAttack, AttributeModifier.Operation.ADDITION));
                event.addModifier(LHMiracleRoadAttributes.ATTACK_CONVERT_HOLY,
                        new AttributeModifier(convertUUID,  "lh_gem_" + NameTool.ATTACK_CONVERT_HOLY, 0.25, AttributeModifier.Operation.MULTIPLY_BASE));
                break;
            case NameTool.POISON:
                break;
            case NameTool.ICE:
                if (bonusAttack > 0) event.addModifier(LHMiracleRoadAttributes.MAGIC_ATTRIBUTE_DAMAGE,
                        new AttributeModifier(attributeUUID,  "lh_gem_" + NameTool.ICE, bonusAttack, AttributeModifier.Operation.ADDITION));

                event.addModifier(LHMiracleRoadAttributes.ATTACK_CONVERT_MAGIC,
                        new AttributeModifier(convertUUID,  "lh_gem_ice_" + NameTool.ATTACK_CONVERT_MAGIC, 0.25, AttributeModifier.Operation.MULTIPLY_BASE));
                break;
        }
    }

    public static double setAttributeType(CompoundTag gemTag){
        String type = gemTag.getString("type");
        return switch (type) {
            case NameTool.FLAME, NameTool.LIGHTNING, NameTool.DARK, NameTool.MAGIC, NameTool.HOLY, NameTool.ICE -> 0.5;
            default -> 0;
        };
    }

    public static double setAttackType(CompoundTag gemTag){
        String type = gemTag.getString("type");
        return switch (type) {
            case NameTool.SHARP -> 2;
            case NameTool.POISON, NameTool.BLOOD -> 1;
            default -> 0;
        };
    }

    public static float getAttributeDamage(float amount, ServerPlayer player, LivingEntity hurtEvent){
        amount = getAttributeDamage(amount,player,hurtEvent,LHMiracleRoadAttributes.FLAME_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.ATTACK_CONVERT_FLAME, SpellDamageTypes.FLAME_MAGIC);
        amount = getAttributeDamage(amount,player,hurtEvent,LHMiracleRoadAttributes.LIGHTNING_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.ATTACK_CONVERT_LIGHTNING, SpellDamageTypes.LIGHTNING_MAGIC);
        amount = getAttributeDamage(amount,player,hurtEvent,LHMiracleRoadAttributes.DARK_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.ATTACK_CONVERT_DARK, SpellDamageTypes.DARK_MAGIC);
        amount = getAttributeDamage(amount,player,hurtEvent,LHMiracleRoadAttributes.HOLY_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.ATTACK_CONVERT_HOLY, SpellDamageTypes.HOLY_MAGIC);
        amount = getAttributeDamage(amount,player,hurtEvent,LHMiracleRoadAttributes.MAGIC_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.ATTACK_CONVERT_MAGIC, SpellDamageTypes.MAGIC);

        return amount;
    }

    private static float getAttributeDamage(float amount, ServerPlayer player, LivingEntity hurtEvent, Attribute attributeDamage, Attribute attackConvert, ResourceKey<DamageType> resourceKey){
        AttributeInstance attributeInstance = player.getAttribute(attributeDamage);
        AttributeInstance convertAttributeInstance = player.getAttribute(attackConvert);

        float attributeInstanceDamage = .0f;
        var attribute = ((AttributeInstanceAccess) attributeInstance);
        if (attribute != null) {
            attributeInstanceDamage = (float) attribute.computeIncreasedValueForInitial(0);
        }

        var convertAttribute = ((AttributeInstanceAccess) convertAttributeInstance);
        float amountConvert = .0f;
        if (convertAttribute != null) {
            float damage = (float) convertAttribute.computeIncreasedValueForInitial(convertAttributeInstance.getBaseValue() > 0 ? 0 : 1);
            damage -= convertAttributeInstance.getBaseValue() > 0 ? 0 : 1;
            amountConvert = amount * damage;
        }

        float attributeAmountDamage = attributeInstanceDamage + amountConvert;

        if (attributeAmountDamage > 0) {
//        hurtEvent.invulnerableTime = 0;
            DamageSource src = LHMiracleRoadTool.getDamageSource(player, resourceKey);
            hurtEvent.hurt(src, attributeAmountDamage);
            return amount - amountConvert;
        }else return amount;
    }
}
