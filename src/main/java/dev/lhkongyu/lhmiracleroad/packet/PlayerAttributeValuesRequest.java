package dev.lhkongyu.lhmiracleroad.packet;

import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.tool.SyncTool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PlayerAttributeValuesRequest() {

    public PlayerAttributeValuesRequest(FriendlyByteBuf buffer) {
        this();
    }

    public void encode(FriendlyByteBuf buffer) {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        if (player != null) {
            player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER)
                    .ifPresent(attributes -> SyncTool.synchronizationClient(attributes, player));
            PlayerAttributeValuesSnapshot.send(player);
        }
        context.setPacketHandled(true);
    }
}
