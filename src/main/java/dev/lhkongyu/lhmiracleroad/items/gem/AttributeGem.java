package dev.lhkongyu.lhmiracleroad.items.gem;

import com.google.common.collect.Multimap;
import dev.lhkongyu.lhmiracleroad.attributes.AttributeInstanceAccess;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypeTagGenerator;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypes;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.registry.TagsRegistry;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.NameTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.Map;
import java.util.UUID;

public class AttributeGem {

    public static ItemStack attributeStrengthen(ItemStack baseItemStack, ItemStack gemItemStack){
        if (!LHMiracleRoadTool.itemIsWeaponsAll(baseItemStack)) return ItemStack.EMPTY;
        CompoundTag tag = baseItemStack.getOrCreateTag().getCompound("lh_gem");
        ItemStack out = baseItemStack.copy();
        //如果存在质变的tag，就可以使用砥石清除质变
        if (tag.contains("type")) {
            if (gemItemStack.is(ItemsRegistry.WHETSTONE.get())) {
                CompoundTag compoundTag = tag.copy();
                compoundTag.remove("type");
                out.getOrCreateTag().put("lh_gem",compoundTag);
            }
            return out;
        }

        String type = LHMiracleRoadTool.getGemType(gemItemStack);
        if (type == null) return ItemStack.EMPTY;
        if (tag.isEmpty()) tag = new CompoundTag();

        CompoundTag compoundTag = tag.copy();
        compoundTag.putString("type", type);
        out.getOrCreateTag().put("lh_gem", compoundTag);
        return out;
    }

    public static void setAttributeStrengthen(CompoundTag gemTag,ItemAttributeModifierEvent event){
        String type = gemTag.getString("type");
        if (type.isEmpty()) return;
        UUID attributeUUID = UUID.fromString("4f45d87b-87c7-a2e3-8a92-7647175709c8");
        UUID attackUUID = UUID.fromString("c074f091-6cf0-4540-8e3f-ffb1571e0adf");

        double convertValue = 1;
        Multimap<Attribute, AttributeModifier> modifiers = event.getModifiers();
        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
            Attribute attribute = entry.getKey();
            if (attribute == Attributes.ATTACK_DAMAGE){
                AttributeModifier modifier = entry.getValue();
                double amount = modifier.getAmount();
                if (amount > 0) convertValue += amount;
            }
        }

        double attack = convertValue / 2;
        double bonusAttack =  2 + attack;
        switch (type){
            case NameTool.FLAME:
                if (bonusAttack > 0) event.addModifier(LHMiracleRoadAttributes.FLAME_ATTRIBUTE_DAMAGE,
                        new AttributeModifier(attributeUUID, "lh_gem_" + NameTool.FLAME_ATTRIBUTE_DAMAGE, bonusAttack, AttributeModifier.Operation.ADDITION));
                event.addModifier(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(attackUUID, "lh_gem_attack", -attack, AttributeModifier.Operation.ADDITION));
                break;
            case NameTool.LIGHTNING:
                if (bonusAttack > 0) event.addModifier(LHMiracleRoadAttributes.LIGHTNING_ATTRIBUTE_DAMAGE,
                        new AttributeModifier(attributeUUID, "lh_gem_" + NameTool.LIGHTNING_ATTRIBUTE_DAMAGE, bonusAttack, AttributeModifier.Operation.ADDITION));
                event.addModifier(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(attackUUID, "lh_gem_attack", -attack, AttributeModifier.Operation.ADDITION));
                break;
            case NameTool.DARK:
                if (bonusAttack > 0) event.addModifier(LHMiracleRoadAttributes.DARK_ATTRIBUTE_DAMAGE,
                        new AttributeModifier(attributeUUID,  "lh_gem_" + NameTool.DARK_ATTRIBUTE_DAMAGE, bonusAttack, AttributeModifier.Operation.ADDITION));
                event.addModifier(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(attackUUID, "lh_gem_attack", -attack, AttributeModifier.Operation.ADDITION));
                break;
            case NameTool.BLOOD, NameTool.SHARP, NameTool.POISON:
                break;
            case NameTool.ICE:
                if (bonusAttack > 0) event.addModifier(LHMiracleRoadAttributes.MAGIC_ATTRIBUTE_DAMAGE,
                        new AttributeModifier(attributeUUID,  "lh_gem_" + NameTool.ICE, bonusAttack, AttributeModifier.Operation.ADDITION));
                event.addModifier(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(attackUUID, "lh_gem_attack", -attack, AttributeModifier.Operation.ADDITION));
                break;
        }
    }

    public static double setAttackType(CompoundTag gemTag){
        String type = gemTag.getString("type");
        return switch (type) {
            case NameTool.SHARP -> 2;
            case NameTool.POISON, NameTool.BLOOD -> 1;
            default -> 0;
        };
    }

    public static void getAttributeDamage( LivingEntity source, LivingEntity hurtEvent){
        getAttributeDamage(source,hurtEvent,LHMiracleRoadAttributes.FLAME_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.ATTACK_CONVERT_FLAME, SpellDamageTypes.FLAME_MAGIC);
        getAttributeDamage(source,hurtEvent,LHMiracleRoadAttributes.LIGHTNING_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.ATTACK_CONVERT_LIGHTNING, SpellDamageTypes.LIGHTNING_MAGIC);
        getAttributeDamage(source,hurtEvent,LHMiracleRoadAttributes.DARK_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.ATTACK_CONVERT_DARK, SpellDamageTypes.DARK_MAGIC);
        getAttributeDamage(source,hurtEvent,LHMiracleRoadAttributes.HOLY_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.ATTACK_CONVERT_HOLY, SpellDamageTypes.HOLY_MAGIC);
        getAttributeDamage(source,hurtEvent,LHMiracleRoadAttributes.MAGIC_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.ATTACK_CONVERT_MAGIC, SpellDamageTypes.MAGIC);
    }

    private static void getAttributeDamage( LivingEntity source, LivingEntity hurtEvent, Attribute attributeDamage, Attribute attackConvert, ResourceKey<DamageType> resourceKey){
        AttributeInstance attributeInstance = source.getAttribute(attributeDamage);

        float attributeInstanceDamage = .0f;
        var attribute = ((AttributeInstanceAccess) attributeInstance);
        if (attribute != null) {
            attributeInstanceDamage = (float) attribute.computeIncreasedValueForInitial(0);
        }

        if (attributeInstanceDamage > 0) {
//            hurtEvent.invulnerableTime = 0;
            DamageSource src = LHMiracleRoadTool.getDamageSource(source, resourceKey);
            hurtEvent.hurt(src, attributeInstanceDamage);
        }
    }

}
