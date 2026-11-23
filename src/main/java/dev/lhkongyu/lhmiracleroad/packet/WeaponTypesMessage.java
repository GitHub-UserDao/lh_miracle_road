package dev.lhkongyu.lhmiracleroad.packet;

import dev.lhkongyu.lhmiracleroad.config.strengthen.WeaponTypeConfig.WeaponTypes;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.function.Supplier;

public record WeaponTypesMessage(WeaponTypes types) {
    public WeaponTypesMessage(FriendlyByteBuf buffer) {
        this(new WeaponTypes(
                buffer.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation),
                buffer.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation),
                buffer.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation)));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeCollection(types.melee(), FriendlyByteBuf::writeResourceLocation);
        buffer.writeCollection(types.ranged(), FriendlyByteBuf::writeResourceLocation);
        buffer.writeCollection(types.magicStaff(), FriendlyByteBuf::writeResourceLocation);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ClientData.setWeaponTypes(types);
        context.get().setPacketHandled(true);
    }
}
