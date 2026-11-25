package dev.lhkongyu.lhmiracleroad.config.editor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.client.screen.config.ConfigEditorScreen;
import dev.lhkongyu.lhmiracleroad.client.screen.weaponPodium.WeaponPodiumMenu;
import dev.lhkongyu.lhmiracleroad.config.strengthen.StrengthenConfig;
import dev.lhkongyu.lhmiracleroad.items.trophy.TrophyManager;
import dev.lhkongyu.lhmiracleroad.packet.PlayerChannel;
import dev.lhkongyu.lhmiracleroad.packet.ClientDataMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public final class ConfigEditorPackets {
    private static final int CHUNK_BYTES = 24000;
    static final int DOCUMENT_BYTES = 512000;
    private static final int GAMEPLAY_BYTES = 900000;
    private static final int MAX_CHUNKS = (DOCUMENT_BYTES + CHUNK_BYTES - 1) / CHUNK_BYTES;
    private static final Map<ServerPlayer, Upload> UPLOADS = new WeakHashMap<>();

    private ConfigEditorPackets() {}

    public static void register(SimpleChannel channel) {
        channel.messageBuilder(Request.class, 13, NetworkDirection.PLAY_TO_SERVER)
                .decoder(Request::new).encoder(Request::encode).consumerMainThread(Request::handle).add();
        channel.messageBuilder(EditChunk.class, 14, NetworkDirection.PLAY_TO_SERVER)
                .decoder(EditChunk::new).encoder(EditChunk::encode).consumerMainThread(EditChunk::handle).add();
        channel.messageBuilder(Response.class, 15, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(Response::new).encoder(Response::encode).consumerMainThread(Response::handle).add();
        channel.messageBuilder(GameplaySnapshot.class, 16, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(GameplaySnapshot::new).encoder(GameplaySnapshot::encode)
                .consumerMainThread(GameplaySnapshot::handle).add();
    }

    public static void request(ConfigSection section) {
        PlayerChannel.sendToServer(new Request(section, false, ""));
    }

    public static void restore(ConfigSection section, String revision) {
        PlayerChannel.sendToServer(new Request(section, true, revision));
    }

    public static void save(ConfigSection section, String revision, JsonObject document) {
        byte[] bytes = jsonBytes(document);
        if (bytes.length > DOCUMENT_BYTES) {
            receiveOnClient(section, "", "", "配置超过网络传输上限 / Configuration exceeds the transfer limit.", false);
            return;
        }
        UUID id = UUID.randomUUID();
        int count = (bytes.length + CHUNK_BYTES - 1) / CHUNK_BYTES;
        for (int index = 0; index < count; index++) {
            int start = index * CHUNK_BYTES;
            PlayerChannel.sendToServer(new EditChunk(section, revision, id, index, count,
                    Arrays.copyOfRange(bytes, start, Math.min(start + CHUNK_BYTES, bytes.length))));
        }
    }

    static void respond(ServerPlayer player, ConfigSection section, ConfigEditorStorage.Snapshot snapshot,
                        String status, boolean open) {
        byte[] bytes = snapshot == null ? new byte[0] : jsonBytes(snapshot.document());
        if (bytes.length > DOCUMENT_BYTES) {
            snapshot = null;
            bytes = new byte[0];
            status = "配置超过网络传输上限 / Configuration exceeds the transfer limit.";
        }
        PlayerChannel.sendToClient(new Response(section, snapshot == null ? "" : snapshot.revision(),
                status.substring(0, Math.min(status.length(), 1024)), bytes, open), player);
    }

    public static void syncGameplay(ServerPlayer player, ConfigSection section, JsonObject data) {
        byte[] bytes = jsonBytes(data);
        if (bytes.length <= GAMEPLAY_BYTES) {
            PlayerChannel.sendToClient(new GameplaySnapshot(section, bytes), player);
        } else {
            LHMiracleRoad.LOGGER.warn("Cannot send oversized {} configuration snapshot ({} bytes)", section, bytes.length);
        }
    }

    private static byte[] jsonBytes(JsonObject document) {
        return document.toString().getBytes(StandardCharsets.UTF_8);
    }

    private record Request(ConfigSection section, boolean restore, String revision) {
        Request(FriendlyByteBuf buf) {
            this(buf.readEnum(ConfigSection.class), buf.readBoolean(), buf.readUtf(128));
        }

        void encode(FriendlyByteBuf buf) {
            buf.writeEnum(section).writeBoolean(restore);
            buf.writeUtf(revision, 128);
        }

        void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context context = supplier.get();
            ServerPlayer player = context.getSender();
            if (player != null && player.hasPermissions(2)) {
                if (restore) ConfigEditorService.restore(player, section, revision);
                else ConfigEditorService.read(player, section, false);
            } else if (player != null) {
                respond(player, section, null, "需要管理员权限 / Operator permission required.", false);
            }
            context.setPacketHandled(true);
        }
    }

    private record EditChunk(ConfigSection section, String revision, UUID id, int index, int count, byte[] bytes) {
        EditChunk(FriendlyByteBuf buf) {
            this(buf.readEnum(ConfigSection.class), buf.readUtf(128), buf.readUUID(),
                    buf.readVarInt(), buf.readVarInt(), buf.readByteArray(CHUNK_BYTES));
        }

        void encode(FriendlyByteBuf buf) {
            buf.writeEnum(section).writeUtf(revision, 128).writeUUID(id).writeVarInt(index).writeVarInt(count);
            buf.writeByteArray(bytes);
        }

        void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context context = supplier.get();
            ServerPlayer player = context.getSender();
            if (player != null && player.hasPermissions(2)) {
                accept(player);
            } else if (player != null && index == count - 1) {
                UPLOADS.remove(player);
                respond(player, section, null, "需要管理员权限 / Operator permission required.", false);
            }
            context.setPacketHandled(true);
        }

        private void accept(ServerPlayer player) {
            if (count < 1 || count > MAX_CHUNKS || index < 0 || index >= count) return;
            long now = System.nanoTime();
            if (index == 0) UPLOADS.put(player, new Upload(section, revision, id, count, now));
            Upload upload = UPLOADS.get(player);
            if (upload == null || !upload.matches(this, now)) {
                UPLOADS.remove(player);
                respond(player, section, null, "传输中断 / Incomplete edit transfer.", false);
                return;
            }
            upload.document.writeBytes(bytes);
            if (++upload.nextIndex == count) {
                UPLOADS.remove(player);
                ConfigEditorService.save(player, section, revision, upload.document.toString(StandardCharsets.UTF_8));
            }
        }
    }

    private static final class Upload {
        private final ConfigSection section;
        private final String revision;
        private final UUID id;
        private final int count;
        private final long startTime;
        private final ByteArrayOutputStream document = new ByteArrayOutputStream();
        private int nextIndex;

        Upload(ConfigSection section, String revision, UUID id, int count, long startTime) {
            this.section = section;
            this.revision = revision;
            this.id = id;
            this.count = count;
            this.startTime = startTime;
        }

        boolean matches(EditChunk chunk, long now) {
            return now - startTime < 30_000_000_000L && section == chunk.section && count == chunk.count
                    && id.equals(chunk.id) && revision.equals(chunk.revision) && nextIndex == chunk.index
                    && document.size() + chunk.bytes.length <= DOCUMENT_BYTES;
        }
    }

    private static void receiveOnClient(ConfigSection section, String revision, String json, String status, boolean open) {
        ConfigEditorScreen.receive(section, revision, json, status, open);
    }

    private record Response(ConfigSection section, String revision, String status, byte[] bytes, boolean open) {
        Response(FriendlyByteBuf buf) {
            this(buf.readEnum(ConfigSection.class), buf.readUtf(128), buf.readUtf(1024),
                    buf.readByteArray(DOCUMENT_BYTES), buf.readBoolean());
        }

        void encode(FriendlyByteBuf buf) {
            buf.writeEnum(section).writeUtf(revision, 128).writeUtf(status, 1024).writeByteArray(bytes);
            buf.writeBoolean(open);
        }

        void handle(Supplier<NetworkEvent.Context> supplier) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> receiveOnClient(
                    section, revision, new String(bytes, StandardCharsets.UTF_8), status, open));
            supplier.get().setPacketHandled(true);
        }
    }

    private record GameplaySnapshot(ConfigSection section, byte[] bytes) {
        GameplaySnapshot(FriendlyByteBuf buf) {
            this(buf.readEnum(ConfigSection.class), buf.readByteArray(GAMEPLAY_BYTES));
        }

        void encode(FriendlyByteBuf buf) {
            buf.writeEnum(section).writeByteArray(bytes);
        }

        void handle(Supplier<NetworkEvent.Context> supplier) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                JsonObject data = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
                if (section == ConfigSection.EQUIPMENT) ClientDataMessage.installEquipmentSnapshot(data);
                if (section == ConfigSection.TROPHY) TrophyManager.setClientSnapshot(data);
                if (section == ConfigSection.STRENGTHEN) {
                    StrengthenConfig.setClientSnapshot(data);
                    var player = Minecraft.getInstance().player;
                    if (player != null && player.containerMenu instanceof WeaponPodiumMenu podium) podium.createResult();
                }
            });
            supplier.get().setPacketHandled(true);
        }
    }
}
