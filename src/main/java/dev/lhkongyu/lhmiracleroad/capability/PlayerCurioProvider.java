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

public class PlayerCurioProvider implements ICapabilitySerializable<CompoundTag> {

    private PlayerCurio playerCurio;

    public static final Capability<PlayerCurio> PLAYER_CURIO_PROVIDER = CapabilityManager.get(new CapabilityToken<>() {});

    private final LazyOptional<PlayerCurio> lazyOptional = LazyOptional.of(() -> this.playerCurio);

    public PlayerCurioProvider(){
        playerCurio = new PlayerCurio();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_CURIO_PROVIDER){
            return lazyOptional.cast();
        }else {
            return LazyOptional.empty();
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        playerCurio.saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        playerCurio.loadNBTData(nbt);
    }
}
