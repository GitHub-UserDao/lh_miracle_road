package dev.lhkongyu.lhmiracleroad.registry;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.enchantments.ShockwaveEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnchantmentRegistry {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(
                    ForgeRegistries.ENCHANTMENTS,
                    LHMiracleRoad.MODID
            );

//    public static final RegistryObject<Enchantment> SHOCKWAVE = ENCHANTMENTS.register("shockwave", ShockwaveEnchantment::new);

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
