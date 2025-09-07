package dev.lhkongyu.lhmiracleroad.event;

import com.google.common.collect.Lists;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.data.loot.function.CurioFunction;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID)
public class LootEvent {

    private static final String lootEntityPrefix = "entities/";

    @SubscribeEvent
    public static void modifyVanillaLootPools(final LootTableLoadEvent event) {

        //配置实体战利品掉落
        entityLoot(event);

        //铁匠铺
        if (event.getName().equals(BuiltInLootTables.VILLAGE_WEAPONSMITH)) {
            commonLootTable(event);

            commonCuriosLoot(event);
        }

        //沙漠金字塔
        if (event.getName().equals(BuiltInLootTables.DESERT_PYRAMID)) {
            commonLootTable(event);

            rareLootTable(event);

            commonCuriosLoot(event);
        }

        //地牢加前哨站
        if (event.getName().equals(BuiltInLootTables.SIMPLE_DUNGEON) || event.getName().equals(BuiltInLootTables.PILLAGER_OUTPOST)) {
            commonLootTable(event);

            commonCuriosLoot(event);
        }

        //废弃矿井
        if (event.getName().equals(BuiltInLootTables.ABANDONED_MINESHAFT)) {
            commonLootTable(event);

            commonCuriosLoot(event);
        }

        //水下废弃 大/小
        if (event.getName().equals(BuiltInLootTables.UNDERWATER_RUIN_BIG) || event.getName().equals(BuiltInLootTables.UNDERWATER_RUIN_SMALL)){
            commonLootTable(event);

            //避水戒指
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.025f))
                    .add(LootItem.lootTableItem(ItemsRegistry.WATER_AVOIDANCE_RING.get()))
                    .build());

            commonCuriosLoot(event);
        }

        //沉船系列
        if (event.getName().equals(BuiltInLootTables.SHIPWRECK_MAP) || event.getName().equals(BuiltInLootTables.SHIPWRECK_SUPPLY) || event.getName().equals(BuiltInLootTables.SHIPWRECK_TREASURE) || event.getName().equals(BuiltInLootTables.BURIED_TREASURE)){
            rareLootTable(event);
            //避水戒指
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(ItemsRegistry.WATER_AVOIDANCE_RING.get()))
                    .build());

            commonCuriosLoot(event);
        }

        //林地府邸
        if (event.getName().equals(BuiltInLootTables.WOODLAND_MANSION)) {
            commonLootTable(event);
            rareLootTable(event);

            commonCuriosLoot(event);
        }

        //末地要塞系列
        if (event.getName().equals(BuiltInLootTables.STRONGHOLD_LIBRARY) || event.getName().equals(BuiltInLootTables.STRONGHOLD_CORRIDOR) || event.getName().equals(BuiltInLootTables.STRONGHOLD_CROSSING)) {
            commonLootTable(event);

            commonCuriosLoot(event);
        }

        //堡垒系列
        if (event.getName().equals(BuiltInLootTables.BASTION_OTHER) || event.getName().equals(BuiltInLootTables.BASTION_BRIDGE)
                || event.getName().equals(BuiltInLootTables.BASTION_HOGLIN_STABLE) || event.getName().equals(BuiltInLootTables.BASTION_TREASURE)) {
            commonLootTable(event);

            //贪婪银蛇戒指
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(ItemsRegistry.GREEDY_SILVER_SERPENT_RING.get()))
                    .build());

            commonCuriosLoot(event);
        }

        //丛林神庙
        if (event.getName().equals(BuiltInLootTables.JUNGLE_TEMPLE) || event.getName().equals(BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER)) {
            commonLootTable(event);

            rareLootTable(event);

            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(ItemsRegistry.HUNTERS_MARK.get()))
                    .build());

            commonCuriosLoot(event);
        }

        //下界堡垒系列
        if (event.getName().equals(BuiltInLootTables.NETHER_BRIDGE) || event.getName().equals(BuiltInLootTables.BASTION_BRIDGE)) {
            commonLootTable(event);

            rareLootTable(event);

            //贪婪银蛇戒指
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.08f))
                    .add(LootItem.lootTableItem(ItemsRegistry.GREEDY_SILVER_SERPENT_RING.get()))
                    .build());

            commonCuriosLoot(event);
        }

        //雪屋
        if (event.getName().equals(BuiltInLootTables.IGLOO_CHEST)) {
            commonLootTable(event);

            //冰霜戒指
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.334f))
                    .add(LootItem.lootTableItem(ItemsRegistry.FROST_RING.get()))
                    .build());
        }

        //古城
        if (event.getName().equals(BuiltInLootTables.ANCIENT_CITY)) {
            rareLootTable(event);

            superlativeCurrencyLootTable(event);

            //古老咒术戒指
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.08f))
                    .add(LootItem.lootTableItem(ItemsRegistry.ANCIENT_SPELLCRAFT_RING.get()))
                    .build());
        }

        //末地城
        if (event.getName().equals(BuiltInLootTables.END_CITY_TREASURE)) {
            superlativeCurrencyLootTable(event);
        }

    }

    private static void commonLootTable(final LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.LARGE_BLOCK_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(ItemsRegistry.LARGE_BLOCK_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.STRAY_LARGE_BLOCK_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(ItemsRegistry.STRAY_LARGE_BLOCK_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.ADVENTURER_LARGE_BLOCK_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(ItemsRegistry.ADVENTURER_LARGE_BLOCK_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.UNKNOWN_SOLDIER_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(ItemsRegistry.UNKNOWN_SOLDIER_SOUL.get()))
                .build());
    }

    private static void commonCuriosLoot(final LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.03f))
                .add(LootItem.lootTableItem(ItemsRegistry.LIFE_RING.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.03f))
                .add(LootItem.lootTableItem(ItemsRegistry.RECOVERY_RING.get()))
                .build());


        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.03f))
                .add(LootItem.lootTableItem(ItemsRegistry.RING_STRENGTH.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.03f))
                .add(LootItem.lootTableItem(ItemsRegistry.SHIELDING_RING.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.03f))
                .add(LootItem.lootTableItem(ItemsRegistry.BERSERK_BRACELET.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.03f))
                .add(LootItem.lootTableItem(ItemsRegistry.HEAVY_BRACELET.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.03f))
                .add(LootItem.lootTableItem(ItemsRegistry.LUCKY_BRACELET.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.03f))
                .add(LootItem.lootTableItem(ItemsRegistry.CREED_TALISMAN.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.015f))
                .add(LootItem.lootTableItem(ItemsRegistry.WHISPER_RING.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.01f))
                .add(LootItem.lootTableItem(ItemsRegistry.BIG_SHIELD_TALISMAN.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.015f))
                .add(LootItem.lootTableItem(ItemsRegistry.CONSECRATED_COMBAT_PLUME.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.01f))
                .add(LootItem.lootTableItem(ItemsRegistry.HEART_OF_BLOODLUST.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.015f))
                .add(LootItem.lootTableItem(ItemsRegistry.HUNTING_BOW_TALISMAN.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.015f))
                .add(LootItem.lootTableItem(ItemsRegistry.MANY_WEAPONS_TALISMAN.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.015f))
                .add(LootItem.lootTableItem(ItemsRegistry.SPANNING_WINGS.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.03f))
                .add(LootItem.lootTableItem(ItemsRegistry.WARRIOR_TALISMAN.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.0025f))
                .add(LootItem.lootTableItem(ItemsRegistry.COSSET_RING.get()))
                .apply(CurioFunction.builder())
                .build());
    }

    private static void rareLootTable(final  LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.UNKNOWN_SOLDIER_LARGE_BLOCK_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(ItemsRegistry.UNKNOWN_SOLDIER_LARGE_BLOCK_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.EXHAUSTED_KNIGHT_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(ItemsRegistry.EXHAUSTED_KNIGHT_SOUL.get()))
                .build());
    }

    private static void superlativeCurrencyLootTable(final  LootTableLoadEvent event){
        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.DEATH_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(ItemsRegistry.DEATH_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.FORGET_WATER_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(ItemsRegistry.FORGET_WATER.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.KING_SOUL_PROBABILITY.get().floatValue()))
                .add(LootItem.lootTableItem(ItemsRegistry.KING_SOUL.get()))
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.08f))
                .add(LootItem.lootTableItem(ItemsRegistry.CREED_TALISMAN.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.03f))
                .add(LootItem.lootTableItem(ItemsRegistry.MIRACULOUS_TALISMAN.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.025f))
                .add(LootItem.lootTableItem(ItemsRegistry.HEART_OF_BLOODLUST.get()))
                .apply(CurioFunction.builder())
                .build());

        event.getTable().addPool(LootPool.lootPool()
                .when(LootItemRandomChanceCondition.randomChance(0.015f))
                .add(LootItem.lootTableItem(ItemsRegistry.COSSET_RING.get()))
                .apply(CurioFunction.builder())
                .build());

    }

    private static void entityLoot(final  LootTableLoadEvent event){
        String name = event.getName().toString();
        if (!name.contains(lootEntityPrefix)) return;
        customEntityLoot(event,name);
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
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.ORDINARY_LOOT_SOON_ELAPSE_SOUL_ODDS.get().floatValue()))
                    .add(LootItem.lootTableItem(ItemsRegistry.SOON_ELAPSE_SOUL.get()))
                    .build());
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.ORDINARY_LOOT_INCOMPLETE_SOUL_ODDS.get().floatValue()))
                    .add(LootItem.lootTableItem(ItemsRegistry.INCOMPLETE_SOUL.get()))
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
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.ELITE_LOOT_SOUL_ODDS.get().floatValue()))
                    .add(LootItem.lootTableItem(ItemsRegistry.LARGE_BLOCK_SOUL.get()))
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
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.SPECIAL_ELITE_LOOT_SOUL_ODDS.get().floatValue()))
                    .add(LootItem.lootTableItem(ItemsRegistry.EXHAUSTED_GENERAL_SOUL.get()))
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
                    .add(LootItem.lootTableItem(ItemsRegistry.LIEGE_SOUL.get()))
                    .build());
            return true;
        }
        return false;
    }

    /**
     * 自定义实体掉落物品
     * @param event
     * @param name
     */
    private static void customEntityLoot(final  LootTableLoadEvent event, String name){
        //配置末影龙掉落遗忘之水
        if (event.getName().equals(EntityType.ENDER_DRAGON.getDefaultLootTable())){
            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(LHMiracleRoadConfig.COMMON.ENDER_DRAGON_FORGET_WATER_ODDS.get().floatValue()))
                    .add(LootItem.lootTableItem(ItemsRegistry.FORGET_WATER.get()))
                    .build());
            //配置监守者掉落
        }else if (event.getName().equals(EntityType.WARDEN.getDefaultLootTable())){
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.15f))
                    .add(LootItem.lootTableItem(ItemsRegistry.GREEDY_GOLD_SERPENT_RING.get()))
                    .build());
            //配置烈焰人掉落
        }else if (event.getName().equals(EntityType.BLAZE.getDefaultLootTable())){
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.03f))
                    .add(LootItem.lootTableItem(ItemsRegistry.FIRE_RESISTANCE_RING.get()))
                    .build());
            //配置女巫掉落
        }else if (event.getName().equals(EntityType.WITCH.getDefaultLootTable())){
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.03f))
                    .add(LootItem.lootTableItem(ItemsRegistry.POISON_INVADING_RING.get()))
                    .build());
            //配置凋灵掉落
        }else if (event.getName().equals(EntityType.WITHER.getDefaultLootTable())){
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.5f))
                    .add(LootItem.lootTableItem(ItemsRegistry.DESIRE_RING.get()))
                    .build());
        }
    }
}
