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

public class PlayerOccupationAttributeProvider implements ICapabilitySerializable<CompoundTag> {

    private PlayerOccupationAttribute playerOccupationAttribute;

    public static final Capability<PlayerOccupationAttribute> PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER = CapabilityManager.get(new CapabilityToken<>() {});

    private final LazyOptional<PlayerOccupationAttribute> lazyOptional = LazyOptional.of(() -> this.playerOccupationAttribute);

    public PlayerOccupationAttributeProvider(){
        playerOccupationAttribute = new PlayerOccupationAttribute();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER){
            return lazyOptional.cast();
        }else {
            return LazyOptional.empty();
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        playerOccupationAttribute.saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        playerOccupationAttribute.loadNBTData(nbt);
    }
}
