package dev.lhkongyu.lhmiracleroad.effect;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public class HunterMarkEffect extends MobEffect {

    protected final double multiplier;

    public HunterMarkEffect(MobEffectCategory category, int color, double multiplier) {
        super(category, color);
        this.multiplier = multiplier;
    }

    public double getAttributeModifierValue(int p_19430_, @NotNull AttributeModifier attributeModifier) {
        return this.multiplier * (double)(p_19430_ + 1);
    }
}
