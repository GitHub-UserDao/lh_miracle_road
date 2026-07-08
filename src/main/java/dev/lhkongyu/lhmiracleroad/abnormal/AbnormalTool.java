package dev.lhkongyu.lhmiracleroad.abnormal;

import dev.lhkongyu.lhmiracleroad.abnormal.capability.AbnormalProvider;
import dev.lhkongyu.lhmiracleroad.attributes.AttributeInstanceAccess;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.client.particle.common.PhotonParticleOption;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypes;
import dev.lhkongyu.lhmiracleroad.registry.ParticleRegistry;
import dev.lhkongyu.lhmiracleroad.tool.NameTool;
import dev.lhkongyu.lhmiracleroad.tool.SyncTool;
import dev.lhkongyu.lhmiracleroad.tool.particle.ParticleTool;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.joml.Vector3f;

public class AbnormalTool {

    public static int getMaxBuildup(float hp, float armor){
        int node = 20;

        if (hp <= 20) return applyArmor(80, armor);

        int buildup = 80;
        if (hp > 30) buildup += 40;
        if (hp > 50) buildup += 35;

        int stages = Math.max(0, (int)((hp - 60) / node));
        for (int i = 0; i < stages; i++) {
            buildup += Math.max(3, (node + 15) - ((i + 1) * 2));
        }
        return applyArmor(buildup, armor);
    }

    public static int getBurnMaxBuildup(float hp){
        int node = 20;

        if (hp <= 20) return 14;

        int buildup = 14;
        int stages = Math.max(0, (int)((hp - 20) / node));
        for (int i = 0; i < stages; i++) {
            buildup += Math.max(2, (node - 6) - ((i + 1) * 2));
        }
        return buildup;
    }

    private static int applyArmor(int buildup, float armor) {
        return Math.round(buildup * (1F + armor * 0.015F));
    }

    public static float abnormalBuildupRaise(float damage,LivingEntity source){
        if (source instanceof Player player) {
            AttributeInstance abnormalAttributeInstance = player.getAttribute(LHMiracleRoadAttributes.ABNORMAL_BUILDUP);
            if (abnormalAttributeInstance != null) {
                damage = (float) (damage * abnormalAttributeInstance.getValue());
            }
        }

        return damage;
    }

    public static void setAbnormalBuildup(AbnormalType abnormalType,float abnormalValue,int maxBuildup,LivingEntity target,LivingEntity source){
        target.getCapability(AbnormalProvider.CAPABILITY).ifPresent(cap -> {
            cap.addBuildUp(source, target, abnormalType, abnormalValue, maxBuildup);
            SyncTool.abnormalSync(target);
        });
    }

    public static void attackAbnormalBuildup(LivingEntity target,LivingEntity source) {
        float bleed = (float) source.getAttributeValue(LHMiracleRoadAttributes.ABNORMAL_BLEED_BUILDUP);
        float frost = (float) source.getAttributeValue(LHMiracleRoadAttributes.ABNORMAL_FROST_BUILDUP);
        float poison = (float) source.getAttributeValue(LHMiracleRoadAttributes.ABNORMAL_POISON_BUILDUP);

        int maxBuildup;
        if (bleed > 0){
            bleed = abnormalBuildupRaise(bleed,source);
            maxBuildup = AbnormalTool.getMaxBuildup(target.getMaxHealth(),target.getArmorValue());
            setAbnormalBuildup(AbnormalType.BLEED,bleed,maxBuildup,target,source);
        }
        if (frost > 0){
            frost = abnormalBuildupRaise(frost,source);
            maxBuildup = AbnormalTool.getMaxBuildup(target.getMaxHealth(),target.getArmorValue());
            setAbnormalBuildup(AbnormalType.FROST,frost,maxBuildup,target,source);
        }

        if (poison > 0){
            poison = abnormalBuildupRaise(poison,source);
            maxBuildup = AbnormalTool.getMaxBuildup(target.getMaxHealth(),target.getArmorValue());
            setAbnormalBuildup(AbnormalType.POISON,poison,maxBuildup,target,source);
        }
    }

    public static void attackAbnormalBurnBuildup(LivingEntity target,LivingEntity source,float amount) {
        if (amount > 0){
            if (source instanceof Player player) {
                AttributeInstance abnormalBurnAttributeInstance = player.getAttribute(LHMiracleRoadAttributes.ABNORMAL_BURN_BUILDUP);
                if (abnormalBurnAttributeInstance != null) {
                    AttributeInstanceAccess abnormalBurnInstanceAccess = ((AttributeInstanceAccess) abnormalBurnAttributeInstance);
                    float accessDamage = (float) abnormalBurnInstanceAccess.lh_miracle_road$computeIncreasedValueForInitial(1);
                    amount = amount * accessDamage;
                }

                AttributeInstance abnormalAttributeInstance = player.getAttribute(LHMiracleRoadAttributes.ABNORMAL_BUILDUP);
                if (abnormalAttributeInstance != null) {
                    amount = (float) (amount * abnormalAttributeInstance.getValue());
                }
            }
            int maxBuildup = AbnormalTool.getBurnMaxBuildup(target.getMaxHealth());
            setAbnormalBuildup(AbnormalType.BURN,amount,maxBuildup,target,source);
        }
    }

