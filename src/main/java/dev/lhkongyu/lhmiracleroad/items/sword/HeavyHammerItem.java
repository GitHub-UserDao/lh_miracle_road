package dev.lhkongyu.lhmiracleroad.items.sword;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.lhkongyu.lhmiracleroad.registry.EnchantmentRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeMod;

import java.util.Map;

public class HeavyHammerItem extends TieredItem {

    private final Multimap<Attribute, AttributeModifier> attributes;

    public HeavyHammerItem(Tier tier, int damage,float speed,Properties props) {
        super(tier, props);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder =
                ImmutableMultimap.builder();

        // 攻击伤害
        builder.put(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        BASE_ATTACK_DAMAGE_UUID,
                        "Weapon modifier",
                        damage-1,
                        AttributeModifier.Operation.ADDITION
                )
        );

        // 攻速
        builder.put(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        BASE_ATTACK_SPEED_UUID,
                        "Weapon modifier",
                        speed,
                        AttributeModifier.Operation.ADDITION
                )
        );

        // 攻击距离
        builder.put(
                ForgeMod.BLOCK_REACH.get(),
                new AttributeModifier(
                        java.util.UUID.randomUUID(),
                        "Reach modifier",
                        1.0,
                        AttributeModifier.Operation.ADDITION
                )
        );

        this.attributes = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {

        return slot == EquipmentSlot.MAINHAND
                ? this.attributes
                : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {

        // 通用允许

        if (enchantment == Enchantments.MENDING) {
            return true;
        }

        if (enchantment == Enchantments.UNBREAKING) {
            return true;
        }

//        if (enchantment == EnchantmentRegistry.SHOCKWAVE.get()) {
//            return true;
//        }

        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {

        Map<Enchantment, Integer> enchants =
                EnchantmentHelper.getEnchantments(book);

        for (Enchantment enchantment : enchants.keySet()) {

//            && enchantment != EnchantmentRegistry.SHOCKWAVE.get()
            if (enchantment != Enchantments.MENDING && enchantment != Enchantments.UNBREAKING ) {
                return false;
            }
        }

        return true;
    }
}