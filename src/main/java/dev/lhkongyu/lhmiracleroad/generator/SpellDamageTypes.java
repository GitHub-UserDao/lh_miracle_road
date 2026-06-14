package dev.lhkongyu.lhmiracleroad.generator;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
public class SpellDamageTypes {

    public static final ResourceKey<DamageType> MAGIC =  register("magic");
    public static final ResourceKey<DamageType> FLAME_MAGIC =  register("flame_magic");
    public static final ResourceKey<DamageType> DARK_MAGIC =  register("dark_magic");
    public static final ResourceKey<DamageType> LIGHTNING_MAGIC =  register("lightning_magic");
    public static final ResourceKey<DamageType> HOLY_MAGIC =  register("holy_magic");
    public static final ResourceKey<DamageType> SOUL_MAGIC =  register("soul_magic");

    public static final ResourceKey<DamageType> ABNORMAL_BLEED =  register("abnormal_bleed");
    public static final ResourceKey<DamageType> ABNORMAL_FROST =  register("abnormal_frost");
    public static final ResourceKey<DamageType> ABNORMAL_POISON =  register("abnormal_poison");
    public static final ResourceKey<DamageType> ABNORMAL_BURN =  register("abnormal_burn");


    public static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(LHMiracleRoad.MODID, name));
    }

    public static void bootstrap(BootstapContext<DamageType> context) {
        //属性伤害类型
        context.register(MAGIC, new DamageType(MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
        context.register(FLAME_MAGIC, new DamageType(FLAME_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
        context.register(DARK_MAGIC, new DamageType(DARK_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
        context.register(LIGHTNING_MAGIC, new DamageType(LIGHTNING_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
        context.register(HOLY_MAGIC, new DamageType(HOLY_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
        context.register(SOUL_MAGIC, new DamageType(SOUL_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));


        //异常伤害类型
        context.register(ABNORMAL_BLEED, new DamageType(ABNORMAL_BLEED.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
        context.register(ABNORMAL_FROST, new DamageType(ABNORMAL_FROST.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
        context.register(ABNORMAL_POISON, new DamageType(ABNORMAL_POISON.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
        context.register(ABNORMAL_BURN, new DamageType(ABNORMAL_BURN.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
    }
}
