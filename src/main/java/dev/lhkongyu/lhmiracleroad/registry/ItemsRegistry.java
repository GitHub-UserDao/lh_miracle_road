package dev.lhkongyu.lhmiracleroad.registry;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.items.CommonItem;
import dev.lhkongyu.lhmiracleroad.items.SoulItem;
import dev.lhkongyu.lhmiracleroad.items.TrophyItem;
import dev.lhkongyu.lhmiracleroad.items.WaterBottleItem;
import dev.lhkongyu.lhmiracleroad.items.curio.BraceletItem;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import dev.lhkongyu.lhmiracleroad.items.curio.bracelet.BerserkBracelet;
import dev.lhkongyu.lhmiracleroad.items.curio.bracelet.HeavyBracelet;
import dev.lhkongyu.lhmiracleroad.items.curio.bracelet.LuckyBracelet;
import dev.lhkongyu.lhmiracleroad.items.curio.ring.*;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.*;
import dev.lhkongyu.lhmiracleroad.items.sword.HeavyHammerItem;
import dev.lhkongyu.lhmiracleroad.block.tiers.ModTiers;
import dev.lhkongyu.lhmiracleroad.tool.NameTool;
import net.minecraft.world.item.*;
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
    public static final RegistryObject<Item> COSSET_RING = ITEMS.register("cosset_ring", CossetRing::addAttributeModifier);

    public static final RegistryObject<Item> ENDLESS_DESIRE = ITEMS.register("endless_desire", EndlessDesire::addAttributeModifier);

    public static final RegistryObject<Item> RADIANCE_RING = ITEMS.register("radiance_ring", () -> new RingItem(new Item.Properties().rarity(Rarity.UNCOMMON),null));

    public static final RegistryObject<Item> GREEDY_GOLD_SERPENT_RING = ITEMS.register("greedy_gold_serpent_ring", GreedyGoldSerpentRing::addAttributeModifier);

    public static final RegistryObject<Item> GREEDY_SILVER_SERPENT_RING = ITEMS.register("greedy_silver_serpent_ring", GreedySilverSerpentRing::addAttributeModifier);

    public static final RegistryObject<Item> DESIRE_RING = ITEMS.register("desire_ring", DesireRing::addAttributeModifier);

    public static final RegistryObject<Item> ANCIENT_SPELLCRAFT_RING = ITEMS.register("ancient_spellcraft_ring", AncientSpellCraftRing::addAttributeModifier);

//    public static final RegistryObject<Item> WHISPER_RING = ITEMS.register("whisper_ring", WhisperRing::addAttributeModifier);

    public static final RegistryObject<Item> LIFE_RING = ITEMS.register("life_ring", LifeRing::addAttributeModifier);

    public static final RegistryObject<Item> RECOVERY_RING = ITEMS.register("recovery_ring", RecoveryRing::addAttributeModifier);

    public static final RegistryObject<Item> CAT_RING = ITEMS.register("cat_ring", CatRing::addAttributeModifier);

    public static final RegistryObject<Item> FLAME_RING = ITEMS.register("flame_ring", FlameRing::addAttributeModifier);

    public static final RegistryObject<Item> SOUL_RING = ITEMS.register("soul_ring", SoulRing::addAttributeModifier);

    public static final RegistryObject<Item> HOLY_RING = ITEMS.register("holy_ring", HolyRing::addAttributeModifier);

    public static final RegistryObject<Item> LIGHTNING_RING = ITEMS.register("lightning_ring", LightningRing::addAttributeModifier);

    public static final RegistryObject<Item> MAGIC_RING = ITEMS.register("magic_ring", MagicRing::addAttributeModifier);

    public static final RegistryObject<Item> DARK_RING = ITEMS.register("dark_ring", DarkRing::addAttributeModifier);

    //手镯
    public static final RegistryObject<Item> HUNTERS_MARK = ITEMS.register("hunter_mark", () -> new BraceletItem(new Item.Properties().rarity(Rarity.EPIC),null));

    public static final RegistryObject<Item> BERSERK_BRACELET = ITEMS.register("berserk_bracelet", BerserkBracelet::addAttributeModifier);

    public static final RegistryObject<Item> HEAVY_BRACELET = ITEMS.register("heavy_bracelet", HeavyBracelet::addAttributeModifier);

    public static final RegistryObject<Item> LUCKY_BRACELET = ITEMS.register("lucky_bracelet", LuckyBracelet::addAttributeModifier);

    public static final RegistryObject<Item> ABYSSBIND_BRACELET = ITEMS.register("abyssbind_bracelet", () -> new BraceletItem(new Item.Properties().rarity(Rarity.RARE),null));


    //护符
    public static final RegistryObject<Item> BIG_SHIELD_TALISMAN = ITEMS.register("big_shield_talisman", () -> new BigShieldTalisman(new Item.Properties().rarity(Rarity.EPIC),null));

    public static final RegistryObject<Item> CONSECRATED_COMBAT_PLUME = ITEMS.register("consecrated_combat_plume", ConsecratedCombatPlume::addAttributeModifier);

    public static final RegistryObject<Item> CREED_TALISMAN = ITEMS.register("creed_talisman", CreedTalisman::createCreedTalisman);

