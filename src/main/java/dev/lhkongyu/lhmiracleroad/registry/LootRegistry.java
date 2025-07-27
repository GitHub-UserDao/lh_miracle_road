package dev.lhkongyu.lhmiracleroad.registry;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.data.loot.RandomizeCurioEnhance;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class LootRegistry {

    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, LHMiracleRoad.MODID);

    public static final RegistryObject<LootItemFunctionType> RANDOMIZE_CURIO_ENHANCE = LOOT_FUNCTIONS.register("randomize_curio_enhance",
            () -> new LootItemFunctionType(new RandomizeCurioEnhance.Serializer()));

    public static void register(IEventBus eventBus) {
        LOOT_FUNCTIONS.register(eventBus);
    }

}
