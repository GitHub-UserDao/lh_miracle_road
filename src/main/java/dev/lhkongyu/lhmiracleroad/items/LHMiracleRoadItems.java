package dev.lhkongyu.lhmiracleroad.items;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LHMiracleRoadItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LHMiracleRoad.MODID);

    public static final RegistryObject<Item> BROKEN_SOUL = ITEMS.register("broken_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> GROUP_SOUL = ITEMS.register("group_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> EVIL_SOUL = ITEMS.register("evil_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> EXTREMELY_EVIL_SOUL = ITEMS.register("extremely_evil_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> KING_SOUL = ITEMS.register("king_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> FORGET_WATER = ITEMS.register("forget_water", () -> new ForgetWaterItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> DEATH_SOUL = ITEMS.register("death_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.EPIC)));
}
