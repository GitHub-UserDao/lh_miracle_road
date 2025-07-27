package dev.lhkongyu.lhmiracleroad.registry;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.items.SoulItem;
import dev.lhkongyu.lhmiracleroad.items.WaterBottleItem;
import dev.lhkongyu.lhmiracleroad.items.curio.BraceletItem;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import dev.lhkongyu.lhmiracleroad.items.curio.bracelet.BerserkBracelet;
import dev.lhkongyu.lhmiracleroad.items.curio.bracelet.HeavyBracelet;
import dev.lhkongyu.lhmiracleroad.items.curio.bracelet.LuckyBracelet;
import dev.lhkongyu.lhmiracleroad.items.curio.ring.*;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemsRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LHMiracleRoad.MODID);

//    public static final RegistryObject<Item> BROKEN_SOUL = ITEMS.register("broken_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.COMMON)));
//
//    public static final RegistryObject<Item> GROUP_SOUL = ITEMS.register("group_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.COMMON)));
//
//    public static final RegistryObject<Item> EVIL_SOUL = ITEMS.register("evil_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.RARE)));
//
//    public static final RegistryObject<Item> EXTREMELY_EVIL_SOUL = ITEMS.register("extremely_evil_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.RARE)));

    //灵魂
    public static final RegistryObject<Item> KING_SOUL = ITEMS.register("king_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> DEATH_SOUL = ITEMS.register("death_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> SOON_ELAPSE_SOUL = ITEMS.register("soon_elapse_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> INCOMPLETE_SOUL = ITEMS.register("incomplete_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> LARGE_BLOCK_SOUL = ITEMS.register("large_block_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> STRAY_LARGE_BLOCK_SOUL = ITEMS.register("stray_large_block_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> ADVENTURER_LARGE_BLOCK_SOUL = ITEMS.register("adventurer_large_block_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> UNKNOWN_SOLDIER_SOUL = ITEMS.register("unknown_soldier_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> UNKNOWN_SOLDIER_LARGE_BLOCK_SOUL = ITEMS.register("unknown_soldier_large_block_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> EXHAUSTED_KNIGHT_SOUL = ITEMS.register("exhausted_knight_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> EXHAUSTED_GENERAL_SOUL = ITEMS.register("exhausted_general_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> LIEGE_SOUL = ITEMS.register("liege_soul", () -> new SoulItem(new Item.Properties().rarity(Rarity.EPIC)));

    //药水
    public static final RegistryObject<Item> FORGET_WATER = ITEMS.register("forget_water", () -> new WaterBottleItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> EXPERIENCE_CONVERT_SOUL = ITEMS.register("experience_convert_soul", () -> new WaterBottleItem(new Item.Properties().rarity(Rarity.COMMON)));

    //戒指
    public static final RegistryObject<Item> GREEDY_GOLD_SERPENT_RING = ITEMS.register("greedy_gold_serpent_ring", GreedyGoldSerpentRing::addAttributeModifier);

    public static final RegistryObject<Item> COSSET_RING = ITEMS.register("cosset_ring", CossetRing::addAttributeModifier);

    public static final RegistryObject<Item> ENDLESS_DESIRE = ITEMS.register("endless_desire", EndlessDesire::addAttributeModifier);

    public static final RegistryObject<Item> RADIANCE_RING = ITEMS.register("radiance_ring", () -> new RingItem(new Item.Properties().rarity(Rarity.UNCOMMON),null));

    public static final RegistryObject<Item> GREEDY_SILVER_SERPENT_RING = ITEMS.register("greedy_silver_serpent_ring", GreedySilverSerpentRing::addAttributeModifier);

    public static final RegistryObject<Item> DESIRE_RING = ITEMS.register("desire_ring", DesireRing::addAttributeModifier);

//    public static final RegistryObject<Item> VIGILANCE_RING_DISTANT = ITEMS.register("vigilance_ring_distant", () -> new RingItem(new Item.Properties().rarity(Rarity.EPIC),null));
//
//    public static final RegistryObject<Item> VIGILANCE_RING_NEAR = ITEMS.register("vigilance_ring_near", () -> new RingItem(new Item.Properties().rarity(Rarity.EPIC),null));

    public static final RegistryObject<Item> ANCIENT_SPELLCRAFT_RING = ITEMS.register("ancient_spellcraft_ring", AncientSpellCraftRing::addAttributeModifier);

    public static final RegistryObject<Item> WHISPER_RING = ITEMS.register("whisper_ring", WhisperRing::addAttributeModifier);

    public static final RegistryObject<Item> FIRE_RESISTANCE_RING = ITEMS.register("fire_resistance_ring", () -> new RingItem(new Item.Properties().rarity(Rarity.RARE),null));

    public static final RegistryObject<Item> POISON_INVADING_RING = ITEMS.register("poison_invading_ring", () -> new RingItem(new Item.Properties().rarity(Rarity.RARE),null));

    public static final RegistryObject<Item> WATER_AVOIDANCE_RING = ITEMS.register("water_avoidance_ring", () -> new RingItem(new Item.Properties().rarity(Rarity.RARE),null));

    public static final RegistryObject<Item> FROST_RING = ITEMS.register("frost_ring", () -> new RingItem(new Item.Properties().rarity(Rarity.RARE),null));

    public static final RegistryObject<Item> LIFE_RING = ITEMS.register("life_ring", LifeRing::addAttributeModifier);

    public static final RegistryObject<Item> RECOVERY_RING = ITEMS.register("recovery_ring", RecoveryRing::addAttributeModifier);

    public static final RegistryObject<Item> RING_STRENGTH = ITEMS.register("ring_strength", RingStrength::addAttributeModifier);

    public static final RegistryObject<Item> SHIELDING_RING = ITEMS.register("shielding_ring", ShieldingRing::addAttributeModifier);

    //手镯
    public static final RegistryObject<Item> HUNTERS_MARK = ITEMS.register("hunter_mark", () -> new BraceletItem(new Item.Properties().rarity(Rarity.EPIC),null));

    public static final RegistryObject<Item> BERSERK_BRACELET = ITEMS.register("berserk_bracelet", BerserkBracelet::addAttributeModifier);

    public static final RegistryObject<Item> HEAVY_BRACELET = ITEMS.register("heavy_bracelet", HeavyBracelet::addAttributeModifier);

    public static final RegistryObject<Item> LUCKY_BRACELET = ITEMS.register("lucky_bracelet", LuckyBracelet::addAttributeModifier);

    //护符
    public static final RegistryObject<Item> BIG_SHIELD_TALISMAN = ITEMS.register("big_shield_talisman", BigShieldTalisman::addAttributeModifier);

    public static final RegistryObject<Item> CONSECRATED_COMBAT_PLUME = ITEMS.register("consecrated_combat_plume", ConsecratedCombatPlume::addAttributeModifier);

    public static final RegistryObject<Item> CREED_TALISMAN = ITEMS.register("creed_talisman", CreedTalisman::createCreedTalisman);

//    public static final RegistryObject<Item> ENDER_GAZE = ITEMS.register("ender_gaze", () -> new TalismanItem(new Item.Properties().rarity(Rarity.EPIC),null));

    public static final RegistryObject<Item> HEART_OF_BLOODLUST = ITEMS.register("heart_of_bloodlust", () -> new TalismanItem(new Item.Properties().rarity(Rarity.UNCOMMON),null));

    public static final RegistryObject<Item> HUNTING_BOW_TALISMAN = ITEMS.register("hunting_bow_talisman", HuntingBowTalisman::addAttributeModifier);

    public static final RegistryObject<Item> MANY_WEAPONS_TALISMAN = ITEMS.register("many_weapons_talisman", ManyWeaponsTalisman::addAttributeModifier);

    public static final RegistryObject<Item> MIRACULOUS_TALISMAN = ITEMS.register("miraculous_talisman", MiraculousTalisman::createCreedTalisman);

    public static final RegistryObject<Item> SPANNING_WINGS = ITEMS.register("spanning_wings", SpanningWings::addAttributeModifier);

    public static final RegistryObject<Item> WARRIOR_TALISMAN = ITEMS.register("warrior_talisman", WarriorTalisman::addAttributeModifier);


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
