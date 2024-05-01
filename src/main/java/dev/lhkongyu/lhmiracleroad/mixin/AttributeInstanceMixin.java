package dev.lhkongyu.lhmiracleroad.mixin;

import dev.lhkongyu.lhmiracleroad.access.AttributeInstanceAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;

@Mixin(AttributeInstance.class)
public abstract class AttributeInstanceMixin implements AttributeInstanceAccess {

	@Shadow public abstract double getBaseValue();

	@Shadow @Final private Attribute attribute;

	@Shadow protected abstract Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation p_22117_);

	@Override
	@Unique
	public double computeIncreasedValueForInitial(double initial) {
		double value1 = initial + this.getBaseValue();
		for (AttributeModifier modifier : this.getModifiersOrEmpty(AttributeModifier.Operation.ADDITION)) {
			value1 += modifier.getAmount();
		}
		double value2 = value1;
		for (AttributeModifier modifier : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_BASE)) {
			value2 += value1 * modifier.getAmount();
		}
		for (AttributeModifier modifier : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
			value2 *= 1.0 + modifier.getAmount();
		}
		return this.attribute.sanitizeValue(value2);
	}

	@Override
	@Unique
	public double computeDecreasedValueForInitial(double initial) {
		double value1 = initial - this.getBaseValue();
		for (AttributeModifier modifier : this.getModifiersOrEmpty(AttributeModifier.Operation.ADDITION)) {
			value1 -= modifier.getAmount();
		}
		double value2 = value1;
		for (AttributeModifier modifier : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_BASE)) {
			value2 -= value1 * modifier.getAmount();
		}
		for (AttributeModifier modifier : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
			value2 *= 1.0 - modifier.getAmount();
		}
		return this.attribute.sanitizeValue(value2);
	}
}
