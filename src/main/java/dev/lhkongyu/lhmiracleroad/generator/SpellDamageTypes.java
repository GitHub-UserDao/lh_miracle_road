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


    public static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(LHMiracleRoad.MODID, name));
    }

    public static void bootstrap(BootstapContext<DamageType> context) {

        context.register(MAGIC, new DamageType(MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));

        context.register(FLAME_MAGIC, new DamageType(FLAME_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));

        context.register(DARK_MAGIC, new DamageType(DARK_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));

        context.register(LIGHTNING_MAGIC, new DamageType(LIGHTNING_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));

        context.register(HOLY_MAGIC, new DamageType(HOLY_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
    }
}
