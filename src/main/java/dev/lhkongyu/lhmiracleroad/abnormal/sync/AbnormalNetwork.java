package dev.lhkongyu.lhmiracleroad.abnormal.sync;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class AbnormalNetwork {

    private static final String VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LHMiracleRoad.MODID, "lh_miracle_abnormal"),
                    () -> VERSION,
                    VERSION::equals,
                    VERSION::equals
            );

    private static int id = 0;

    public static void register() {
        CHANNEL.registerMessage(id++, SyncAbnormalPacket.class, SyncAbnormalPacket::encode, SyncAbnormalPacket::decode, SyncAbnormalPacket::handle);
    }
}
