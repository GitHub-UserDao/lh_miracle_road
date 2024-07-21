package dev.lhkongyu.lhmiracleroad.items.curio;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.lhkongyu.lhmiracleroad.items.curio.ring.*;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class RingItem extends LHMiracleRoadCurioItem{

    public RingItem(Properties properties, Multimap<Attribute, AttributeModifier> defaultModifiers) {
        super(properties,defaultModifiers);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {

        Item item = stack.getItem();
        switch (item.getDescriptionId()) {
            case "item.lhmiracleroad.fire_resistance_ring" -> FireResistanceRing.setEffect(slotContext.entity());
            case "item.lhmiracleroad.poison_invading_ring" -> PoisonInvadingRing.removeEffect(slotContext.entity());
            case "item.lhmiracleroad.water_avoidance_ring" -> WaterAvoidanceRing.setEffect(slotContext.entity());
            case "item.lhmiracleroad.radiance_ring" -> RadianceRing.setEquipRadianceRing(slotContext.entity());
            case "item.lhmiracleroad.vigilance_ring_distant" -> VigilanceRingDistant.setVigilanceRingDistant(slotContext.entity());
            case "item.lhmiracleroad.vigilance_ring_near" -> VigilanceRingNear.setVigilanceRingNear(slotContext.entity());
            case "item.lhmiracleroad.frost_ring" -> FrostRing.ignoreFrost(slotContext.entity());
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        Item item = stack.getItem();
        switch (item.getDescriptionId()) {
            case "item.lhmiracleroad.fire_resistance_ring" -> FireResistanceRing.addEffect(slotContext.entity());
            case "item.lhmiracleroad.water_avoidance_ring" -> WaterAvoidanceRing.addEffect(slotContext.entity());
            case "item.lhmiracleroad.life_ring" -> LifeRing.hpSynchronous((Player) slotContext.entity());
            case "item.lhmiracleroad.radiance_ring" -> RadianceRing.resettingEffect(slotContext.entity());
            case "item.lhmiracleroad.vigilance_ring_distant" -> VigilanceRingDistant.initVigilanceRingDistant(slotContext.entity(),true);
            case "item.lhmiracleroad.vigilance_ring_near" -> VigilanceRingNear.initVigilanceRingNear(slotContext.entity(),true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        Item item = stack.getItem();
        switch (item.getDescriptionId()) {
            case "item.lhmiracleroad.life_ring" -> LifeRing.hpSynchronous((Player) slotContext.entity());
            case "item.lhmiracleroad.radiance_ring" -> RadianceRing.equipRadianceRing(slotContext.entity(),false);
            case "item.lhmiracleroad.vigilance_ring_distant" -> VigilanceRingDistant.initVigilanceRingDistant(slotContext.entity(),false);
            case "item.lhmiracleroad.vigilance_ring_near" -> VigilanceRingNear.initVigilanceRingNear(slotContext.entity(),false);
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

        switch (item.getDescriptionId()) {
            case "item.lhmiracleroad.greedy_gold_serpent_ring","item.lhmiracleroad.radiance_ring" -> {
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
            }
            case "item.lhmiracleroad.endless_desire" -> {
                MutableComponent mutableComponent = Component.translatable(item.getDescriptionId() + ".tooltip.details");
                lines.add(mutableComponent.withStyle(ChatFormatting.YELLOW));
            }
            case "item.lhmiracleroad.vigilance_ring_distant","item.lhmiracleroad.vigilance_ring_near" -> {
                MutableComponent detailsA = Component.translatable(item.getDescriptionId() + ".tooltip.details.1");
                List<String> strings = LHMiracleRoadTool.baseTextWidthSplitText(font,detailsA,baseMaxWidth * 32,0,0);
                for (String string : strings){
                    lines.add(Component.literal(string));
                }
                MutableComponent detailsB = Component.translatable(item.getDescriptionId() + ".tooltip.details.2");
                lines.add(detailsB.withStyle(ChatFormatting.DARK_PURPLE));

                MutableComponent detailsC = Component.translatable(item.getDescriptionId() + ".tooltip.details.3");
                lines.add(detailsC.withStyle(ChatFormatting.DARK_PURPLE));
            }
            default -> {
                MutableComponent mutableComponent = Component.translatable(item.getDescriptionId() + ".tooltip.details");
                List<String> strings = LHMiracleRoadTool.baseTextWidthSplitText(font,mutableComponent,baseMaxWidth * 32,0,0);
                for (String string : strings){
                    lines.add(Component.literal(string));
                }
            }
        }

//        switch (item.getDescriptionId()) {
//            case "item.lhmiracleroad.greedy_gold_serpent_ring" -> Component.translatable(item.getDescriptionId()+".tooltip.describe");
//            case "item.lhmiracleroad.endless_desire" -> ;
//            case "item.lhmiracleroad.radiance_ring" -> ;
//            case "item.lhmiracleroad.greedy_silver_serpent_ring" -> ;
//            case "item.lhmiracleroad.desire_ring" -> ;
//            case "item.lhmiracleroad.vigilance_ring_distant" -> ;
//            case "item.lhmiracleroad.vigilance_ring_near" -> ;
//            case "item.lhmiracleroad.ancient_spellcraft_ring" -> ;
//            case "item.lhmiracleroad.fire_resistance_ring" -> ;
//            case "item.lhmiracleroad.poison_invading_ring" -> ;
//            case "item.lhmiracleroad.water_avoidance_ring" -> ;
//            case "item.lhmiracleroad.frost_ring" -> ;
//            case "item.lhmiracleroad.life_ring" -> ;
//            case "item.lhmiracleroad.recovery_ring" -> ;
//            case "item.lhmiracleroad.ring_strength" -> ;
//            case "item.lhmiracleroad.shielding_ring" -> ;
//        };
    }
}
