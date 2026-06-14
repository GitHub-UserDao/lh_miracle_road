package dev.lhkongyu.lhmiracleroad.abnormal.capability;

import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalData;
import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalTool;
import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalType;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypes;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.NameTool;
import dev.lhkongyu.lhmiracleroad.tool.SyncTool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumMap;

public class AbnormalCapability implements IAbnormalCapability {

    private final EnumMap<AbnormalType, AbnormalData> map =
            new EnumMap<>(AbnormalType.class);

    private static final int FROST_TIME = 30;

    private static final int POISON_TIME = 45;

    private static final int BURN_TIME = 2;

    private static final int DISSIPATE_TIME = 20;

    private static final float TICK_COUNT = 0.05f;

    public AbnormalCapability() {

        for (AbnormalType type : AbnormalType.values()) {
            map.put(type, new AbnormalData());
        }
    }

    @Override
    public AbnormalData get(AbnormalType type) {
        return map.get(type);
    }

    @Override
    public void addBuildUp(LivingEntity source,LivingEntity target, AbnormalType type, float value,int maxBuildup) {
        AbnormalData data = get(type);
        data.maxBuildup = maxBuildup;

        // 激活期间不再累计
        if (data.active)
            return;

        data.buildup += value;

        data.lastAttacker = source.getUUID();
        data.lastAttackTime = System.currentTimeMillis();

        if (data.buildup >= data.maxBuildup) {
            data.buildup = data.maxBuildup;
            trigger(type, source, target, data);
        }
    }

    private void trigger(AbnormalType type, LivingEntity source,LivingEntity target, AbnormalData data) {
        data.active = true;

        switch (type) {
            case BLEED -> {
                float current = target.getHealth();
                float max = target.getMaxHealth();
                float damage = 6f + max * 0.06f + Math.min(current * 0.1f, 10f);

                LHMiracleRoadTool.magicHurt(source,target,SpellDamageTypes.ABNORMAL_BLEED,damage);

                AbnormalTool.abnormalAddParticle(target,damage, NameTool.BLOOD);
                data.buildup = 0;
                data.active = false;
            }
            case FROST -> {
                float max = target.getMaxHealth();
                float damage = 10f + max * 0.08f;
                LHMiracleRoadTool.magicHurt(source,target,SpellDamageTypes.ABNORMAL_FROST,damage);

                AbnormalTool.abnormalAddParticle(target,damage, NameTool.ICE);
            }
        }
    }

    @Override
    public void tick(LivingEntity target) {
        for (AbnormalType type : AbnormalType.values()) {
            AbnormalData data = get(type);
            long now = System.currentTimeMillis();
            long elapsed = now - data.lastAttackTime;
            if (elapsed > 5000 && data.lastAttacker != null && !data.active){
                int tickCount = (int)(DISSIPATE_TIME / TICK_COUNT);
                float reducePerTick = (float) data.maxBuildup / tickCount;
                data.buildup -= Math.max(reducePerTick,0f);

                if (target.tickCount % 10 == 0) {
                    SyncTool.abnormalSync(target);
                }
            }

            if (!data.active) continue;

            switch (type) {
                case FROST -> tickFrost(target,data);
                case POISON -> tickPoison(target, data);
                case BURN -> tickBurn(target,data);
            }

            if (data.buildup <= 0) {
                data.lastAttacker = null;
                data.lastAttackTime = 0;
                data.active = false;
                SyncTool.abnormalSync(target);
            }
        }
    }

    private void tickFrost(LivingEntity target, AbnormalData data) {
        int tickCount = (int)(FROST_TIME / TICK_COUNT);
        float reducePerTick = (float) data.maxBuildup / tickCount;
        data.buildup -= Math.max(reducePerTick,0f);

        if (target.tickCount % 10 == 0) {
            SyncTool.abnormalSync(target);
            AbnormalTool.abnormalSustainParticle(target,3,NameTool.ICE);
        }
    }

    private void tickPoison(LivingEntity target, AbnormalData data) {
        int tickCount = (int)(POISON_TIME / TICK_COUNT);
        float reducePerTick = (float) data.maxBuildup / tickCount;
        data.buildup -= Math.max(reducePerTick,0f);

        if (target.tickCount % 10 == 0) {
            SyncTool.abnormalSync(target);
            AbnormalTool.abnormalSustainParticle(target,5,NameTool.POISON);
        }

        if (target.tickCount % 20 == 0) {
            if (data.lastAttacker == null) return;
            float damage = 1f + target.getMaxHealth() * 0.002f;
            Entity source = ((ServerLevel)target.level()).getEntity(data.lastAttacker);
            if (source == null) return;
            if (source instanceof LivingEntity sourceLivingEntity) {
                LHMiracleRoadTool.magicHurt(sourceLivingEntity,target,SpellDamageTypes.ABNORMAL_POISON,damage);
            }
        }
    }

    private void tickBurn(LivingEntity target, AbnormalData data) {
        int tickCount = (int)(BURN_TIME / TICK_COUNT);
        float reducePerTick = (float) data.maxBuildup / tickCount;
        data.buildup -= Math.max(reducePerTick,0f);

        if (target.tickCount % 5 == 0) {
            SyncTool.abnormalSync(target);
        }

        if (target.tickCount % 4 == 0) {
            if (data.lastAttacker == null) return;
            Entity source = ((ServerLevel)target.level()).getEntity(data.lastAttacker);
            if (source == null) return;
            if (source instanceof LivingEntity sourceLivingEntity) {
                LHMiracleRoadTool.magicHurt(sourceLivingEntity,target,SpellDamageTypes.ABNORMAL_BURN,2f);
                AbnormalTool.abnormalSustainParticle(target,3,NameTool.FLAME);
            }
        }
    }
}
