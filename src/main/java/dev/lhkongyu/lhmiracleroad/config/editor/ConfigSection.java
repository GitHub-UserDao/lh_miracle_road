package dev.lhkongyu.lhmiracleroad.config.editor;

import net.minecraft.network.chat.Component;

import java.util.Locale;

public enum ConfigSection {
    OCCUPATION,
    STRENGTHEN,
    WEAPON_TYPES,
    EQUIPMENT,
    TROPHY,
    COMMON;

    public Component label() {
        return Component.translatable("lhmiracleroad.config_editor.section." + name().toLowerCase(Locale.ROOT));
    }
}