    public static boolean isAbnormalDamageTypes(DamageSource damageSource){
        if (damageSource.is(SpellDamageTypes.ABNORMAL_BLEED)){
            return true;
        }else if (damageSource.is(SpellDamageTypes.ABNORMAL_FROST)){
            return true;
        }else if (damageSource.is(SpellDamageTypes.ABNORMAL_POISON)){
            return true;
        }else return damageSource.is(SpellDamageTypes.ABNORMAL_BURN);
    }

    private static void abnormalDamage(LivingDamageEvent event, Player player, Attribute attribute, DamageSource damageSource, ResourceKey<DamageType> abnormalType){
        if (damageSource.is(abnormalType)){
            AttributeInstance abnormal = player.getAttribute(attribute);
            if (abnormal != null) {
                float damage = (float) (event.getAmount() * abnormal.getValue());
                event.setAmount(damage);
            }
        }
    }

    public static void setAbnormalDamage(LivingDamageEvent event, Player player){
        DamageSource damageSource = event.getSource();
        //异常伤害加成
        if (AbnormalTool.isAbnormalDamageTypes(damageSource)){
            //单独异常伤害加成
            abnormalDamage(event,player,LHMiracleRoadAttributes.ABNORMAL_BLEED_DAMAGE,damageSource,SpellDamageTypes.ABNORMAL_BLEED);
            abnormalDamage(event,player,LHMiracleRoadAttributes.ABNORMAL_FROST_DAMAGE,damageSource,SpellDamageTypes.ABNORMAL_FROST);
            abnormalDamage(event,player,LHMiracleRoadAttributes.ABNORMAL_POISON_DAMAGE,damageSource,SpellDamageTypes.ABNORMAL_POISON);
            abnormalDamage(event,player,LHMiracleRoadAttributes.ABNORMAL_BURN_DAMAGE,damageSource,SpellDamageTypes.ABNORMAL_BURN);

            //全部异常伤害加成
            AttributeInstance abnormalAttributeInstance = player.getAttribute(LHMiracleRoadAttributes.ABNORMAL_DAMAGE);
            if (abnormalAttributeInstance != null) {
                float damage = (float) (event.getAmount() * abnormalAttributeInstance.getValue());
                event.setAmount(damage);
            }
        }
    }

    public static void abnormalAddParticle(LivingEntity target,float damage,String abnormal){
        float width = target.getBbWidth();
        float height = target.getBbHeight();
        Vec3 position = target.position().add(0.0F, (height / 2.0F), 0.0F);
        int particleCount = (int) Math.max(10.0F * width * height + Math.min(damage,30),12);

        switch (abnormal){
            case NameTool.BLOOD:
                ParticleTool.spawnServerParticles(target.level(),
                        (SimpleParticleType) ParticleRegistry.BLEED.get(),
                        true,
                        position.x, position.y, position.z,
                        particleCount,
                        width / 3.0F, height / 8.0F, width / 3.0F,
                        0.1);
                break;
            case NameTool.ICE:
                Vector3f color = ParticleTool.RGBChangeVector3f(203,219,252);
                ParticleTool.spawnServerParticles(target.level(),
                        new PhotonParticleOption(color,1.25f,0),
                        true,
                        position.x, position.y, position.z,
                        particleCount,
                        width / 5.0F, height / 5.0F, width / 5.0F,
                        0.075);
                ParticleTool.spawnServerParticles(target.level(),
                        (SimpleParticleType)ParticleRegistry.SNOW_FLAKE.get(),
                        true,
                        position.x, position.y, position.z,
                        particleCount / 2,
                        width / 5.0F, height / 5.0F, width / 5.0F,
                        0.035);
                break;
        }
    }

    public static void abnormalSustainParticle(LivingEntity target,int particleCount,String abnormal){
        float width = target.getBbWidth();
        float height = target.getBbHeight();
        Vec3 position = target.position().add(0.0F, (height / 2.0F), 0.0F);

        switch (abnormal){
            case NameTool.ICE:
                ParticleTool.spawnServerParticles(target.level(),
                        (SimpleParticleType)ParticleRegistry.SNOW_FLAKE.get(),
                        true,
                        position.x, position.y, position.z,
                        particleCount,
                        width / 5.0F, height / 5.0F, width / 5.0F,
                        0.035);
                break;
            case NameTool.POISON:
                ParticleTool.spawnServerParticles(target.level(),
                        (SimpleParticleType)ParticleRegistry.POISON.get(),
                        true,
                        position.x, position.y, position.z,
                        particleCount,
                        width / 5.0F, height / 5.0F, width / 5.0F,
                        0.03);
                break;
            case NameTool.FLAME:
                ParticleTool.spawnServerParticles(target.level(),
                        (SimpleParticleType)ParticleRegistry.FIRE_PARTICLE.get(),
                        true,
                        position.x, position.y, position.z,
                        particleCount,
                        width / 4.0F, height / 5.0F, width / 4.0F,
                        0.06);
        }
    }
}
