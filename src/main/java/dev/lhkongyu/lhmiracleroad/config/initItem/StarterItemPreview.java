package dev.lhkongyu.lhmiracleroad.config.initItem;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public record StarterItemPreview(Map<EquipmentSlot, ItemStack> equipment, List<ItemStack> inventory) {
    public static final StarterItemPreview EMPTY = new StarterItemPreview(Map.of(), List.of());

    public StarterItemPreview {
        equipment = Map.copyOf(equipment);
        inventory = List.copyOf(inventory);
    }
}
