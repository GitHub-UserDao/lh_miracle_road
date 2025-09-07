package dev.lhkongyu.lhmiracleroad.packet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.client.screen.overlay.SoulHudOverlay;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public record ClientSoulMessage(JsonObject playerOccupationAttributeObject) {

    public ClientSoulMessage(FriendlyByteBuf buf){
        this(getPlayerOccupationAttributeObject(buf));
    }

    private static JsonObject getPlayerOccupationAttributeObject(FriendlyByteBuf buf){
        String jsonStr = buf.readUtf();
        return JsonParser.parseString(jsonStr).getAsJsonObject();
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeUtf(playerOccupationAttributeObject.toString());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel clientLevel = mc.level;
            if (clientLevel == null) return;
            String playerUUID = LHMiracleRoadTool.isAsString(playerOccupationAttributeObject.get("playerUUID"));
            Player clientPlayer = clientLevel.getPlayerByUUID(UUID.fromString(playerUUID));
            if (clientPlayer != null) {

                Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = clientPlayer.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
                if (optionalPlayerOccupationAttribute.isEmpty()) return;
                PlayerOccupationAttribute playerOccupationAttribute = optionalPlayerOccupationAttribute.get();

                int occupationExperience = LHMiracleRoadTool.isAsInt(playerOccupationAttributeObject.get("occupationExperience"));
                playerOccupationAttribute.setOccupationExperience(occupationExperience);

                int soulStart = LHMiracleRoadTool.isAsInt(playerOccupationAttributeObject.get("soulStart"));

                SoulHudOverlay.obtainSoul(soulStart,occupationExperience);
            }
        });
        context.setPacketHandled(true);
    }
}
