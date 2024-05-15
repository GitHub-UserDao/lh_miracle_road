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
            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.2F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.EXTREMELY_EVIL_SOUL.get()))
                    .build());

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.1F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.KING_SOUL.get()))
                    .build());
        }

        if (event.getName().equals(BuiltInLootTables.SIMPLE_DUNGEON) || event.getName().equals(BuiltInLootTables.PILLAGER_OUTPOST)) {
            currencyLootTable(event);

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.2F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.EVIL_SOUL.get()))
                    .build());
        }

        if (event.getName().equals(BuiltInLootTables.ABANDONED_MINESHAFT) || event.getName().equals(BuiltInLootTables.UNDERWATER_RUIN_BIG)) {
            currencyLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.SHIPWRECK_MAP) || event.getName().equals(BuiltInLootTables.WOODLAND_MANSION)) {
            currencyLootTable(event);

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.2F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.EVIL_SOUL.get()))
                    .build());

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.15F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.EXTREMELY_EVIL_SOUL.get()))
                    .build());
        }

        if (event.getName().equals(BuiltInLootTables.STRONGHOLD_LIBRARY)) {
            currencyLootTable(event);

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.2F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.EVIL_SOUL.get()))
                    .build());
        }

        if (event.getName().equals(BuiltInLootTables.BASTION_OTHER) || event.getName().equals(BuiltInLootTables.BASTION_BRIDGE)
                || event.getName().equals(BuiltInLootTables.BASTION_HOGLIN_STABLE) || event.getName().equals(BuiltInLootTables.JUNGLE_TEMPLE)) {
            currencyLootTable(event);

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.2F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.EVIL_SOUL.get()))
                    .build());

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.15F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.EXTREMELY_EVIL_SOUL.get()))
                    .build());
        }

        if (event.getName().equals(BuiltInLootTables.NETHER_BRIDGE)) {
            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.2F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.EVIL_SOUL.get()))
                    .build());

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.08F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.KING_SOUL.get()))
                    .build());
        }

        if (event.getName().equals(BuiltInLootTables.IGLOO_CHEST)) {
            currencyLootTable(event);

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.15F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.EXTREMELY_EVIL_SOUL.get()))
                    .build());
        }

        if (event.getName().equals(BuiltInLootTables.ANCIENT_CITY)) {
            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.1F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.KING_SOUL.get()))
                    .build());

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.08F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.DEATH_SOUL.get()))
                    .build());

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.FORGET_WATER_PROBABILITY.get().floatValue()))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.FORGET_WATER.get()))
                    .build());
        }

        if (event.getName().equals(BuiltInLootTables.END_CITY_TREASURE)) {
            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.08F))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.DEATH_SOUL.get()))
                    .build());

            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.FORGET_WATER_PROBABILITY.get().floatValue()))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.FORGET_WATER.get()))
                    .build());
        }

    }

    private static void currencyLootTable(final LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.5F))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.BROKEN_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.35F))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.GROUP_SOUL.get()))
                .build());
    }
}
