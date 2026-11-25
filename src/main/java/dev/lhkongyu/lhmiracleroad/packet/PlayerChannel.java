package dev.lhkongyu.lhmiracleroad.packet;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.config.editor.ConfigEditorPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PlayerChannel {

    private static final String PROTOCOL_VERSION = "11";

    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LHMiracleRoad.MODID, "lh_miracle_road_player_attribute"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
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

        channel.messageBuilder(AbyssbindActivatePacket.class, 7,NetworkDirection.PLAY_TO_CLIENT)
                .encoder(AbyssbindActivatePacket::encode)
                .decoder(AbyssbindActivatePacket::new)
                .consumerMainThread(AbyssbindActivatePacket::handle)
                .add();

        channel.messageBuilder(PlayerAttributeValuesRequest.class, 8, NetworkDirection.PLAY_TO_SERVER)
                .decoder(PlayerAttributeValuesRequest::new)
                .encoder(PlayerAttributeValuesRequest::encode)
                .consumerMainThread(PlayerAttributeValuesRequest::handle)
                .add();

        channel.messageBuilder(PlayerAttributeValuesSnapshot.class, 9, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlayerAttributeValuesSnapshot::new)
                .encoder(PlayerAttributeValuesSnapshot::encode)
                .consumerMainThread(PlayerAttributeValuesSnapshot::handle)
                .add();

        channel.messageBuilder(InitItemPreviewMessage.class, 10, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(InitItemPreviewMessage::new)
                .encoder(InitItemPreviewMessage::encode)
                .consumerMainThread(InitItemPreviewMessage::handle)
                .add();

        channel.messageBuilder(OccupationCatalogMessage.class, 11, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OccupationCatalogMessage::new)
                .encoder(OccupationCatalogMessage::encode)
                .consumerMainThread(OccupationCatalogMessage::handle)
                .add();

        channel.messageBuilder(WeaponTypesMessage.class, 12, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(WeaponTypesMessage::new)
                .encoder(WeaponTypesMessage::encode)
                .consumerMainThread(WeaponTypesMessage::handle)
                .add();

        ConfigEditorPackets.register(channel);
    }

    public static <MSG> void sendToServer(MSG msg) {
        channel.sendToServer(msg);
    }

    public static <MSG> void sendToClient(MSG msg, ServerPlayer serverPlayer) {
        channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer),msg);
    }
}
