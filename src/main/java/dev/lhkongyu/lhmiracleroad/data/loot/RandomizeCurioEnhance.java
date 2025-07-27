package dev.lhkongyu.lhmiracleroad.data.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import dev.lhkongyu.lhmiracleroad.items.curio.CreedTalismanItem;
import dev.lhkongyu.lhmiracleroad.registry.LootRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class RandomizeCurioEnhance extends LootItemConditionalFunction {
    protected RandomizeCurioEnhance(LootItemCondition[] lootConditions) {
        super(lootConditions);
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.getItem() instanceof CreedTalismanItem) {
            CompoundTag nbt = itemStack.getOrCreateTag();
            nbt.putString("ky","123");
            itemStack.setTag(nbt);
        }
        return itemStack;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootRegistry.RANDOMIZE_CURIO_ENHANCE.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<RandomizeCurioEnhance> {
        public void serialize(JsonObject json, RandomizeCurioEnhance scrollFunction, JsonSerializationContext jsonDeserializationContext) {
            super.serialize(json, scrollFunction, jsonDeserializationContext);
        }

        public RandomizeCurioEnhance deserialize(JsonObject json, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootConditions) {
            return new RandomizeCurioEnhance(lootConditions);
        }
    }
}
