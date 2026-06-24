package dev.lhkongyu.lhmiracleroad.items.trophy;

import java.util.ArrayList;
import java.util.List;

public class TrophyTool {

    public static List<TrophyData> setCommon() {
        List<TrophyData> dataList = new ArrayList<>();
        TrophyData data = group("common",1);
        // 铁锭
        add(data, "minecraft:iron_ingot", 50, 1, 4);
        // 煤炭
        add(data, "minecraft:coal", 50, 2, 8);
        // 铜锭
        add(data, "minecraft:copper_ingot", 45, 2, 6);
        // 面包
        add(data, "minecraft:bread", 40, 1, 3);
        // 骨头
        add(data, "minecraft:bone", 35, 2, 8);
        // 皮革
        add(data, "minecraft:leather", 30, 1, 4);
        // 史莱姆球
        add(data, "minecraft:slime_ball", 20, 1, 3);
        // 金锭
        add(data, "minecraft:gold_ingot", 18, 1, 3);
        // 紫水晶碎片
        add(data, "minecraft:amethyst_shard", 16, 2, 6);
        // 海龟鳞甲
        add(data, "minecraft:scute", 12, 1, 2);
        // 绿宝石
        add(data, "minecraft:emerald", 10, 1, 3);
        // 经验瓶（受幸运影响）
        add(data, "minecraft:experience_bottle", 8, 1, 2, 1);
        // 末影珍珠（受幸运影响）
        add(data, "minecraft:ender_pearl", 6, 1, 2, 1);
        // 即将逝去的灵魂
        add(data, "lhmiracleroad:soon_elapse_soul", 20, 1, 2);
        // 不完整的灵魂（受幸运影响）
        add(data, "lhmiracleroad:incomplete_soul", 5, 1, 1, 1);
        // 经验转换药水
        add(data, "lhmiracleroad:experience_convert_soul", 20, 1, 1);
        // 陨铁碎片（受幸运影响）
        add(data, "lhmiracleroad:meteoric_iron_fragment", 5, 1, 1, 1);

        dataList.add(data);

        return dataList;
    }

    public static List<TrophyData> setExquisite(){
        List<TrophyData> dataList = new ArrayList<>();

        TrophyData data = group("exquisite",1);
        // 铁锭
        add(data, "minecraft:iron_ingot", 50, 2, 6);
        // 金锭
        add(data, "minecraft:gold_ingot", 35, 2, 5);
        // 绿宝石
        add(data, "minecraft:emerald", 50, 2, 5);
        // 海龟鳞甲
        add(data, "minecraft:scute", 30, 1, 2);
        // 经验瓶
        add(data, "minecraft:experience_bottle", 20, 2, 5);
        // 钻石（受幸运影响）
        add(data, "minecraft:diamond", 12, 1, 2, 1.5f);
        // 末影珍珠
        add(data, "minecraft:ender_pearl", 20, 1, 3);
        // 即将逝去的灵魂
        add(data, "lhmiracleroad:soon_elapse_soul", 20, 2, 6);
        // 不完整的灵魂
        add(data, "lhmiracleroad:incomplete_soul", 18, 1, 2);
        // 大块灵魂
        add(data, "lhmiracleroad:large_block_soul", 12, 1, 1);
        // 陨铁碎片（受幸运影响）
        add(data, "lhmiracleroad:meteoric_iron_fragment", 10, 1, 2, 1);
        // 寻常战利品袋
        add(data, "lhmiracleroad:common_trophy", 20, 1, 3);
        // 陨铁大碎片 （受幸运影响）
        add(data, "lhmiracleroad:meteoric_iron_big_fragment", 5, 1, 1,1);
        // 流浪者的大块灵魂 （受幸运影响）
        add(data, "lhmiracleroad:stray_large_block_soul", 8, 1, 1,1);
        //  砥石 （受幸运影响）
        add(data, "lhmiracleroad:whetstone", 12, 1, 2,1);

        dataList.add(data);

        return dataList;
    }

