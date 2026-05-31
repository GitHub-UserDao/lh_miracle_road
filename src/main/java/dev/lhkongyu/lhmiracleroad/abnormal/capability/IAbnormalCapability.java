package dev.lhkongyu.lhmiracleroad.abnormal.capability;

import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalData;
import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalType;
import net.minecraft.world.entity.LivingEntity;

public interface IAbnormalCapability {

    AbnormalData get(AbnormalType type);

    void addBuildUp(LivingEntity source,LivingEntity target, AbnormalType type, float value,int maxBuildup);

    void tick(LivingEntity entity);
}
