package dev.lhkongyu.lhmiracleroad.event.client;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.client.screen.occupation.OccupationChooseScreen;
import dev.lhkongyu.lhmiracleroad.client.screen.occupation.UpgradeInterfaceScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

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
            if (player == null || minecraft.screen != null || event.getAction() != GLFW.GLFW_PRESS
                    || !LH_MIRACLE_ROAD_TREE_KEY.matches(event.getKey(), event.getScanCode())) return;

            boolean hasOccupation = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER)
                    .map(attributes -> attributes.getOccupationId() != null && !attributes.getOccupationId().isBlank())
                    .orElse(false);
            minecraft.setScreen(hasOccupation ? new UpgradeInterfaceScreen() : new OccupationChooseScreen());
        }
    }
}
