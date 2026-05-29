package dev.lhkongyu.lhmiracleroad.enchantments;

import dev.lhkongyu.lhmiracleroad.registry.EnchantmentRegistry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
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
        return 3;
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
        return true;
    }

    public static void shockwave(LivingEntity target, Player player, ItemStack weapon, float damage){

    }

}
