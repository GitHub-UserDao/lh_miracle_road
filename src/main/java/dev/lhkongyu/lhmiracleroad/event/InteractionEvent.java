package dev.lhkongyu.lhmiracleroad.event;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.items.gem.StrengthenGem;
import dev.lhkongyu.lhmiracleroad.packet.ClientPromptMessage;
import dev.lhkongyu.lhmiracleroad.packet.PlayerChannel;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.registry.TagsRegistry;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractionEvent {

}
