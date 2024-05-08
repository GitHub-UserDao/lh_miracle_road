package dev.lhkongyu.lhmiracleroad.event;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.ItemStackPunishmentAttributeProvider;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.command.GetPlayerOccupationLevelCommand;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * register event 注册事件
 */
@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterEvent {

    /**
     * 注册能力事件
     * @param event
     */
    @SubscribeEvent
    public static void registerCapability(RegisterCapabilitiesEvent event){
        event.register(PlayerOccupationAttributeProvider.class);
        event.register(ItemStackPunishmentAttributeProvider.class);
    }


    /**
     * 设置实体能力事件
     * @param event
     */
    @SubscribeEvent
    public static void setUpEntityCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof  Player){
            event.addCapability(new ResourceLocation(LHMiracleRoad.MODID, "player_occupation_attribute"),new PlayerOccupationAttributeProvider());
        }
    }

    /**
     * 注册指令
     * @param event
     */
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event){
        GetPlayerOccupationLevelCommand.register(event.getDispatcher());
    }

    /**
     * 同步数据包事件
     * @param event
     */
    @SubscribeEvent
    public static void onDataPackSync(final OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            LHMiracleRoadTool.synchronizationClientData(event.getPlayer());
        } else {
            event.getPlayerList().getPlayers().forEach(LHMiracleRoadTool::synchronizationClientData);
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ModRegisterEvent {

//        @SubscribeEvent
//        public static void onCommonSetup(FMLCommonSetupEvent event) {
//            System.err.println(1);
//        }
    }

}
