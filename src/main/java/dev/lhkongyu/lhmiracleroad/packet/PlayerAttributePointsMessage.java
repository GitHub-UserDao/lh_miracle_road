package dev.lhkongyu.lhmiracleroad.packet;

import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PlayerAttributePointsMessage(String attributeTypeName) {

    public PlayerAttributePointsMessage(FriendlyByteBuf buf){
        this(buf.readUtf());
    }
    public void encode(FriendlyByteBuf buf){
        buf.writeUtf(attributeTypeName);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            PlayerAttributeTool.points(player, attributeTypeName);

            PlayerOccupationAttribute playerOccupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve().get();
            //设置一下在gui渲染的属性
//            playerOccupationAttribute.setShowAttribute(LHMiracleRoadTool.setShowAttribute(player));
            LHMiracleRoadTool.synchronizationClient(playerOccupationAttribute, player);
            LHMiracleRoadTool.synchronizationShowAttribute(player);
        });
        context.setPacketHandled(true);
    }
}
