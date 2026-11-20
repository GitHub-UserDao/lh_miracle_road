package dev.lhkongyu.lhmiracleroad.packet;

import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import dev.lhkongyu.lhmiracleroad.tool.SyncTool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PlayerAttributePointsMessage(String attributeTypeName, int count) {

    public PlayerAttributePointsMessage(FriendlyByteBuf buf){
        this(buf.readUtf(), buf.readVarInt());
    }
    public void encode(FriendlyByteBuf buf){
        buf.writeUtf(attributeTypeName);
        buf.writeVarInt(count);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || (count != 1 && count != 10)) return;
            PlayerAttributeTool.points(player, attributeTypeName, count);

            player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER)
                    .ifPresent(attributes -> SyncTool.synchronizationClient(attributes, player));
        });
        context.setPacketHandled(true);
    }
}
