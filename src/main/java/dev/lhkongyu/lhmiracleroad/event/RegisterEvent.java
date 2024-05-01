package dev.lhkongyu.lhmiracleroad.event;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.access.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.ItemStackPunishmentAttribute;
import dev.lhkongyu.lhmiracleroad.capability.ItemStackPunishmentAttributeProvider;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.world.item.WeaponItem;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ModRegisterEvent {

//        @SubscribeEvent
//        public static void onCommonSetup(FMLCommonSetupEvent event) {
//            System.err.println(1);
//        }
    }

}
