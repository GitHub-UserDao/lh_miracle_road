package dev.lhkongyu.lhmiracleroad.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemDurabilityBarMixin {
    @Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
    private void lh_miracle_road$barWidth(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        float remaining = lh_miracle_road$remainingRatio(stack);
        if (!Float.isNaN(remaining)) cir.setReturnValue(Math.round(remaining * 13.0F));
    }

    @Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true)
    private void lh_miracle_road$barColor(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        float remaining = lh_miracle_road$remainingRatio(stack);
        if (!Float.isNaN(remaining)) cir.setReturnValue(Mth.hsvToRgb(remaining / 3.0F, 1.0F, 1.0F));
    }

    @Unique
    private static float lh_miracle_road$remainingRatio(ItemStack stack) {
        CompoundTag gem = stack.getTagElement("lh_gem");
        if (gem == null || !gem.contains("max_durability", Tag.TAG_ANY_NUMERIC)) return Float.NaN;
        int max = stack.getMaxDamage();
        return max > 0 ? Mth.clamp(1.0F - stack.getDamageValue() / (float) max, 0.0F, 1.0F) : Float.NaN;
    }
}
