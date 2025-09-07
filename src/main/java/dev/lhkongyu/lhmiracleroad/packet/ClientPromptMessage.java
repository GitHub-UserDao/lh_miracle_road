package dev.lhkongyu.lhmiracleroad.packet;

import dev.lhkongyu.lhmiracleroad.event.client.HudEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientPromptMessage(String text){

    public ClientPromptMessage(FriendlyByteBuf buf){
        this(decode(buf));
    }

    public static String decode(FriendlyByteBuf buf) {
        return buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeUtf(text);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() ->
                HudEvents.show(text)
        );
        supplier.get().setPacketHandled(true);
    }
}
