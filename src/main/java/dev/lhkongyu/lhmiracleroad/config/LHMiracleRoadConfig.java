package dev.lhkongyu.lhmiracleroad.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public class LHMiracleRoadConfig {

    public static class Common {
        public final ForgeConfigSpec.IntValue INIT_BURDEN;

        public final ForgeConfigSpec.IntValue LEVEL_BASE;

        public final ForgeConfigSpec.BooleanValue IS_OFFHAND_CALCULATE_HEAVY;

        public final ForgeConfigSpec.DoubleValue PUNISHMENT_LIGHT;

        public final ForgeConfigSpec.DoubleValue PUNISHMENT_NORMAL;

        public final ForgeConfigSpec.DoubleValue PUNISHMENT_BIASED_WEIGHT;

        public final ForgeConfigSpec.DoubleValue PUNISHMENT_OVERWEIGHT;

        public final ForgeConfigSpec.ConfigValue<String> EMPIRICAL_CALCULATION_FORMULA;

        public final ForgeConfigSpec.ConfigValue<String> EXPERIENCE_ACQUISITION_FORMULA;

//        public final ForgeConfigSpec.DoubleValue EMPIRICAL_BASE_MULTIPLIER;

        public final ForgeConfigSpec.DoubleValue SOUL_LOSS_COUNT;

        public final ForgeConfigSpec.BooleanValue IS_SKILL_POINTS_RESTRICT;

        public final ForgeConfigSpec.DoubleValue FORGET_WATER_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue DEATH_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue KING_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue LARGE_BLOCK_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue STRAY_LARGE_BLOCK_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue ADVENTURER_LARGE_BLOCK_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue UNKNOWN_SOLDIER_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue UNKNOWN_SOLDIER_LARGE_BLOCK_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue EXHAUSTED_KNIGHT_SOUL_PROBABILITY;

        public final ForgeConfigSpec.IntValue ATTRIBUTE_MAX_LEVEL;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> ORDINARY_MOB;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> ELITE_MOB;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> SPECIAL_ELITE_MOB;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> LIEGE_MOB;

        public final ForgeConfigSpec.DoubleValue ORDINARY_LOOT_SOON_ELAPSE_SOUL_ODDS;

        public final ForgeConfigSpec.DoubleValue ORDINARY_LOOT_INCOMPLETE_SOUL_ODDS;

        public final ForgeConfigSpec.DoubleValue ELITE_LOOT_SOUL_ODDS;

        public final ForgeConfigSpec.DoubleValue SPECIAL_ELITE_LOOT_SOUL_ODDS;

        public final ForgeConfigSpec.DoubleValue LIEGE_LOOT_SOUL_ODDS;

        public final ForgeConfigSpec.DoubleValue ENDER_DRAGON_FORGET_WATER_ODDS;

        public final ForgeConfigSpec.IntValue MAX_LEVEL;

        public final ForgeConfigSpec.BooleanValue DARK_SOUL;

        public final ForgeConfigSpec.BooleanValue IS_EXP_SHARING;

        public final ForgeConfigSpec.DoubleValue EXP_SHARING_PERCENTAGE;

        public final ForgeConfigSpec.ConfigValue<String> GEMSTONE_STRENGTHEN_ONE;

        public final ForgeConfigSpec.ConfigValue<String> GEMSTONE_STRENGTHEN_TWO;

        public final ForgeConfigSpec.ConfigValue<String> GEMSTONE_STRENGTHEN_THREE;

        public final ForgeConfigSpec.ConfigValue<String> GEMSTONE_STRENGTHEN_FOUR;

        public final ForgeConfigSpec.ConfigValue<String> GEMSTONE_STRENGTHEN_FIVE;

        public final ForgeConfigSpec.ConfigValue<String> GEMSTONE_STRENGTHEN_SIX;

        public final ForgeConfigSpec.ConfigValue<String> GEMSTONE_STRENGTHEN_SEVEN;

        public final ForgeConfigSpec.ConfigValue<String> GEMSTONE_STRENGTHEN_EIGHT;

        public final ForgeConfigSpec.ConfigValue<String> GEMSTONE_STRENGTHEN_NINE;

        public final ForgeConfigSpec.ConfigValue<String> GEMSTONE_STRENGTHEN_TEN;


        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("base");
            INIT_BURDEN = builder.comment("init burden").defineInRange("init_burden",60,10,1000);
            LEVEL_BASE =  builder.comment("level base").defineInRange("level_base",10,1,100);
            IS_OFFHAND_CALCULATE_HEAVY = builder.comment("is open offhand heavy").define("is_offhand_calculate_heavy",true);
            PUNISHMENT_LIGHT = builder.comment("light").defineInRange("punishment_light",0.1,-0.99,1);
            PUNISHMENT_NORMAL = builder.comment("normal").defineInRange("punishment_normal",0.0,-0.99,1);
            PUNISHMENT_BIASED_WEIGHT = builder.comment("biased weight").defineInRange("punishment_biased_weight",-0.3,-0.99,1);
            PUNISHMENT_OVERWEIGHT = builder.comment("overweight").defineInRange("punishment_overweight",-0.6,-0.99,1);
            EMPIRICAL_CALCULATION_FORMULA = builder.comment("empirical calculation formula").define("empirical_calculation_formula","min(pow(3.12 * lv,2) + 268.4 * lv, 19999999)");
//            EMPIRICAL_BASE_MULTIPLIER = builder.comment("empirical base multiplier").defineInRange("empirical_base_multiplier",8.0,1.0,100.0);
            EXPERIENCE_ACQUISITION_FORMULA = builder.comment("experience acquisition formula").define("experience_acquisition_formula","(exp / 8 + max(hp,2) * 10) * (1 + atk * 0.15 + arm * 0.1 + atou * 0.15 + buff * 0.05)");
            SOUL_LOSS_COUNT = builder.comment("The percentage amount of soul loss: 1 means no loss, 0 means loss is cleared, and 0.5 means half of the loss.").defineInRange("soul_loss_count",0.0,0,1);
            IS_SKILL_POINTS_RESTRICT = builder.comment("Enable skill points restrict or not").define("is_skill_points_restrict",true);
            ATTRIBUTE_MAX_LEVEL = builder.comment("Enable skill points restrict or not").defineInRange("attribute_max_level",0,0,Integer.MAX_VALUE);
            MAX_LEVEL = builder.comment("player max level").defineInRange("max_level",Integer.MAX_VALUE,0,Integer.MAX_VALUE);
            DARK_SOUL = builder.comment("When Dark Soul mode is enabled, dying will always erase the soul remnant from your previous death.").define("dark_soul",false);
            IS_EXP_SHARING = builder.comment("Allow nearby players to share souls?").define("is_exp_sharing",true);
            EXP_SHARING_PERCENTAGE = builder.comment("Percentage of souls gained by other players.").defineInRange("exp_sharing_percentage",0.3,0,1);
            builder.pop();

            builder.push("items loot probability");
            FORGET_WATER_PROBABILITY = builder.comment("The probability of Forget Water appearing in a treasure chest").defineInRange("forget_water_probability",0.05,0,1);
            DEATH_SOUL_PROBABILITY = builder.comment("The probability of Death Soul appearing in a treasure chest").defineInRange("death_soul_probability",0.06,0,1);
            KING_SOUL_PROBABILITY = builder.comment("The probability of King Soul appearing in a treasure chest").defineInRange("king_soul_probability",0.06,0,1);

            LARGE_BLOCK_SOUL_PROBABILITY = builder.comment("The probability of Large Block Soul appearing in a treasure chest").defineInRange("large_block_soul",0.35,0,1);
            STRAY_LARGE_BLOCK_SOUL_PROBABILITY = builder.comment("The probability of Stray Large Block Soul appearing in a treasure chest").defineInRange("stray_large_block_soul",0.3,0,1);
            ADVENTURER_LARGE_BLOCK_SOUL_PROBABILITY = builder.comment("The probability of Adventurer Large Block Soul appearing in a treasure chest").defineInRange("adventurer_large_block_soul",0.25,0,1);
            UNKNOWN_SOLDIER_SOUL_PROBABILITY = builder.comment("The probability of Unknown Soldier Soul appearing in a treasure chest").defineInRange("unknown_soldier_soul",0.2,0,1);
            UNKNOWN_SOLDIER_LARGE_BLOCK_SOUL_PROBABILITY = builder.comment("The probability of Unknown Soldier Large Block Soul appearing in a treasure chest").defineInRange("unknown_soldier_large_block_soul",0.15,0,1);
            EXHAUSTED_KNIGHT_SOUL_PROBABILITY = builder.comment("The probability of Exhausted Knight Soul appearing in a treasure chest").defineInRange("exhausted_knight_soul",0.1,0,1);
            builder.pop();

            builder.push("mob loot");

            ORDINARY_MOB = builder.comment("ordinary mob").defineList("ordinary_mob", initOrdinaryMobList(),(string) -> true);
            ORDINARY_LOOT_SOON_ELAPSE_SOUL_ODDS = builder.comment("The drop rate of the 'Soon Elapse Soul' from Ordinary Mobs").defineInRange("ordinary_loot_soon_elapse_soul_odds",0.06,0,1.0);
            ORDINARY_LOOT_INCOMPLETE_SOUL_ODDS = builder.comment("The drop rate of 'Incomplete Soul' from Ordinary Mobs").defineInRange("ordinary_loot_incomplete_soul_odds",0.03,0,1.0);

            ELITE_MOB = builder.comment("elite mob").defineList("elite_mob", initEliteMobList(),(string) -> true);
            ELITE_LOOT_SOUL_ODDS = builder.comment("The drop rate of 'Large Block Soul' from Elite Mobs").defineInRange("elite_loot_soul_odds",0.5,0,1.0);

            SPECIAL_ELITE_MOB = builder.comment("special elite mob").defineList("special_elite_mob", initSpecialEliteMobList(),(string) -> true);
            SPECIAL_ELITE_LOOT_SOUL_ODDS = builder.comment("The drop rate of 'Exhausted General Soul' from Special Elite Mobs").defineInRange("special_elite_loot_soul_odds",0.5,0,1.0);

            LIEGE_MOB = builder.comment("liege level mob").defineList("liege_mob", initLiegeMobList(),(string) -> true);
            LIEGE_LOOT_SOUL_ODDS = builder.comment("The drop rate of 'Liege Soul' from Liege Level Mob").defineInRange("liege_loot_soul_odds",1.0,0,1.0);

            ENDER_DRAGON_FORGET_WATER_ODDS = builder.comment("The drop rate of 'Forget Water' from Ender Dragon").defineInRange("ender_dragon_forget_water_odds",0.5,0,1.0);
            builder.pop();

            builder.push("gemstone strengthen").comment("CN:每级强化配置").comment("US:Per-level strengthen config");
            Queue<String> initGemstoneStrengthenList = getinitGemstoneStrengthenQueue();
            GEMSTONE_STRENGTHEN_ONE = builder
                    .comment("LV 1")
                    .define("gemstone_strengthen_one", initGemstoneStrengthenList.poll());

            GEMSTONE_STRENGTHEN_TWO = builder
                    .comment("LV 2")
                    .define("gemstone_strengthen_two", initGemstoneStrengthenList.poll());

            GEMSTONE_STRENGTHEN_THREE = builder
                    .comment("LV 3")
                    .define("gemstone_strengthen_three", initGemstoneStrengthenList.poll());

            GEMSTONE_STRENGTHEN_FOUR = builder
                    .comment("LV 4")
                    .define("gemstone_strengthen_four", initGemstoneStrengthenList.poll());

            GEMSTONE_STRENGTHEN_FIVE = builder
                    .comment("LV 5")
                    .define("gemstone_strengthen_five", initGemstoneStrengthenList.poll());

            GEMSTONE_STRENGTHEN_SIX = builder
                    .comment("LV 6")
                    .define("gemstone_strengthen_six", initGemstoneStrengthenList.poll());

            GEMSTONE_STRENGTHEN_SEVEN = builder
                    .comment("LV 7")
                    .define("gemstone_strengthen_seven", initGemstoneStrengthenList.poll());

            GEMSTONE_STRENGTHEN_EIGHT = builder
                    .comment("LV 8")
                    .define("gemstone_strengthen_eight", initGemstoneStrengthenList.poll());

            GEMSTONE_STRENGTHEN_NINE = builder
                    .comment("LV 9")
                    .define("gemstone_strengthen_nine", initGemstoneStrengthenList.poll());

            GEMSTONE_STRENGTHEN_TEN = builder
                    .comment("LV 10")
                    .define("gemstone_strengthen_ten", initGemstoneStrengthenList.poll());

            builder.pop();

        }

        private static List<String> initLiegeMobList(){
            List<String> liegeMobList = Lists.newArrayList();
            liegeMobList.add("wither");
            liegeMobList.add("ender_dragon");
            liegeMobList.add("dead_king");
            return liegeMobList;
        }

        private static List<String> initSpecialEliteMobList(){
            List<String> specialEliteMobList = Lists.newArrayList();
            specialEliteMobList.add("warden");
            return specialEliteMobList;
        }

        private static List<String> initEliteMobList(){
            List<String> eliteMobList = Lists.newArrayList();
            eliteMobList.add("piglin_brute");
            eliteMobList.add("evoker");
            eliteMobList.add("elder_guardian");
            eliteMobList.add("ravager");
            eliteMobList.add("cryomancer");
            eliteMobList.add("pyromancer");
            eliteMobList.add("priest");
            eliteMobList.add("archevoker");
            eliteMobList.add("apothecarist");
            eliteMobList.add("keeper");
            return eliteMobList;
        }

        private static List<String> initOrdinaryMobList(){
            List<String> ordinaryMobList = Lists.newArrayList();
            ordinaryMobList.add("zombie");
            ordinaryMobList.add("zombie_villager");
            ordinaryMobList.add("zombified_piglin");
            ordinaryMobList.add("skeleton");
            ordinaryMobList.add("wither_skeleton");
            ordinaryMobList.add("stray");
            ordinaryMobList.add("drowned");
            ordinaryMobList.add("witch");
            ordinaryMobList.add("husk");
            ordinaryMobList.add("guardian");
            ordinaryMobList.add("spider");
            ordinaryMobList.add("cave_spider");
            ordinaryMobList.add("creeper");
            ordinaryMobList.add("pillager");
            ordinaryMobList.add("enderman");
            ordinaryMobList.add("blaze");
            ordinaryMobList.add("necromancer");
            return ordinaryMobList;
        }

        private static Queue<String> getinitGemstoneStrengthenQueue(){
            Queue<String> ordinaryMobList = Lists.newLinkedList();
            Gson gson = new Gson();
            for (int i = 1; i <= 6; i++) {
                JsonObject lv = new JsonObject();
                lv.addProperty("attack", 0.25);
                lv.addProperty("attack_speed", 0.02);
                lv.addProperty("ranged", 0.03);
                lv.addProperty("armor", 0.2);
                lv.addProperty("armor_toughness", 0.1);
                if (i > 3) lv.addProperty("durability", 1.25);
                else lv.addProperty("durability", 1.1);
                ordinaryMobList.add(gson.toJson(lv));
            }

            for (int i = 7; i <= 9; i++) {
                JsonObject lv = new JsonObject();
                lv.addProperty("attack", 0.25);
                lv.addProperty("attack_speed", 0.03);
                lv.addProperty("ranged", 0.04);
                lv.addProperty("armor", 0.4);
                lv.addProperty("armor_toughness", 0.2);
                lv.addProperty("durability", 1.5);
                ordinaryMobList.add(gson.toJson(lv));
            }

            JsonObject lv = new JsonObject();
            lv.addProperty("attack", 0.75);
            lv.addProperty("attack_speed", 0.04);
            lv.addProperty("ranged", 0.1);
            lv.addProperty("armor", 0.6);
            lv.addProperty("armor_toughness", 0.3);
            lv.addProperty("durability", 2);
            ordinaryMobList.add(gson.toJson(lv));

            return ordinaryMobList;
        }
    }

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = common.getRight();
        COMMON = common.getLeft();
    }
}
