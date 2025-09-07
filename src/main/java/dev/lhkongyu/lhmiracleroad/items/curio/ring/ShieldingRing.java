package dev.lhkongyu.lhmiracleroad.items.curio.ring;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.items.curio.LHMiracleRoadCurioItem;
import dev.lhkongyu.lhmiracleroad.items.curio.RingItem;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.ConsecratedCombatPlume;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.HuntingBowTalisman;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.SpanningWings;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.*;

/**
 * 护盾戒指 的饰品功能
 */
public class ShieldingRing extends LHMiracleRoadCurioItem {

    private final AttributeModifier attributeModifier;

    public ShieldingRing(Properties properties, Multimap<Attribute, AttributeModifier> defaultModifiers) {
        super(properties, defaultModifiers);
        attributeModifier = new AttributeModifier(UUID.fromString("1fb71174-8d95-b2ce-6e45-3c567a1969e0"), "", .16, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();

        Optional<ICuriosItemHandler> iCurioOptional = CuriosApi.getCuriosInventory(entity).resolve();
        if (iCurioOptional.isEmpty()) return;
        ICuriosItemHandler iCuriosItemHandler = iCurioOptional.get();
        Map<String, ICurioStacksHandler> iCurioStacksHandlerMap = iCuriosItemHandler.getCurios();
        int count = 0;
        for (String key:iCurioStacksHandlerMap.keySet()){
            for (int i = 0; i < iCurioStacksHandlerMap.get(key).getSlots(); i++) {
                ItemStack itemStack = iCurioStacksHandlerMap.get(key).getStacks().getStackInSlot(i);
                if (itemStack.getDescriptionId().equals(stack.getDescriptionId())){
                    count++;
                }
            }
        }
        if (count == 1){
            Objects.requireNonNull(entity.getAttribute(LHMiracleRoadAttributes.DAMAGE_REDUCTION)).addTransientModifier(attributeModifier);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        Optional<ICuriosItemHandler> iCurioOptional = CuriosApi.getCuriosInventory(entity).resolve();
        if (iCurioOptional.isEmpty()) return;
        ICuriosItemHandler iCuriosItemHandler = iCurioOptional.get();
        Map<String, ICurioStacksHandler> iCurioStacksHandlerMap = iCuriosItemHandler.getCurios();
        int count = 0;
        for (String key:iCurioStacksHandlerMap.keySet()){
            for (int i = 0; i < iCurioStacksHandlerMap.get(key).getSlots(); i++) {
                ItemStack itemStack = iCurioStacksHandlerMap.get(key).getStacks().getStackInSlot(i);
                if (itemStack.getDescriptionId().equals(stack.getDescriptionId())){
                    count++;
                }
            }
        }

        if (count == 0){
            Objects.requireNonNull(entity.getAttribute(LHMiracleRoadAttributes.DAMAGE_REDUCTION)).removeModifier(attributeModifier);
        }
    }

    /**
     *
     * @param stack
     * @param pLevel
     * @param lines
     * @param pIsAdvanced
     */
    @Override
    public void appendHoverText(ItemStack stack, Level pLevel, List<Component> lines,TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, lines, pIsAdvanced);
        Item item = stack.getItem();
        Font font = Minecraft.getInstance().font;
        int baseMaxWidth = font.width("K");

        MutableComponent detailsA = Component.translatable(item.getDescriptionId() + ".tooltip.details.1");
        List<String> strings = LHMiracleRoadTool.baseTextWidthSplitText(font,detailsA,baseMaxWidth * 32,0,0);
        for (String string : strings){
            lines.add(Component.literal(string));
        }
        MutableComponent detailsB = Component.translatable(item.getDescriptionId() + ".tooltip.details.2");
        strings = LHMiracleRoadTool.baseTextWidthSplitText(font,detailsB,baseMaxWidth * 32,0,0);
        for (String string : strings){
            lines.add(Component.literal(string).withStyle(ChatFormatting.YELLOW));
        }

        MutableComponent detailsC = Component.translatable(item.getDescriptionId() + ".tooltip.details.3");
        strings = LHMiracleRoadTool.baseTextWidthSplitText(font,detailsC,baseMaxWidth * 32,0,0);
        for (String string : strings){
            lines.add(Component.literal(string).withStyle(ChatFormatting.RED));
        }
    }
}
