package dev.lhkongyu.lhmiracleroad.enchantments;

import dev.lhkongyu.lhmiracleroad.registry.EnchantmentRegistry;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class ShockwaveEnchantment extends Enchantment {

    public ShockwaveEnchantment() {

        super(
                Rarity.VERY_RARE,
                EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{
                        EquipmentSlot.MAINHAND
                }
        );
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return super.isDiscoverable();
    }

    public static void shockwave(LivingEntity target, Player player, float damage){
//        ItemStack weapon = player.getMainHandItem();
//        int level = weapon.getEnchantmentLevel(EnchantmentRegistry.SHOCKWAVE.get());
//
//        if (level <= 0) return;
//
//        // 计算范围和伤害
//        double radius = 2.0 + (level - 1) * 0.5; // 2格 + (等级-1) * 0.5格
//        float damagePercentage = 0.2f + (level - 1) * 0.05f; // 20% + (等级-1) * 5%
//        float shockwaveDamage = damage * damagePercentage;
//
//        Level levelWorld = target.level();
//
//        if (!levelWorld.isClientSide()) {
//            ServerLevel serverLevel = (ServerLevel) levelWorld;
//            // 创建范围检测，以目标为中心的圆形范围
//            AABB aabb = new AABB(
//                    target.getX() - radius,
//                    target.getY() - 1.0,
//                    target.getZ() - radius,
//                    target.getX() + radius,
//                    target.getY() + 1.0,
//                    target.getZ() + radius
//            );
//
//            // 获取范围内所有生物实体
//            List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, aabb,
//                    entity -> entity != target &&
//                            LHMiracleRoadTool.isBelongTeammate(entity,player) &&
//                            entity.isAlive() &&
//                            entity.distanceTo(target) <= radius
//            );
//
//            // 对每个实体造成伤害
//            for (LivingEntity entity : entities) {
//                DamageSource damageSource = LHMiracleRoadTool.getDamageSource(player, DamageTypes.PLAYER_ATTACK);
//                entity.hurt(damageSource,shockwaveDamage);
//            }
//
//        }
    }

}