    public static List<TrophyData> setRare(){
        List<TrophyData> dataList = new ArrayList<>();

        // 钻石
        addSingle(dataList,"diamond", "minecraft:diamond", 30, 2, 5);
        // 大块灵魂
        addSingle(dataList,"large_block_soul", "lhmiracleroad:large_block_soul", 35, 2, 5);
        // 陨铁碎片
        addSingle(dataList,"meteoric_iron_fragment", "lhmiracleroad:meteoric_iron_fragment", 30, 2, 5);
        // 精品战利品袋
        addSingle(dataList,"exquisite_trophy", "lhmiracleroad:exquisite_trophy", 20, 1, 3);
        // 流浪者的大块灵魂
        addSingle(dataList,"stray_large_block_soul", "lhmiracleroad:stray_large_block_soul", 25, 1, 3);
        // 冒险者的大块灵魂
        addSingle(dataList,"adventurer_large_block_soul", "lhmiracleroad:adventurer_large_block_soul", 20, 1, 3);
        // 无名战士的灵魂
        addSingle(dataList,"unknown_soldier_soul", "lhmiracleroad:unknown_soldier_soul", 16, 1, 3);
        // 陨铁大碎片 （受幸运影响）
        addSingle(dataList,"meteoric_iron_big_fragment", "lhmiracleroad:meteoric_iron_big_fragment", 12, 1, 2,1);
        // 砥石
        addSingle(dataList,"whetstone", "lhmiracleroad:whetstone", 20, 1, 3);
        // 陨铁块 （受幸运影响）
        addSingle(dataList,"meteoric_iron_block","lhmiracleroad:meteoric_iron_block",6, 1, 2,1.5f);

        //饰品 （受幸运影响）
        TrophyData data = group("curios",5,1.5f);
        // 生命戒指
        add(data, "lhmiracleroad:life_ring", 5, 1, 1);
        // 恢复戒指
        add(data, "lhmiracleroad:recovery_ring", 5, 1, 1);
        // 狂暴手镯
        add(data, "lhmiracleroad:berserk_bracelet", 5, 1, 1);
        // 沉重手镯
        add(data, "lhmiracleroad:heavy_bracelet", 5, 1, 1);
        // 幸运手镯
        add(data, "lhmiracleroad:lucky_bracelet", 5, 1, 1);
        // 信仰护符
        add(data, "lhmiracleroad:creed_talisman", 5, 1, 1);
        // 战士护符
        add(data, "lhmiracleroad:warrior_talisman", 5, 1, 1);
        // 灵猫戒指
        add(data, "lhmiracleroad:cat_ring", 5, 1, 1);
        // 幽邃固魂
        add(data, "lhmiracleroad:abyssbind_bracelet", 5, 1, 1);
        // 鹰护符
        add(data, "lhmiracleroad:hawk_talisman", 5, 1, 1);
        dataList.add(data);

        return dataList;
    }

