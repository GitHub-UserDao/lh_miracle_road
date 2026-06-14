package dev.lhkongyu.lhmiracleroad.generator;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SpellDamageTypeTagGenerator extends TagsProvider<DamageType> {
    public SpellDamageTypeTagGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, LHMiracleRoad.MODID, existingFileHelper);
    }

    private static TagKey<DamageType> create(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(LHMiracleRoad.MODID, name));
    }
    public static final TagKey<DamageType> MAGIC = create("magic");
    public static final TagKey<DamageType> FLAME_MAGIC = create("flame_magic");
    public static final TagKey<DamageType> DARK_MAGIC = create("dark_magic");
    public static final TagKey<DamageType> LIGHTNING_MAGIC = create("lightning_magic");
    public static final TagKey<DamageType> HOLY_MAGIC = create("holy_magic");
    public static final TagKey<DamageType> SOUL_MAGIC = create("soul_magic");

    public static final TagKey<DamageType> ABNORMAL_BLEED = create("abnormal_bleed");
    public static final TagKey<DamageType> ABNORMAL_FROST = create("abnormal_frost");
    public static final TagKey<DamageType> ABNORMAL_POISON = create("abnormal_poison");
    public static final TagKey<DamageType> ABNORMAL_BURN = create("abnormal_burn");


    @Override
    protected void addTags(Provider provider) {
        tag(DamageTypeTags.BYPASSES_ARMOR)
                .replace(false)
                .add(SpellDamageTypes.MAGIC)
                .add(SpellDamageTypes.FLAME_MAGIC)
                .add(SpellDamageTypes.DARK_MAGIC)
                .add(SpellDamageTypes.LIGHTNING_MAGIC)
                .add(SpellDamageTypes.HOLY_MAGIC)
                .add(SpellDamageTypes.SOUL_MAGIC)
                .add(SpellDamageTypes.ABNORMAL_BLEED)
                .add(SpellDamageTypes.ABNORMAL_FROST)
                .add(SpellDamageTypes.ABNORMAL_POISON)
                .add(SpellDamageTypes.ABNORMAL_BURN);

        tag(DamageTypeTags.AVOIDS_GUARDIAN_THORNS)
                .replace(false)
                .add(SpellDamageTypes.MAGIC)
                .add(SpellDamageTypes.FLAME_MAGIC)
                .add(SpellDamageTypes.DARK_MAGIC)
                .add(SpellDamageTypes.LIGHTNING_MAGIC)
                .add(SpellDamageTypes.HOLY_MAGIC)
                .add(SpellDamageTypes.SOUL_MAGIC)
                .add(SpellDamageTypes.ABNORMAL_BLEED)
                .add(SpellDamageTypes.ABNORMAL_FROST)
                .add(SpellDamageTypes.ABNORMAL_POISON)
                .add(SpellDamageTypes.ABNORMAL_BURN);

        tag(DamageTypeTags.BYPASSES_COOLDOWN)
                .replace(false)
                .add(SpellDamageTypes.MAGIC)
                .add(SpellDamageTypes.FLAME_MAGIC)
                .add(SpellDamageTypes.DARK_MAGIC)
                .add(SpellDamageTypes.LIGHTNING_MAGIC)
                .add(SpellDamageTypes.HOLY_MAGIC)
                .add(SpellDamageTypes.SOUL_MAGIC)
                .add(SpellDamageTypes.ABNORMAL_BLEED)
                .add(SpellDamageTypes.ABNORMAL_FROST)
                .add(SpellDamageTypes.ABNORMAL_POISON)
                .add(SpellDamageTypes.ABNORMAL_BURN);

        //属性伤害类型
        tag(SpellDamageTypeTagGenerator.MAGIC).replace(false).add(SpellDamageTypes.MAGIC);
        tag(SpellDamageTypeTagGenerator.FLAME_MAGIC).replace(false).add(SpellDamageTypes.FLAME_MAGIC);
        tag(SpellDamageTypeTagGenerator.DARK_MAGIC).replace(false).add(SpellDamageTypes.DARK_MAGIC);
        tag(SpellDamageTypeTagGenerator.LIGHTNING_MAGIC).replace(false).add(SpellDamageTypes.LIGHTNING_MAGIC);
        tag(SpellDamageTypeTagGenerator.HOLY_MAGIC).replace(false).add(SpellDamageTypes.HOLY_MAGIC);
        tag(SpellDamageTypeTagGenerator.SOUL_MAGIC).replace(false).add(SpellDamageTypes.SOUL_MAGIC);

        //异常伤害类型
        tag(SpellDamageTypeTagGenerator.ABNORMAL_BLEED).replace(false).add(SpellDamageTypes.ABNORMAL_BLEED);
        tag(SpellDamageTypeTagGenerator.ABNORMAL_FROST).replace(false).add(SpellDamageTypes.ABNORMAL_FROST);
        tag(SpellDamageTypeTagGenerator.ABNORMAL_POISON).replace(false).add(SpellDamageTypes.ABNORMAL_POISON);
        tag(SpellDamageTypeTagGenerator.ABNORMAL_BURN).replace(false).add(SpellDamageTypes.ABNORMAL_BURN);
    }
}