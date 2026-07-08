package dev.lhkongyu.lhmiracleroad.items.gem;

import com.google.common.collect.Multimap;
import dev.lhkongyu.lhmiracleroad.attributes.AttributeInstanceAccess;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.client.particle.common.PhotonParticleOption;
import dev.lhkongyu.lhmiracleroad.client.particle.soul.SoulParticleOption;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypes;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.registry.ParticleRegistry;
import dev.lhkongyu.lhmiracleroad.tool.*;
import dev.lhkongyu.lhmiracleroad.tool.particle.ParticleTool;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AttributeGem {

    public static ItemStack attributeStrengthen(ItemStack baseItemStack, ItemStack gemItemStack){
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

        String type = GemTool.getGemType(gemItemStack);
        if (type == null) return ItemStack.EMPTY;
        if (tag.isEmpty()) tag = new CompoundTag();

        CompoundTag compoundTag = tag.copy();
        compoundTag.putString("type", type);
        out.getOrCreateTag().put("lh_gem", compoundTag);
        return out;
    }

    private static void setAttribute(ItemAttributeModifierEvent event,double bonusAttack,double attack,
                                        UUID attributeUUID,UUID attackUUID,String name,Attribute attribute){
        if (LHMiracleRoadTool.itemIsRangedWeapons(event.getItemStack())) {
           bonusAttack = bonusAttack + 3;
            event.addModifier(attribute,
                    new AttributeModifier(attributeUUID, "lh_gem_" + name, bonusAttack, AttributeModifier.Operation.ADDITION));
        }else {
           bonusAttack += attack;
           if (bonusAttack <= 0) return;
           event.addModifier(attribute,
                   new AttributeModifier(attributeUUID, "lh_gem_" + name, bonusAttack, AttributeModifier.Operation.ADDITION));
           event.addModifier(Attributes.ATTACK_DAMAGE,
                   new AttributeModifier(attackUUID, "lh_gem_attack_" + name, -attack, AttributeModifier.Operation.ADDITION));
       }
    }

    private static void setAbnormal(ItemAttributeModifierEvent event,double abnormalValue,
                                     UUID abnormalUUID,UUID attackUUID,String name,Attribute attribute){;
        if (LHMiracleRoadTool.itemIsRangedWeapons(event.getItemStack())) {
            event.addModifier(attribute,
                    new AttributeModifier(abnormalUUID, "lh_gem_" + name, abnormalValue, AttributeModifier.Operation.ADDITION));
            event.addModifier(LHMiracleRoadAttributes.RANGED_DAMAGE,
                    new AttributeModifier(attackUUID, "lh_gem_ranged_" + name, -0.1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }else {
            event.addModifier(attribute, new AttributeModifier(abnormalUUID, "lh_gem_" + name, abnormalValue, AttributeModifier.Operation.ADDITION));
            event.addModifier(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(attackUUID, "lh_gem_attack_" + name, -1, AttributeModifier.Operation.ADDITION));
        }
    }

    /**
     * 根据攻击速度计算异常状态积累值
     * @param attackSpeed 攻击速度值
     * @return 异常状态积累值
     */
    private static int calculateAbnormalValue(double attackSpeed,ItemStack itemStack) {
        // 基准满性能值
        int fullPerformance = 38;
        if (LHMiracleRoadTool.itemIsRangedWeapons(itemStack)) return (int) (fullPerformance * 0.85);
        if (attackSpeed <= 0.6){
            // 0.6 及以下：更高性能
            return 42;
        } else if (attackSpeed <= 1) {
            // 0.6 - 1 满性能
            return fullPerformance;
        } else if (attackSpeed <= 1.6) {
            // 1 - 1.6：性能下降 15%
            return (int) (fullPerformance * 0.85);
        }else if (attackSpeed <= 2.0){
            // 1.6 - 2.0：性能下降 25%
            return (int) (fullPerformance * 0.75);
        }else if (attackSpeed <= 2.4){
            // 2.0 - 2.4：性能下降 40%
            return (int) (fullPerformance * 0.6);
        }else if (attackSpeed <= 3.0) {
            // 2.4 - 3.0：性能下降 50%
            return (int) (fullPerformance * 0.5);
        } else {
            // 超过 3.0：值为 12
            return 12;
        }
    }

    public static void setAttributeStrengthen(CompoundTag gemTag,ItemAttributeModifierEvent event){
        String type = gemTag.getString("type");
        if (type.isEmpty()) return;
        UUID attributeUUID = UUID.fromString("4f45d87b-87c7-a2e3-8a92-7647175709c8");
        UUID attackUUID = UUID.fromString("c074f091-6cf0-4540-8e3f-ffb1571e0adf");
        UUID abnormalUUID = UUID.fromString("4f57bcf7-9775-4176-984b-b59786d16b10");

        double convertValue = 1;
        double bonusAttack =  2;
        double baseAttackSpeed = 4;

        Multimap<Attribute, AttributeModifier> modifiers = event.getModifiers();
        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
            Attribute attribute = entry.getKey();
            if (attribute == Attributes.ATTACK_DAMAGE){
                AttributeModifier modifier = entry.getValue();
                if (modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)) {
                    double amount = modifier.getAmount();
                    if (amount > 0) convertValue += amount;
                }
            }else if (attribute == Attributes.ATTACK_SPEED){
                AttributeModifier modifier = entry.getValue();
                if (modifier.getName().equals("lh_gem_attack_" + NameTool.SHARP) || modifier.getName().equals("lh_gem_attack_speed")) continue;
                if (modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)) {
                    double amount = modifier.getAmount();
                    baseAttackSpeed += amount;
                }
            }
        }

        int abnormalValue = calculateAbnormalValue(baseAttackSpeed,event.getItemStack());
        double attack = convertValue / 2;
        switch (type){
            case NameTool.FLAME -> setAttribute(event,bonusAttack,attack,attributeUUID,attackUUID,
                    NameTool.FLAME_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.FLAME_ATTRIBUTE_DAMAGE);
            case NameTool.LIGHTNING -> setAttribute(event,bonusAttack,attack,attributeUUID,attackUUID,
                    NameTool.LIGHTNING_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.LIGHTNING_ATTRIBUTE_DAMAGE);
            case NameTool.DARK -> setAttribute(event,bonusAttack,attack,attributeUUID,attackUUID,
                    NameTool.DARK_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.DARK_ATTRIBUTE_DAMAGE);
            case NameTool.BLOOD -> setAbnormal(event,abnormalValue,abnormalUUID,attackUUID,
                    NameTool.ABNORMAL_BLEED_BUILDUP,LHMiracleRoadAttributes.ABNORMAL_BLEED_BUILDUP);
            case NameTool.POISON -> setAbnormal(event,abnormalValue,abnormalUUID,attackUUID,
                    NameTool.ABNORMAL_POISON_BUILDUP,LHMiracleRoadAttributes.ABNORMAL_POISON_BUILDUP);
            case NameTool.ICE -> setAbnormal(event,abnormalValue,abnormalUUID,attackUUID,
                    NameTool.ABNORMAL_FROST_BUILDUP,LHMiracleRoadAttributes.ABNORMAL_FROST_BUILDUP);
            case NameTool.HOLY -> setAttribute(event,bonusAttack,attack,attributeUUID,attackUUID,
                    NameTool.HOLY_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.HOLY_ATTRIBUTE_DAMAGE);
            case NameTool.SOUL -> setAttribute(event,bonusAttack,attack,attributeUUID,attackUUID,
                    NameTool.SOUL_ATTRIBUTE_DAMAGE,LHMiracleRoadAttributes.SOUL_ATTRIBUTE_DAMAGE);
            case NameTool.HEAVY -> event.addModifier(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(attackUUID, "lh_gem_attack_" + NameTool.HEAVY, 0.2, AttributeModifier.Operation.MULTIPLY_BASE));
            case NameTool.SHARP ->{
//                event.addModifier(Attributes.ATTACK_SPEED,
//                        new AttributeModifier(attackUUID, "lh_gem_attack_" + NameTool.SHARP, 0.2, AttributeModifier.Operation.MULTIPLY_BASE));
                event.addModifier(LHMiracleRoadAttributes.ARMOR_PENETRATION,
                        new AttributeModifier(UUID.fromString("51a1025f-9761-45a1-a7df-af693c77b858"), "lh_gem_armor_penetration_" + NameTool.SHARP, 0.3, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
    }

    public static double setAttackType(CompoundTag gemTag){
        String type = gemTag.getString("type");
        return switch (type) {
            case NameTool.SHARP -> 3;
            case NameTool.HEAVY -> 2;
            default -> 0;
        };
    }

    public static void getAttributeDamage(LivingEntity source, LivingEntity target){
        getAttributeDamage(source,target,LHMiracleRoadAttributes.FLAME_ATTRIBUTE_DAMAGE, SpellDamageTypes.FLAME_MAGIC);
        getAttributeDamage(source,target,LHMiracleRoadAttributes.LIGHTNING_ATTRIBUTE_DAMAGE, SpellDamageTypes.LIGHTNING_MAGIC);
        getAttributeDamage(source,target,LHMiracleRoadAttributes.DARK_ATTRIBUTE_DAMAGE, SpellDamageTypes.DARK_MAGIC);
        getAttributeDamage(source,target,LHMiracleRoadAttributes.HOLY_ATTRIBUTE_DAMAGE, SpellDamageTypes.HOLY_MAGIC);
        getAttributeDamage(source,target,LHMiracleRoadAttributes.MAGIC_ATTRIBUTE_DAMAGE, SpellDamageTypes.MAGIC);
        getAttributeDamage(source,target,LHMiracleRoadAttributes.SOUL_ATTRIBUTE_DAMAGE, SpellDamageTypes.SOUL_MAGIC);
    }

    private static void getAttributeDamage(LivingEntity source, LivingEntity target, Attribute attributeDamage,  ResourceKey<DamageType> resourceKey){
        AttributeInstance attributeInstance = source.getAttribute(attributeDamage);

        float attributeInstanceDamage = .0f;
        var attribute = ((AttributeInstanceAccess) attributeInstance);
        if (attribute != null) {
            attributeInstanceDamage = (float) attribute.lh_miracle_road$computeIncreasedValueForInitial(0);
        }

        if (attributeInstanceDamage > 0) {
//            hurtEvent.invulnerableTime = 0;
            if (resourceKey.equals(SpellDamageTypes.FLAME_MAGIC)){
                target.setRemainingFireTicks(10);
            }
            LHMiracleRoadTool.attackMagicHurt(source,target,resourceKey,attributeInstanceDamage);

            if (resourceKey.equals(SpellDamageTypes.LIGHTNING_MAGIC)){
                float luck = 0;
                if (source instanceof Player player)  luck = (float) LHMiracleRoadTool.getLuckBonus(50.0,player.getLuck());
                if (LHMiracleRoadTool.percentageProbability(25 + luck)) {
                    MagicRelease.createLightningBolt(source.level(), source, target.position(), attributeInstanceDamage * 1.5f, 3);
                }
            }
        }
    }

    public static void attackParticleSpecialEffects(LivingEntity source, LivingEntity target){
        ItemStack baseItemStack = source.getItemInHand(InteractionHand.MAIN_HAND);
        CompoundTag tag = baseItemStack.getOrCreateTag().getCompound("lh_gem");
        if (tag.contains("type")) {
            String type = tag.getString("type");

            float width = target.getBbWidth();
            float height = target.getBbHeight();
            Vec3 position = target.position().add(0.0F, (height / 2.0F), 0.0F);
            int particleCount = (int) Math.max(8.0F * width * height,5);
            switch (type){
                case NameTool.FLAME:
                    ParticleTool.spawnServerParticles(source.level(),
                            (SimpleParticleType)ParticleRegistry.FIRE_PARTICLE.get(),
                            true,
                            position.x, position.y, position.z,
                            particleCount,
                            width / 4.0F, height / 5.0F, width / 4.0F,
                            0.06);
                break;
                case NameTool.LIGHTNING:
                    ParticleTool.spawnServerParticles(source.level(),
                            (SimpleParticleType)ParticleRegistry.LIGHTNING.get(),
                            true,
                            position.x, position.y, position.z,
                            particleCount,
                            width / 5.0F, height / 5.0F, width / 5.0F,
                            0.05);
                    break;
                case NameTool.DARK:
                    ParticleTool.spawnServerParticles(source.level(),
                            (SimpleParticleType)ParticleRegistry.DARK.get(),
                            true,
                            position.x, position.y, position.z,
                            particleCount,
                            width / 5.0F, height / 5.0F, width / 5.0F,
                            0.035);
                    break;
                case NameTool.BLOOD:
                    ParticleTool.spawnServerParticles(source.level(),
                            (SimpleParticleType) ParticleRegistry.BLEED.get(),
                            true,
                            position.x, position.y, position.z,
                            5,
                            width / 3.0F, height / 8.0F, width / 3.0F,
                            0.1);
                    break;
                case NameTool.HOLY:
                    Vector3f color = ParticleTool.RGBChangeVector3f(251,242,54);
                    ParticleTool.spawnServerParticles(target.level(),
                            new PhotonParticleOption(color,1.25f,0),
                            true,
                            position.x, position.y, position.z,
                            particleCount,
                            width / 5.0F, height / 5.0F, width / 5.0F,
                            0.075);
                    break;
                case NameTool.POISON:
                    ParticleTool.spawnServerParticles(source.level(),
                            (SimpleParticleType)ParticleRegistry.POISON.get(),
                            true,
                            position.x, position.y, position.z,
                            particleCount,
                            width / 5.0F, height / 5.0F, width / 5.0F,
                            0.03);
                    break;
                case NameTool.ICE:
                    ParticleTool.spawnServerParticles(source.level(),
                            (SimpleParticleType)ParticleRegistry.SNOW_FLAKE.get(),
                            true,
                            position.x, position.y, position.z,
                            Math.max(particleCount / 2,5),
                            width / 5.0F, height / 5.0F, width / 5.0F,
                            0.035);
                    break;
                case NameTool.SOUL:
                    ParticleTool.spawnServerParticles(target.level(),
                            new SoulParticleOption(source.getId()),
                            true,
                            position.x, position.y, position.z,
                            particleCount / 2,
                            0.1, 0.1, 0.1,
                            .1f);
                    break;
            }
        }
    }

    /**
     * 高级属性附加伤害效果
     */
    public static float attributeAdditionalDamage(float amount,DamageSource damageSource,LivingEntity target,LivingEntity source){
        if (damageSource.is(SpellDamageTypes.DARK_MAGIC)){
            amount += target.getHealth() * 0.04f;
        }else if (damageSource.is(SpellDamageTypes.HOLY_MAGIC)){
            if (target.getMobType() == MobType.UNDEAD) amount += amount * 0.5f;
        }else if (damageSource.is(SpellDamageTypes.SOUL_MAGIC)){
            if (source instanceof ServerPlayer player){
                Optional<PlayerOccupationAttribute> optional =
                        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
                if (optional.isEmpty()) return amount;
                PlayerOccupationAttribute playerOccupationAttribute = optional.get();
                int soulCount = playerOccupationAttribute.getOccupationExperience();
                amount = amount + (amount * (soulCount * 0.0000002f));
            }else {
                int hp = (int) LHMiracleRoadTool.getAttributeValue(target.getAttribute(Attributes.MAX_HEALTH));
                int atk = (int) LHMiracleRoadTool.getAttributeValue(target.getAttribute(Attributes.ATTACK_DAMAGE));
                int arm = (int) LHMiracleRoadTool.getAttributeValue(target.getAttribute(Attributes.ARMOR));
                int atou = (int) LHMiracleRoadTool.getAttributeValue(target.getAttribute(Attributes.ARMOR_TOUGHNESS));
                int buff = (int) target.getActiveEffects().stream().map(MobEffectInstance::getEffect).filter(MobEffect::isBeneficial).count();
                int entityDroppedXp = target.getExperienceReward();
                entityDroppedXp = Math.max(entityDroppedXp,15);
                int expValue = LHMiracleRoadTool.evaluateFormula(LHMiracleRoadConfig.COMMON.EXPERIENCE_ACQUISITION_FORMULA.get(),entityDroppedXp,hp,atk,arm,atou,buff);
                amount += amount * Math.min(expValue * 0.000005f,100);
            }
        }else if (damageSource.is(SpellDamageTypes.LIGHTNING_MAGIC)){
            int arm = (int) LHMiracleRoadTool.getAttributeValue(target.getAttribute(Attributes.ARMOR));
            amount += amount * Math.min((arm * 0.01f),100);
        }

        return amount;
    }

}