    public static List<TrophyData> setEpic(){
        List<TrophyData> dataList = new ArrayList<>();

        //矿物
        TrophyData mineral = group("mineral",30,0,1,2);
        // 钻石
        add(mineral,"minecraft:diamond", 20, 6, 12);
        // 下届合金碇
        add(mineral,"minecraft:netherite_ingot", 10, 1, 1,1.5f);
        // 下届合金碎片
        add(mineral,"minecraft:netherite_scrap", 15, 3, 6,1);
        // 铁锭
        add(mineral, "minecraft:iron_ingot", 20, 64, 64);
        // 金锭
        add(mineral, "minecraft:gold_ingot", 20, 64, 64);
        // 绿宝石
        add(mineral, "minecraft:emerald", 20, 64, 64);
        dataList.add(mineral);

        //灵魂
        TrophyData soul = group("soul",35);
        add(soul,"lhmiracleroad:stray_large_block_soul", 30, 3, 9);
        add(soul,"lhmiracleroad:adventurer_large_block_soul", 25, 3, 9);
        add(soul,"lhmiracleroad:unknown_soldier_soul", 20, 3, 9);
        add(soul,"lhmiracleroad:unknown_soldier_large_block_soul", 15, 2, 5);
        add(soul,"lhmiracleroad:exhausted_knight_soul", 12, 2, 5,1);
        add(soul,"lhmiracleroad:exhausted_general_soul", 10, 2, 5,1);
        add(soul,"lhmiracleroad:liege_soul", 5, 1, 3,1.5f);
        dataList.add(soul);

        // 稀有战利品袋
        addSingle(dataList,"rare_trophy", "lhmiracleroad:rare_trophy", 15, 1, 3);

        //遗忘之水（受幸运影响）
        addSingle(dataList,"forget_water", "lhmiracleroad:forget_water", 15, 1, 1,1);

        //强化宝石
        TrophyData strengthen_gem = group("strengthen_gem",30);
        add(strengthen_gem,"lhmiracleroad:meteoric_iron_fragment",20, 5, 10);
        add(strengthen_gem,"lhmiracleroad:meteoric_iron_big_fragment",20, 3, 6);
        add(strengthen_gem,"lhmiracleroad:meteoric_iron_block",20, 1, 3,2);
        dataList.add(strengthen_gem);

        //属性宝石（受幸运影响）
        TrophyData attribute_gem = group("attribute_gem",10,1.5f);
        add(attribute_gem,"lhmiracleroad:sharp_gem",20, 1, 1);
        add(attribute_gem,"lhmiracleroad:heavy_gem",20, 1, 1);
        add(attribute_gem,"lhmiracleroad:flame_gem",15, 1, 1);
        add(attribute_gem,"lhmiracleroad:blood_gem",15, 1, 1);
        add(attribute_gem,"lhmiracleroad:ice_gem",15, 1, 1);
        add(attribute_gem,"lhmiracleroad:poison_gem",15, 1, 1);
        add(attribute_gem,"lhmiracleroad:lightning_gem",5, 1, 1,1);
        add(attribute_gem,"lhmiracleroad:dark_gem",5, 1, 1,1);
        add(attribute_gem,"lhmiracleroad:holy_gem",5, 1, 1,1);
        add(attribute_gem,"lhmiracleroad:soul_gem",5, 1, 1,1);
        dataList.add(attribute_gem);

        //稀有饰品
        TrophyData curios_rare = group("curios_rare",25);
        // 生命戒指
        add(curios_rare, "lhmiracleroad:life_ring", 5, 1, 1);
        // 恢复戒指
        add(curios_rare, "lhmiracleroad:recovery_ring", 5, 1, 1);
        // 狂暴手镯
        add(curios_rare, "lhmiracleroad:berserk_bracelet", 5, 1, 1);
        // 沉重手镯
        add(curios_rare, "lhmiracleroad:heavy_bracelet", 5, 1, 1);
        // 幸运手镯
        add(curios_rare, "lhmiracleroad:lucky_bracelet", 5, 1, 1);
        // 信仰护符
        add(curios_rare, "lhmiracleroad:creed_talisman", 5, 1, 1);
        // 战士护符
        add(curios_rare, "lhmiracleroad:warrior_talisman", 5, 1, 1);
        // 灵猫戒指
        add(curios_rare, "lhmiracleroad:cat_ring", 5, 1, 1);
        // 幽邃固魂
        add(curios_rare, "lhmiracleroad:abyssbind_bracelet", 5, 1, 1);
        // 鹰护符
        add(curios_rare, "lhmiracleroad:hawk_talisman", 5, 1, 1);
        dataList.add(curios_rare);

        //史诗饰品（受幸运影响）
        TrophyData curios_epic = group("curios_epic",8,2f);
        add(curios_epic, "lhmiracleroad:greedy_gold_serpent_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:greedy_silver_serpent_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:desire_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:ancient_spellcraft_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:hunter_mark", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:big_shield_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:consecrated_combat_plume", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:hunting_bow_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:many_weapons_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:spanning_wings", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:flame_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:soul_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:holy_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:lightning_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:magic_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:dark_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:frost_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:poison_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:flame_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:blood_talisman", 5, 1, 1);
        dataList.add(curios_epic);

        return dataList;
    }

    public static List<TrophyData> setLegend(){
        List<TrophyData> dataList = new ArrayList<>();

        // 史诗战利品袋
        addSingle(dataList,"epic_trophy", "lhmiracleroad:epic_trophy", 15, 1, 3);

        //史诗饰品
        TrophyData curios_epic = group("curios_epic",30);
        add(curios_epic, "lhmiracleroad:greedy_gold_serpent_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:greedy_silver_serpent_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:desire_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:ancient_spellcraft_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:hunter_mark", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:big_shield_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:consecrated_combat_plume", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:hunting_bow_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:many_weapons_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:spanning_wings", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:flame_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:soul_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:holy_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:lightning_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:magic_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:dark_ring", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:frost_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:poison_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:flame_talisman", 5, 1, 1);
        add(curios_epic, "lhmiracleroad:blood_talisman", 5, 1, 1);
        dataList.add(curios_epic);

        //属性宝石
        TrophyData attribute_gem = group("attribute_gem",30);
        add(attribute_gem,"lhmiracleroad:sharp_gem",20, 1, 1);
        add(attribute_gem,"lhmiracleroad:heavy_gem",20, 1, 1);
        add(attribute_gem,"lhmiracleroad:flame_gem",15, 1, 1);
        add(attribute_gem,"lhmiracleroad:blood_gem",15, 1, 1);
        add(attribute_gem,"lhmiracleroad:ice_gem",15, 1, 1);
        add(attribute_gem,"lhmiracleroad:poison_gem",15, 1, 1);
        add(attribute_gem,"lhmiracleroad:lightning_gem",5, 1, 1,1);
        add(attribute_gem,"lhmiracleroad:dark_gem",5, 1, 1,1);
        add(attribute_gem,"lhmiracleroad:holy_gem",5, 1, 1,1);
        add(attribute_gem,"lhmiracleroad:soul_gem",5, 1, 1,1);
        dataList.add(attribute_gem);

        //灵魂（受幸运影响）
        TrophyData soul = group("soul",10,1f);
        add(soul,"lhmiracleroad:king_soul", 20, 1, 1);
        add(soul,"lhmiracleroad:death_soul", 20, 1, 1);
        dataList.add(soul);

        //陨石盘（受幸运影响）
        addSingle(dataList,"meteorite_disk", "lhmiracleroad:meteorite_disk", 10, 1, 1,2f);

        //远古核心（受幸运影响）
        addSingle(dataList,"ancient_core", "lhmiracleroad:ancient_core", 10, 1, 1,1.5f);

        //传说饰品（受幸运影响）
        TrophyData curios_legend = group("curios_legend",10,2f);
        add(curios_legend, "lhmiracleroad:endless_desire", 10, 1, 1);
        add(curios_legend, "lhmiracleroad:radiance_ring", 5, 1, 1);
        add(curios_legend, "lhmiracleroad:cosset_ring", 10, 1, 1);
        add(curios_legend, "lhmiracleroad:miraculous_talisman", 10, 1, 1);
        add(curios_legend, "lhmiracleroad:tainted_goddess_statue", 10, 1, 1);
        add(curios_legend, "lhmiracleroad:heart_of_bloodlust", 10, 1, 1);
        dataList.add(curios_legend);

        return dataList;
    }

    private static void addSingle(List<TrophyData> list, String name, String item, int weight, int min, int max){
        addSingle(list,name,item,weight,min,max,0);
    }

    private static void addSingle(List<TrophyData> list, String name, String item, int weight, int min, int max, float luckWeight){
        TrophyData data = group(name,weight,luckWeight);
        add(data,item,1,min,max);
        list.add(data);
    }

    private static void add(TrophyData data, String item, int weight, int min, int max) {
        add(data, item, weight, min, max, 0);
    }

    private static void add(TrophyData data, String item, int weight, int min, int max, float luckWeight) {
        TrophyEntry entry = new TrophyEntry();
        entry.item = item;
        entry.weight = weight;
        entry.min = min;
        entry.max = max;
        entry.luckWeight = luckWeight;
        data.entries.add(entry);
    }

    private static TrophyData group(String name, int weight, int min, int max) {
        return group(name, weight, 0,min,max);
    }

    private static TrophyData group(String name, int weight, float luckWeight, int min, int max) {
        TrophyData data = new TrophyData();
        data.name = name;
        data.weight = weight;
        data.luckWeight = luckWeight;

        data.roll_count_min = min;
        data.roll_count_max = max;
        return data;
    }

    private static TrophyData group(String name, int weight) {
        return group(name, weight, 0);
    }

    private static TrophyData group(String name, int weight, float luckWeight) {
        TrophyData data = new TrophyData();
        data.name = name;
        data.weight = weight;
        data.luckWeight = luckWeight;

        data.roll_count_min = 1;
        data.roll_count_max = 1;
        return data;
    }

}
