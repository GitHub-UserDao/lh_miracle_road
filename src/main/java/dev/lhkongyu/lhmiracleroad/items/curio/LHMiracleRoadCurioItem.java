package dev.lhkongyu.lhmiracleroad.items.curio;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.UUID;

public abstract class LHMiracleRoadCurioItem extends Item implements ICurioItem {

    public abstract String getCurioName();

    protected final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public LHMiracleRoadCurioItem(Properties properties, Multimap<Attribute, AttributeModifier> defaultModifiers) {
        super(properties);
        this.defaultModifiers = defaultModifiers;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder = new ImmutableMultimap.Builder<>();
        if (defaultModifiers != null) {
            for (Attribute attribute : defaultModifiers.keySet()) {
                var modifiers = defaultModifiers.get(attribute);
                for (AttributeModifier attributeModifier : modifiers) {
                    attributeBuilder.put(attribute, new AttributeModifier(uuid, attributeModifier.getName(), attributeModifier.getAmount(), attributeModifier.getOperation()));
                }
            }
        }
        return attributeBuilder.build();
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            boolean success = CuriosApi.getCuriosInventory(player).map(handler -> {
                        ICurioStacksHandler stacksHandler = handler.getCurios().get(getCurioName());
                        if (stacksHandler == null) return false;
                        IDynamicStackHandler stacks = stacksHandler.getStacks();
                        for (int i = 0; i < stacks.getSlots(); i++) {
                            if (stacks.getStackInSlot(i).isEmpty()) {
                                ItemStack copy = stack.copy();
                                copy.setCount(1);
                                stacks.setStackInSlot(i, copy);
                                stack.shrink(1);
                                return true;
                            }
                        }
                        return false;

                    }).orElse(false);

            if (success) return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.sidedSuccess(stack, true);
    }
}
