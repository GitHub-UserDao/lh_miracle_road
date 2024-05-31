package dev.lhkongyu.lhmiracleroad.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

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

        public final ForgeConfigSpec.DoubleValue EMPIRICAL_BASE_MULTIPLIER;

        public final ForgeConfigSpec.IntValue IGNORE_DEATH_PENALTY_PROBABILITY;

        public final ForgeConfigSpec.BooleanValue IS_SKILL_POINTS_RESTRICT;

        public final ForgeConfigSpec.DoubleValue FORGET_WATER_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue DEATH_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue KING_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue EXTREMELY_EVIL_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue EVIL_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue GROUP_SOUL_PROBABILITY;

        public final ForgeConfigSpec.DoubleValue BROKEN_SOUL_PROBABILITY;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("base");
            INIT_BURDEN = builder.comment("init burden").defineInRange("init_burden",60,10,1000);
            LEVEL_BASE =  builder.comment("level base").defineInRange("level_base",10,1,100);
            IS_OFFHAND_CALCULATE_HEAVY = builder.comment("is open offhand heavy").define("is_offhand_calculate_heavy",true);
            PUNISHMENT_LIGHT = builder.comment("light").defineInRange("punishment_light",0.1,-0.99,1);
            PUNISHMENT_NORMAL = builder.comment("normal").defineInRange("punishment_normal",0.0,-0.99,1);
            PUNISHMENT_BIASED_WEIGHT = builder.comment("biased weight").defineInRange("punishment_biased_weight",-0.3,-0.99,1);
            PUNISHMENT_OVERWEIGHT = builder.comment("overweight").defineInRange("punishment_overweight",-0.6,-0.99,1);
            EMPIRICAL_CALCULATION_FORMULA = builder.comment("empirical calculation formula").define("empirical_calculation_formula","min(pow(lv,2.2) + 300 + 20 * (lv + 1), 9999999)");
            EMPIRICAL_BASE_MULTIPLIER = builder.comment("empirical base multiplier").defineInRange("empirical_base_multiplier",8.0,1.0,100.0);
            IGNORE_DEATH_PENALTY_PROBABILITY = builder.comment("ignore punishment probability").defineInRange("ignore_death_penalty_probability",30,1,100);
            IS_SKILL_POINTS_RESTRICT = builder.comment("Enable skill points restrict or not").define("is_skill_points_restrict",true);
            builder.pop();

            builder.push("items");
            FORGET_WATER_PROBABILITY = builder.comment("The probability of Forget Water appearing in a treasure chest").defineInRange("forget_water_probability",0.01,0.001,1);
            DEATH_SOUL_PROBABILITY = builder.comment("The probability of Death Soul appearing in a treasure chest").defineInRange("death_soul_probability",0.08,0.001,1);
            KING_SOUL_PROBABILITY = builder.comment("The probability of King Soul appearing in a treasure chest").defineInRange("king_soul_probability",0.12,0.001,1);
            EXTREMELY_EVIL_SOUL_PROBABILITY = builder.comment("The probability of Extremely Evil Soul appearing in a treasure chest").defineInRange("extremely_evil_soul_probability",0.18,0.001,1);
            EVIL_SOUL_PROBABILITY = builder.comment("The probability of Evil Soul appearing in a treasure chest").defineInRange("evil_soul_probability",0.23,0.001,1);
            GROUP_SOUL_PROBABILITY = builder.comment("The probability of Group Soul appearing in a treasure chest").defineInRange("group_soul_probability",0.35,0.001,1);
            BROKEN_SOUL_PROBABILITY = builder.comment("The probability of Broken Soul appearing in a treasure chest").defineInRange("broken_soul_probability",0.5,0.001,1);
            builder.pop();

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
