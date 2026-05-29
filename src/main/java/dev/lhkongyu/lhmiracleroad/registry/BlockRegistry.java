package dev.lhkongyu.lhmiracleroad.registry;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.block.WeaponPodium;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS,  LHMiracleRoad.MODID);

    public static final RegistryObject<Block> WEAPON_PODIUM = registryBlock("weapon_podium", WeaponPodium::new);


    private static <T extends Block> void registryBlockItems(String name,RegistryObject<T> block){
        ItemsRegistry.ITEMS.register(name,() -> new BlockItem(block.get(),new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> registryBlock(String name, Supplier<T> block){
        RegistryObject<T> blocks = BLOCKS.register(name,block);
        registryBlockItems(name,blocks);
        return blocks;
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