//    public static final RegistryObject<Item> ENDER_GAZE = ITEMS.register("ender_gaze", () -> new TalismanItem(new Item.Properties().rarity(Rarity.EPIC),null));

    public static final RegistryObject<Item> HEART_OF_BLOODLUST = ITEMS.register("heart_of_bloodlust", () -> new TalismanItem(new Item.Properties().rarity(Rarity.UNCOMMON),null));

    public static final RegistryObject<Item> HUNTING_BOW_TALISMAN = ITEMS.register("hunting_bow_talisman", HuntingBowTalisman::addAttributeModifier);

    public static final RegistryObject<Item> MANY_WEAPONS_TALISMAN = ITEMS.register("many_weapons_talisman", ManyWeaponsTalisman::addAttributeModifier);

    public static final RegistryObject<Item> MIRACULOUS_TALISMAN = ITEMS.register("miraculous_talisman", MiraculousTalisman::createCreedTalisman);

    public static final RegistryObject<Item> SPANNING_WINGS = ITEMS.register("spanning_wings", SpanningWings::addAttributeModifier);

    public static final RegistryObject<Item> WARRIOR_TALISMAN = ITEMS.register("warrior_talisman", WarriorTalisman::addAttributeModifier);

    public static final RegistryObject<Item> TAINTED_GODDESS_STATUE = ITEMS.register("tainted_goddess_statue", TaintedGoddessStatue::addAttributeModifier);

    public static final RegistryObject<Item> HAWK_TALISMAN = ITEMS.register("hawk_talisman", () -> new TalismanItem(new Item.Properties().rarity(Rarity.RARE),null));

    public static final RegistryObject<Item> FROST_TALISMAN = ITEMS.register("frost_talisman", FrostTalisman::addAttributeModifier);

    public static final RegistryObject<Item> POISON_TALISMAN = ITEMS.register("poison_talisman",PoisonTalisman::addAttributeModifier );

    public static final RegistryObject<Item> FLAME_TALISMAN = ITEMS.register("flame_talisman", FlameTalisman::addAttributeModifier);

    public static final RegistryObject<Item> BLOOD_TALISMAN = ITEMS.register("blood_talisman", BloodTalisman::addAttributeModifier);


    //营地
    public static final RegistryObject<Item> CAMPSITE = ITEMS.register("campsite", () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));

    //战利品
    public static final RegistryObject<Item> COMMON_TROPHY = ITEMS.register("common_trophy",
                    () -> new TrophyItem(new Item.Properties().rarity(Rarity.COMMON), NameTool.COMMON));

    public static final RegistryObject<Item> EXQUISITE_TROPHY = ITEMS.register("exquisite_trophy",
                    () -> new TrophyItem(new Item.Properties().rarity(Rarity.RARE), NameTool.EXQUISITE));

    public static final RegistryObject<Item> RARE_TROPHY = ITEMS.register("rare_trophy",
                    () -> new TrophyItem(new Item.Properties().rarity(Rarity.RARE), NameTool.RARE));

    public static final RegistryObject<Item> EPIC_TROPHY = ITEMS.register("epic_trophy",
                    () -> new TrophyItem(new Item.Properties().rarity(Rarity.EPIC), NameTool.EPIC));

    public static final RegistryObject<Item> LEGEND_TROPHY = ITEMS.register("legend_trophy",
                    () -> new TrophyItem(new Item.Properties().rarity(Rarity.UNCOMMON), NameTool.LEGEND));
    //宝石
    public static final RegistryObject<Item> FLAME_GEM = ITEMS.register("flame_gem", () -> new CommonItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> LIGHTNING_GEM = ITEMS.register("lightning_gem", () -> new CommonItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> DARK_GEM = ITEMS.register("dark_gem", () -> new CommonItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> BLOOD_GEM = ITEMS.register("blood_gem", () -> new CommonItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> ICE_GEM = ITEMS.register("ice_gem", () -> new CommonItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> HOLY_GEM = ITEMS.register("holy_gem", () -> new CommonItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> SOUL_GEM = ITEMS.register("soul_gem", () -> new CommonItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> HEAVY_GEM = ITEMS.register("heavy_gem", () -> new CommonItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> POISON_GEM = ITEMS.register("poison_gem", () -> new CommonItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> SHARP_GEM = ITEMS.register("sharp_gem", () -> new CommonItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> WHETSTONE = ITEMS.register("whetstone", () -> new CommonItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> METEORITE_DISK = ITEMS.register("meteorite_disk", () -> new CommonItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> METEORIC_IRON_BLOCK = ITEMS.register("meteoric_iron_block", () -> new CommonItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> METEORIC_IRON_BIG_FRAGMENT = ITEMS.register("meteoric_iron_big_fragment", () -> new CommonItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> METEORIC_IRON_FRAGMENT = ITEMS.register("meteoric_iron_fragment", () -> new CommonItem(new Item.Properties().rarity(Rarity.COMMON)));

//weapon
    public static final RegistryObject<Item> HAMMER_IRON = ITEMS.register("hammer_iron", () -> new HeavyHammerItem(ModTiers.HAMMER_IRON, 8, -3.4F, new Item.Properties()));
    public static final RegistryObject<Item> HAMMER_NETHERITE = ITEMS.register("hammer_netherite", () -> new HeavyHammerItem(ModTiers.HAMMER_NETHERITE, 9, -3.3F, new Item.Properties()));
    public static final RegistryObject<Item> HAMMER_ANCIENT = ITEMS.register("hammer_ancient", () -> new HeavyHammerItem(ModTiers.HAMMER_ANCIENT, 12, -3.2F, new Item.Properties()));
    public static final RegistryObject<Item> ANCIENT_CORE = ITEMS.register("ancient_core", () -> new CommonItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
