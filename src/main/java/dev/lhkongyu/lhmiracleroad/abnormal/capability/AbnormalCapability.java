package dev.lhkongyu.lhmiracleroad.abnormal.capability;

import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalData;
import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalType;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypes;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
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

    private static final int BURN_TIME = 3;

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

                DamageSource damageSource = LHMiracleRoadTool.getDamageSource(source, SpellDamageTypes.MAGIC);
                target.hurt(damageSource, damage);
                data.buildup = 0;
                data.active = false;
            }
            case FROST -> {
                float max = target.getMaxHealth();
                float damage = 10f + max * 0.08f;
                DamageSource damageSource = LHMiracleRoadTool.getDamageSource(source, SpellDamageTypes.MAGIC);
                target.hurt(damageSource, damage);
                data.activeTicks = 20 * FROST_TIME;
            }
            case POISON -> data.activeTicks = 20 * POISON_TIME;
            case BURN -> data.activeTicks = 20 * BURN_TIME;
        }
    }

    @Override
    public void tick(LivingEntity target) {
        for (AbnormalType type : AbnormalType.values()) {
            AbnormalData data = get(type);
            if (!data.active)
                continue;

            switch (type) {
                case FROST -> tickFrost(target,data);
                case POISON -> tickPoison(target, data);
                case BURN -> tickBurn(target,data);
            }
        }
    }

    private void tickFrost(LivingEntity target, AbnormalData data) {
        data.activeTicks--;
        int tickCount = (int)(FROST_TIME / TICK_COUNT);
        float reducePerTick = (float) data.maxBuildup / tickCount;
        data.buildup -= reducePerTick;

        if (target.tickCount % 10 == 0) {
            SyncTool.abnormalSync(target);
        }

        if (data.buildup < 0)
            data.buildup = 0;
        if (data.activeTicks <= 0) {
            data.active = false;
        }
    }

    private void tickPoison(LivingEntity target, AbnormalData data) {
        data.activeTicks--;
        int tickCount = (int)(POISON_TIME / TICK_COUNT);
        float reducePerTick = (float) data.maxBuildup / tickCount;
        data.buildup -= reducePerTick;

        if (target.tickCount % 10 == 0) {
            SyncTool.abnormalSync(target);
        }

        if (data.activeTicks <= 0) {
            data.active = false;
        }

        if (target.tickCount % 20 == 0) {
            if (data.lastAttacker == null) return;
            float damage = 2f + target.getMaxHealth() * 0.002f;
            Entity source = ((ServerLevel)target.level()).getEntity(data.lastAttacker);
            if (source == null) return;
            if (source instanceof LivingEntity sourceLivingEntity) {
                DamageSource damageSource = LHMiracleRoadTool.getDamageSource(sourceLivingEntity, SpellDamageTypes.MAGIC);
                target.hurt(damageSource, damage);
            }
        }
    }

    private void tickBurn(LivingEntity target, AbnormalData data) {
        data.activeTicks--;
        int tickCount = (int)(BURN_TIME / TICK_COUNT);
        float reducePerTick = (float) data.maxBuildup / tickCount;
        data.buildup -= reducePerTick;

        if (target.tickCount % 5 == 0) {
            SyncTool.abnormalSync(target);
        }

        if (data.activeTicks <= 0) {
            data.active = false;
            data.buildup = 0;
        }

        if (target.tickCount % 5 == 0) {
            if (data.lastAttacker == null) return;
            Entity source = ((ServerLevel)target.level()).getEntity(data.lastAttacker);
            if (source == null) return;
            if (source instanceof LivingEntity sourceLivingEntity) {
                DamageSource damageSource = LHMiracleRoadTool.getDamageSource(sourceLivingEntity, SpellDamageTypes.FLAME_MAGIC);
                target.hurt(damageSource, 2f);
            }
        }
    }
}
