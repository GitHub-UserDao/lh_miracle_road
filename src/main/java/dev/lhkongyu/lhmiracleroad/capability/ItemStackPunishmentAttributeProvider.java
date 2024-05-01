package dev.lhkongyu.lhmiracleroad.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStackPunishmentAttributeProvider implements ICapabilitySerializable<CompoundTag> {

    private ItemStackPunishmentAttribute itemStackPunishmentAttribute;

    public static final Capability<ItemStackPunishmentAttribute> ITEM_STACK_PUNISHMENT_ATTRIBUTE_PROVIDER = CapabilityManager.get(new CapabilityToken<>() {});

    private final LazyOptional<ItemStackPunishmentAttribute> lazyOptional = LazyOptional.of(() -> this.itemStackPunishmentAttribute);

    public ItemStackPunishmentAttributeProvider(){
        itemStackPunishmentAttribute = new ItemStackPunishmentAttribute();
    }

    public ItemStackPunishmentAttributeProvider(ItemStackPunishmentAttribute itemStackPunishmentAttribute){
        this.itemStackPunishmentAttribute = itemStackPunishmentAttribute;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ITEM_STACK_PUNISHMENT_ATTRIBUTE_PROVIDER){
            return lazyOptional.cast();
        }else {
            return LazyOptional.empty();
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        itemStackPunishmentAttribute.saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        itemStackPunishmentAttribute.loadNBTData(nbt);
    }
}
