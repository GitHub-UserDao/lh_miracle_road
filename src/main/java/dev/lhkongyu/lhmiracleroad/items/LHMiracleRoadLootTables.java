package dev.lhkongyu.lhmiracleroad.items;

import com.google.common.collect.Lists;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID)
public class LHMiracleRoadLootTables {

    private static final String lootEntityPrefix = "entities/";

    @SubscribeEvent
    public static void modifyVanillaLootPools(final LootTableLoadEvent event) {

        //配置末影龙掉落遗忘之水
        if (event.getName().equals(EntityType.ENDER_DRAGON.getDefaultLootTable())){
            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.ENDER_DRAGON_FORGET_WATER_ODDS.get().floatValue()))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.FORGET_WATER.get()))
                    .build());
        }

        //配置实体战利品掉落
        entityLoot(event);

        if (event.getName().equals(BuiltInLootTables.DESERT_PYRAMID)) {
            commonLootTable(event);

            rareLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.SIMPLE_DUNGEON) || event.getName().equals(BuiltInLootTables.PILLAGER_OUTPOST)) {
            commonLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.ABANDONED_MINESHAFT) || event.getName().equals(BuiltInLootTables.UNDERWATER_RUIN_BIG)) {
            commonLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.SHIPWRECK_MAP) || event.getName().equals(BuiltInLootTables.WOODLAND_MANSION)) {
            commonLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.STRONGHOLD_LIBRARY)) {
            commonLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.BASTION_OTHER) || event.getName().equals(BuiltInLootTables.BASTION_BRIDGE)
                || event.getName().equals(BuiltInLootTables.BASTION_HOGLIN_STABLE) || event.getName().equals(BuiltInLootTables.JUNGLE_TEMPLE)) {
            commonLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.NETHER_BRIDGE)) {
            commonLootTable(event);

            rareLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.IGLOO_CHEST)) {
            commonLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.ANCIENT_CITY)) {
            rareLootTable(event);

            superlativeCurrencyLootTable(event);
        }

        if (event.getName().equals(BuiltInLootTables.END_CITY_TREASURE)) {
            superlativeCurrencyLootTable(event);
        }

    }

    private static void commonLootTable(final LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.LARGE_BLOCK_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.LARGE_BLOCK_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.STRAY_LARGE_BLOCK_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.STRAY_LARGE_BLOCK_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.ADVENTURER_LARGE_BLOCK_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.ADVENTURER_LARGE_BLOCK_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.UNKNOWN_SOLDIER_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.UNKNOWN_SOLDIER_SOUL.get()))
                .build());
    }

    private static void rareLootTable(final  LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.UNKNOWN_SOLDIER_LARGE_BLOCK_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.UNKNOWN_SOLDIER_LARGE_BLOCK_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.EXHAUSTED_KNIGHT_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.EXHAUSTED_KNIGHT_SOUL.get()))
                .build());
    }

    private static void superlativeCurrencyLootTable(final  LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.DEATH_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.DEATH_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.FORGET_WATER_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.FORGET_WATER.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.KING_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(LHMiracleRoadItems.KING_SOUL.get()))
                .build());
    }

    private static void entityLoot(final  LootTableLoadEvent event){
        String name = event.getName().toString();
        if (!name.contains(lootEntityPrefix)) return;
        String entityName = name.substring(name.lastIndexOf(':') + 1);
        if (ordinaryMobLoot(event,entityName)) return;
        if (eliteMobLoot(event,entityName)) return;
        if (specialEliteMobLoot(event,entityName)) return;
        liegeMobLoot(event,entityName);
    }

    private static boolean ordinaryMobLoot(final  LootTableLoadEvent event,String entityName){
        List<? extends String> ordinaryMobConfig = LHMiracleRoadConfig.COMMON.ORDINARY_MOB.get();
        List<String> ordinaryMob = Lists.newArrayList();
        // 在数组中的每个元素前加上前缀
        for (String string : ordinaryMobConfig) {
            ordinaryMob.add(lootEntityPrefix + string);
        }

        if (ordinaryMob.contains(entityName)) {
            event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.ORDINARY_LOOT_SOON_ELAPSE_SOUL_ODDS.get().floatValue()))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.SOON_ELAPSE_SOUL.get()))
                    .build());
            event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.ORDINARY_LOOT_INCOMPLETE_SOUL_ODDS.get().floatValue()))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.INCOMPLETE_SOUL.get()))
                    .build());
            return true;
        }

        return false;
    }

    private static boolean eliteMobLoot(final  LootTableLoadEvent event,String entityName){
        List<? extends String> eliteMobConfig = LHMiracleRoadConfig.COMMON.ELITE_MOB.get();
        List<String> eliteMob = Lists.newArrayList();
        // 在数组中的每个元素前加上前缀
        for (String string : eliteMobConfig) {
            eliteMob.add(lootEntityPrefix + string);
        }

        if (eliteMob.contains(entityName)) {
            event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.ELITE_LOOT_SOUL_ODDS.get().floatValue()))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.LARGE_BLOCK_SOUL.get()))
                    .build());
            return true;
        }
        return false;
    }

    private static boolean specialEliteMobLoot(final  LootTableLoadEvent event,String entityName){
        List<? extends String> specialEliteMobConfig = LHMiracleRoadConfig.COMMON.SPECIAL_ELITE_MOB.get();
        List<String> specialEliteMob = Lists.newArrayList();
        // 在数组中的每个元素前加上前缀
        for (String string : specialEliteMobConfig) {
            specialEliteMob.add(lootEntityPrefix + string);
        }

        if (specialEliteMob.contains(entityName)) {
            event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.SPECIAL_ELITE_LOOT_SOUL_ODDS.get().floatValue()))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.EXHAUSTED_GENERAL_SOUL.get()))
                    .build());
            return true;
        }
        return false;
    }

    private static boolean liegeMobLoot(final  LootTableLoadEvent event,String entityName){
        List<? extends String> liegeMobConfig = LHMiracleRoadConfig.COMMON.LIEGE_MOB.get();
        List<String> liegeMob = Lists.newArrayList();
        // 在数组中的每个元素前加上前缀
        for (String string : liegeMobConfig) {
            liegeMob.add(lootEntityPrefix + string);
        }

        if (liegeMob.contains(entityName)) {
            event.getTable().addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.LIEGE_LOOT_SOUL_ODDS.get().floatValue()))
                    .add(LootItem.lootTableItem(LHMiracleRoadItems.LIEGE_SOUL.get()))
                    .build());
            return true;
        }
        return false;
    }
}
