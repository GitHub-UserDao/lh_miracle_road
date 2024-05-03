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

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("base");
            INIT_BURDEN = builder.comment("init_burden").defineInRange("init_burden",60,10,1000);
            LEVEL_BASE =  builder.comment("level_base").defineInRange("level_base",10,1,10);
            IS_OFFHAND_CALCULATE_HEAVY = builder.comment("is_offhand_calculate_heavy").define("is_offhand_calculate_heavy",true);
            PUNISHMENT_LIGHT = builder.comment("punishment_light").defineInRange("punishment_light",0.1,-0.99,1);
            PUNISHMENT_NORMAL = builder.comment("punishment_normal").defineInRange("punishment_normal",0.0,-0.99,1);
            PUNISHMENT_BIASED_WEIGHT = builder.comment("punishment_biased_weight").defineInRange("punishment_biased_weight",-0.3,-0.99,1);
            PUNISHMENT_OVERWEIGHT = builder.comment("punishment_overweight").defineInRange("punishment_overweight",-0.6,-0.99,1);
            EMPIRICAL_CALCULATION_FORMULA = builder.comment("empirical_calculation_formula").define("empirical_calculation_formula","min(pow(lv,2.2) + 180 + 18 * (lv + 1), 9999999)");
            EMPIRICAL_BASE_MULTIPLIER = builder.comment("empirical_base_multiplier").defineInRange("empirical_base_multiplier",10.0,1.0,100.0);
            IGNORE_DEATH_PENALTY_PROBABILITY = builder.comment("ignore_death_penalty_probability").defineInRange("ignore_death_penalty_probability",30,1,100);
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
