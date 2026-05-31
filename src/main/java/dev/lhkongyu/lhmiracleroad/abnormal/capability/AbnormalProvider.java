package dev.lhkongyu.lhmiracleroad.abnormal.capability;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class AbnormalProvider implements ICapabilityProvider {

    public static final Capability<IAbnormalCapability> CAPABILITY =
            CapabilityManager.get(
                    new CapabilityToken<>() {}
            );

    private final LazyOptional<IAbnormalCapability> optional =
            LazyOptional.of(AbnormalCapability::new);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {

        return cap == CAPABILITY
                ? optional.cast()
                : LazyOptional.empty();
    }
}
