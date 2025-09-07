package dev.lhkongyu.lhmiracleroad.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public class SpanningWingsEffect extends MobEffect {
    protected final double multiplier;

    public SpanningWingsEffect(MobEffectCategory category, int color, double multiplier) {
        super(category, color);
        this.multiplier = multiplier;
    }

    public double getAttributeModifierValue(int p_19430_, @NotNull AttributeModifier attributeModifier) {
        return this.multiplier * (double)(p_19430_ + 1);
    }
}
