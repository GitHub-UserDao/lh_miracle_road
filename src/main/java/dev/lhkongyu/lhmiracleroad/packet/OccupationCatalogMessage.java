package dev.lhkongyu.lhmiracleroad.packet;

import com.google.gson.Gson;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationConfig;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

public record OccupationCatalogMessage(boolean replaceAll, List<Occupation> occupations) {
    private static final Gson GSON = new Gson();

    public OccupationCatalogMessage(FriendlyByteBuf buffer) {
        this(buffer.readBoolean(), buffer.readList(buf ->
                GSON.fromJson(new String(buf.readByteArray(), StandardCharsets.UTF_8), Occupation.class)));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(replaceAll);
        buffer.writeCollection(occupations, (buf, occupation) ->
                buf.writeByteArray(GSON.toJson(occupation).getBytes(StandardCharsets.UTF_8)));
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        if (replaceAll) ClientData.OCCUPATION_CATALOG.clear();
        occupations.forEach(occupation -> ClientData.OCCUPATION_CATALOG.put(occupation.getId(), occupation));
        context.get().setPacketHandled(true);
    }

    public static void sendAll(ServerPlayer player) {
        PlayerChannel.sendToClient(new OccupationCatalogMessage(true, OccupationConfig.getOccupations()), player);
    }

    public static void broadcastNew(Occupation occupation) {
        PlayerChannel.channel.send(PacketDistributor.ALL.noArg(), new OccupationCatalogMessage(false, List.of(occupation)));
    }
}
