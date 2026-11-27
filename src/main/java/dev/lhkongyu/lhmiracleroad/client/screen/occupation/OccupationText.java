package dev.lhkongyu.lhmiracleroad.client.screen.occupation;

import dev.lhkongyu.lhmiracleroad.tool.ResourceLocationTool;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class OccupationText {
    private OccupationText() {}

    public static Component description(String occupationId) {
        return Component.translatableWithFallback(ResourceLocationTool.OCCUPATION_DESCRIBE_PREFIX + occupationId, "");
    }

    public static Component rewardName(@Nullable ResourceLocation id, Component fallback) {
        if (id == null) return fallback;

        Language language = Language.getInstance();
        String key = "attribute.name." + id.getNamespace() + "." + id.getPath();
        if (language.has(key)) return Component.translatable(key);

        var attribute = ForgeRegistries.ATTRIBUTES.getValue(id);
        // Existing vanilla reward configs may omit the generic. prefix.
        if (attribute == null && "minecraft".equals(id.getNamespace()) && !id.getPath().contains(".")) {
            attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("minecraft", "generic." + id.getPath()));
        }
        return attribute != null && language.has(attribute.getDescriptionId())
                ? Component.translatable(attribute.getDescriptionId())
                : fallback;
    }
}
