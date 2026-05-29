package dev.lhkongyu.lhmiracleroad.registry;

import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TagsRegistry {
    public static final TagKey<Item> GEM =
            ItemTags.create(LHMiracleRoadTool.resourceLocationId("gem"));

    public static final TagKey<Item> STRENGTHEN_GEM =
            ItemTags.create(LHMiracleRoadTool.resourceLocationId("strengthen_gem"));

    public static final TagKey<Item> HAMMERS =
            ItemTags.create(LHMiracleRoadTool.resourceLocationId("hammers"));

    public static final TagKey<Item> WEAPONS =
            ItemTags.create(LHMiracleRoadTool.resourceLocationId("weapons"));

    public static final TagKey<Item> RANGED_WEAPONS =
            ItemTags.create(LHMiracleRoadTool.resourceLocationId("ranged_weapons"));
}
