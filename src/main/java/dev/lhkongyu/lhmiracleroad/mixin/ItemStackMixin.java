package dev.lhkongyu.lhmiracleroad.mixin;

import dev.lhkongyu.lhmiracleroad.entity.CustomizeItemInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    public void init(ItemLike itemLike, int count, CompoundTag capNBT, CallbackInfo ci) {
        if (itemLike != null && itemLike.asItem() instanceof CustomizeItemInit customizeItemInit) {
            customizeItemInit.initItem((ItemStack) (Object) this);
        }
    }
}