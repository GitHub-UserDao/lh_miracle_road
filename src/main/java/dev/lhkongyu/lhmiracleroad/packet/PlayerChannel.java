package dev.lhkongyu.lhmiracleroad.packet;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PlayerChannel {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LHMiracleRoad.MODID, "lh_miracle_road_player_attribute"),
            () -> PROTOCOL_VERSION,
            s-> true,
            s-> true
    );

    public static void register(){
        channel.messageBuilder(PlayerOccupationMessage.class,1,NetworkDirection.PLAY_TO_SERVER)
                .decoder(PlayerOccupationMessage::new)
                .encoder(PlayerOccupationMessage::encode)
                .consumerMainThread(PlayerOccupationMessage::handle)
                .add();

        channel.messageBuilder(PlayerAttributePointsMessage.class,2,NetworkDirection.PLAY_TO_SERVER)
                .decoder(PlayerAttributePointsMessage::new)
                .encoder(PlayerAttributePointsMessage::encode)
                .consumerMainThread(PlayerAttributePointsMessage::handle)
                .add();

        channel.messageBuilder(ClientOccupationMessage.class,3,NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientOccupationMessage::new)
                .encoder(ClientOccupationMessage::encode)
                .consumerMainThread(ClientOccupationMessage::handle)
                .add();

        channel.messageBuilder(ClientDataMessage.class,4,NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientDataMessage::new)
                .encoder(ClientDataMessage::encode)
                .consumerMainThread(ClientDataMessage::handle)
                .add();

        channel.messageBuilder(ClientSoulMessage.class,5,NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientSoulMessage::new)
                .encoder(ClientSoulMessage::encode)
                .consumerMainThread(ClientSoulMessage::handle)
                .add();

        channel.messageBuilder(ClientPromptMessage.class,6,NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientPromptMessage::new)
                .encoder(ClientPromptMessage::encode)
                .consumerMainThread(ClientPromptMessage::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG msg) {
        channel.sendToServer(msg);
    }

    public static <MSG> void sendToClient(MSG msg, ServerPlayer serverPlayer) {
        channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer),msg);
    }
}
