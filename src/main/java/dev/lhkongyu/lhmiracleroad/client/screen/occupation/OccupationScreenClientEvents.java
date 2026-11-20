package dev.lhkongyu.lhmiracleroad.client.screen.occupation;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OccupationScreenClientEvents {

    public static final KeyMapping OCCUPATION_CHOOSE_KEY =
            new KeyMapping(
                    "key.display_occupation_choose", GLFW.GLFW_KEY_J, "key.categories." + LHMiracleRoad.MODID);

    public static final KeyMapping UPGRADE_INTERFACE_KEY =
            new KeyMapping(
                    "key.display_upgrade_interface", GLFW.GLFW_KEY_G, "key.categories." + LHMiracleRoad.MODID);

    @SubscribeEvent
    public static void registerKeyMappingsEvent(RegisterKeyMappingsEvent event) {
        event.register(OCCUPATION_CHOOSE_KEY);
        event.register(UPGRADE_INTERFACE_KEY);
    }

    @Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID, value = Dist.CLIENT)
    private static class InputEvents {

        @SubscribeEvent
        public static void inputEvent(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || minecraft.screen != null || event.getAction() != GLFW.GLFW_PRESS) return;

            if (event.getKey() == OCCUPATION_CHOOSE_KEY.getKey().getValue()) {
                minecraft.setScreen(new OccupationChooseScreen());
                return;
            }

            if (event.getKey() == UPGRADE_INTERFACE_KEY.getKey().getValue()) {
                minecraft.setScreen(new UpgradeInterfaceScreen());
            }
        }
    }
}
