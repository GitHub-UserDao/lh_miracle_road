package dev.lhkongyu.lhmiracleroad.tool;

import dev.lhkongyu.lhmiracleroad.entity.magic.LightningBoltEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MagicRelease {

    public static void createLightningBolt(Level level, LivingEntity owner, Vec3 position, float damage, int range) {
        LightningBoltEntity lightningBolt = new LightningBoltEntity(level, owner, EntityType.LIGHTNING_BOLT.create(level));
        lightningBolt.setPos(position);
        lightningBolt.setDamage(damage);
        lightningBolt.setRange(range);
        level.addFreshEntity(lightningBolt);
    }
}
