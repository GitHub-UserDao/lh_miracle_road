package dev.lhkongyu.lhmiracleroad.registry;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class TabsRegistry {
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LHMiracleRoad.MODID);

	public static final RegistryObject<CreativeModeTab> ITEMS = TABS.register("items",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.lhmiracleroad.items"))
					.icon(() -> new ItemStack(ItemsRegistry.DEATH_SOUL.get()))
					.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
					.hideTitle()
					.displayItems((params, output) -> {
						ItemsRegistry.ITEMS.getEntries().forEach(it -> {
							output.accept(it.get());
						});
					})
					.build());

	public static void register(IEventBus eventBus) {
		TABS.register(eventBus);
	}
}