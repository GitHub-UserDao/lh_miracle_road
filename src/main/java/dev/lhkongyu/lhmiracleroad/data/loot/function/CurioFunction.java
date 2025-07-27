package dev.lhkongyu.lhmiracleroad.data.loot.function;

import dev.lhkongyu.lhmiracleroad.data.loot.nbt.CreedTalismanData;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.MiraculousTalismanData;
import dev.lhkongyu.lhmiracleroad.items.curio.CreedTalismanItem;
import dev.lhkongyu.lhmiracleroad.items.curio.MiraculousTalismanItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;

public class CurioFunction implements LootItemFunction {
    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_NBT;
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.getItem() instanceof CreedTalismanItem) {
            CreedTalismanData.setCreedTalismanData(itemStack);
        }else if (itemStack.getItem() instanceof MiraculousTalismanItem){
            MiraculousTalismanData.setMiraculousTalismanData(itemStack);
        }

        return itemStack;
    }

    public static LootItemFunction.Builder builder() {
        return new LootItemFunction.Builder() {
            public LootItemFunction build() {

                return new CurioFunction();
            }
        };
    }
}
