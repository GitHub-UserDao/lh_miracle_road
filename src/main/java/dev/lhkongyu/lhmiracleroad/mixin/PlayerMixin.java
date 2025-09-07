package dev.lhkongyu.lhmiracleroad.mixin;

import dev.lhkongyu.lhmiracleroad.attributes.AttributeInstanceAccess;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Player.class)
public abstract class PlayerMixin {

//	@Inject(method = "createAttributes", at = @At("RETURN"))
//	private static void createAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
//		cir.getReturnValue()
//				.add(LHMiracleRoadAttributes.BURDEN)
//				.add(LHMiracleRoadAttributes.HEAVY)
//				.add(LHMiracleRoadAttributes.RANGED_DAMAGE)
//				.add(LHMiracleRoadAttributes.HEALING)
//				.add(LHMiracleRoadAttributes.HUNGER)
//				.add(LHMiracleRoadAttributes.INIT_DIFFICULTY_LEVEL)
//				.add(LHMiracleRoadAttributes.JUMP);
//	}

	@Inject(method = "getDigSpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F", at = @At("RETURN"), cancellable = true,remap = false)
	private void injectMiningSpeed(BlockState state, @Nullable BlockPos pos, CallbackInfoReturnable<Float> cir) {
		if (((LivingEntity) (Object) this) instanceof Player player) {
			var attribute = ((AttributeInstanceAccess) player.getAttribute(LHMiracleRoadAttributes.MINING_SPEED));
			cir.setReturnValue((float) attribute.computeIncreasedValueForInitial(cir.getReturnValueF()));
		}
	}

	@ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private float causeFoodExhaustionUpdate(float amount) {
		if (amount < 0) {
			return amount;
		}

		if (((LivingEntity) (Object) this) instanceof Player player) {
			var attribute = ((AttributeInstanceAccess) player.getAttribute(LHMiracleRoadAttributes.HUNGER));
			amount = (float) attribute.computeDecreasedValueForInitial(amount);
		}
		return amount;
	}

}
