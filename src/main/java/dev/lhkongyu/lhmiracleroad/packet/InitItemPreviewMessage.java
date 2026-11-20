package dev.lhkongyu.lhmiracleroad.packet;

import dev.lhkongyu.lhmiracleroad.config.initItem.InitItemConfig;
import dev.lhkongyu.lhmiracleroad.config.initItem.StarterItemPreview;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.function.Supplier;

public record InitItemPreviewMessage(boolean replaceAll, Map<String, StarterItemPreview> items) {
    public InitItemPreviewMessage(FriendlyByteBuf buffer) {
        this(buffer.readBoolean(), buffer.readMap(FriendlyByteBuf::readUtf,
                buf -> new StarterItemPreview(
                        buf.readMap(b -> b.readEnum(EquipmentSlot.class), InitItemPreviewMessage::readStack),
                        buf.readList(InitItemPreviewMessage::readStack))));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(replaceAll);
        buffer.writeMap(items, FriendlyByteBuf::writeUtf, (buf, preview) -> {
            buf.writeMap(preview.equipment(), FriendlyByteBuf::writeEnum, InitItemPreviewMessage::writeStack);
            buf.writeCollection(preview.inventory(), InitItemPreviewMessage::writeStack);
        });
    }

    private static void writeStack(FriendlyByteBuf buf, ItemStack stack) {
        // Vanilla's item packet uses a byte count; a configured quantity can be larger.
        buf.writeItemStack(stack.copyWithCount(1), false);
        buf.writeVarInt(stack.getCount());
    }

    private static ItemStack readStack(FriendlyByteBuf buf) {
        return buf.readItem().copyWithCount(buf.readVarInt());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        if (replaceAll) ClientData.STARTER_ITEM_PREVIEWS.clear();
        ClientData.STARTER_ITEM_PREVIEWS.putAll(items);
        context.get().setPacketHandled(true);
    }

    public static void sendAll(ServerPlayer player) {
        PlayerChannel.sendToClient(new InitItemPreviewMessage(true, InitItemConfig.previewItems()), player);
    }

    public static void broadcastUpdate(String occupationId) {
        PlayerChannel.channel.send(PacketDistributor.ALL.noArg(), new InitItemPreviewMessage(false,
                Map.of(occupationId, InitItemConfig.previewItems(occupationId))));
    }
}
