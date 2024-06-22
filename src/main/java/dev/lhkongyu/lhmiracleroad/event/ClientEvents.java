package dev.lhkongyu.lhmiracleroad.event;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributeChannel;
import dev.lhkongyu.lhmiracleroad.packet.PlayerOccupationMessage;
import dev.lhkongyu.lhmiracleroad.screen.LHMiracleRoadMainScreen;
import dev.lhkongyu.lhmiracleroad.screen.LHMiracleRoadOccupationScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    public static final KeyMapping LH_MIRACLE_ROAD_TREE_KEY =
            new KeyMapping(
                    "key.display_miracle_road", GLFW.GLFW_KEY_H, "key.categories." + LHMiracleRoad.MODID);

    @SubscribeEvent
    public static void registerKeyMappingsEvent(RegisterKeyMappingsEvent event) {
        event.register(LH_MIRACLE_ROAD_TREE_KEY);
    }

    @Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID, value = Dist.CLIENT)
    private static class InputEvents {
        @SubscribeEvent
        public static void inputEvent(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player == null || minecraft.screen != null) return;
            if (event.getKey() == LH_MIRACLE_ROAD_TREE_KEY.getKey().getValue()) {
                PlayerAttributeChannel.sendToServer(new PlayerOccupationMessage(""));
                Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
                if (optionalPlayerOccupationAttribute.isPresent()) {
                    PlayerOccupationAttribute playerOccupationAttribute = optionalPlayerOccupationAttribute.get();
                    if (playerOccupationAttribute.getOccupationId() != null && !playerOccupationAttribute.getOccupationId().isEmpty()) {
                        minecraft.setScreen(new LHMiracleRoadMainScreen(1));
                        return;
                    }
                }
                minecraft.setScreen(new LHMiracleRoadOccupationScreen());
            }
        }
    }
}
