package dev.lhkongyu.lhmiracleroad.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
//    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
//    public void init(ItemLike itemLike, int count, CompoundTag capNBT, CallbackInfo ci) {
//        if (itemLike != null && itemLike.asItem() instanceof CustomizeItemInit customizeItemInit) {
//            customizeItemInit.initItem((ItemStack) (Object) this);
//        }
//    }
    @Inject(
            method = "getMaxDamage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectCustomMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack itemStack = ((ItemStack) (Object) this);
        CompoundTag tag = itemStack.getOrCreateTag().getCompound("lh_gem");
        if (tag.contains("max_durability")) {
            cir.setReturnValue(tag.getInt("max_durability"));
        }
    }
}