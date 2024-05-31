package dev.lhkongyu.lhmiracleroad.items;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID)
public class LHMiracleRoadLootTables {

    @SubscribeEvent
    public static void modifyVanillaLootPools(final LootTableLoadEvent event) {
        if (event.getName().equals(BuiltInLootTables.DESERT_PYRAMID)) {
            primaryCurrencyLootTable(event);
            extremelyEvilSoulCurrencyLootTable(event);

            kingSoulCurrencyLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.SIMPLE_DUNGEON) || event.getName().equals(BuiltInLootTables.PILLAGER_OUTPOST)) {
            primaryCurrencyLootTable(event);

            evilSoulCurrencyLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.ABANDONED_MINESHAFT) || event.getName().equals(BuiltInLootTables.UNDERWATER_RUIN_BIG)) {
            primaryCurrencyLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.SHIPWRECK_MAP) || event.getName().equals(BuiltInLootTables.WOODLAND_MANSION)) {
            primaryCurrencyLootTable(event);

            evilSoulCurrencyLootTable(event);

            extremelyEvilSoulCurrencyLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.STRONGHOLD_LIBRARY)) {
            primaryCurrencyLootTable(event);

            evilSoulCurrencyLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.BASTION_OTHER) || event.getName().equals(BuiltInLootTables.BASTION_BRIDGE)
                || event.getName().equals(BuiltInLootTables.BASTION_HOGLIN_STABLE) || event.getName().equals(BuiltInLootTables.JUNGLE_TEMPLE)) {
            primaryCurrencyLootTable(event);

            evilSoulCurrencyLootTable(event);

            extremelyEvilSoulCurrencyLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.NETHER_BRIDGE)) {
            primaryCurrencyLootTable(event);
            evilSoulCurrencyLootTable(event);

            kingSoulCurrencyLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.IGLOO_CHEST)) {
            primaryCurrencyLootTable(event);

            extremelyEvilSoulCurrencyLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.ANCIENT_CITY)) {
            primaryCurrencyLootTable(event);
            kingSoulCurrencyLootTable(event);

            superlativeCurrencyLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.END_CITY_TREASURE)) {
            primaryCurrencyLootTable(event);
            superlativeCurrencyLootTable(event);
        }

    }

    private static void primaryCurrencyLootTable(final LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.BROKEN_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.BROKEN_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.GROUP_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.GROUP_SOUL.get()))
                .build());
    }

    private static void evilSoulCurrencyLootTable(final  LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.EVIL_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.EVIL_SOUL.get()))
                .build());
    }

    private static void extremelyEvilSoulCurrencyLootTable(final  LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.EXTREMELY_EVIL_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.EXTREMELY_EVIL_SOUL.get()))
                .build());
    }

    private static void kingSoulCurrencyLootTable(final  LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.KING_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.KING_SOUL.get()))
                .build());
    }

    private static void superlativeCurrencyLootTable(final  LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.DEATH_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.DEATH_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.FORGET_WATER_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.FORGET_WATER.get()))
                .build());
    }
}
