package dev.lhkongyu.lhmiracleroad.items;

import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class SoulItem extends Item {
    public SoulItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Item item = stack.getItem();
        if (item.getDescriptionId().equals("item.lhmiracleroad.death_soul")){
            tooltip.add(Component.translatable(item.getDescriptionId() + ".tooltip.details.1").withStyle(ChatFormatting.RED));
            tooltip.add(Component.translatable(item.getDescriptionId() + ".tooltip.details.2").withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable(item.getDescriptionId() + ".tooltip.details.3").withStyle(ChatFormatting.DARK_PURPLE));
        }else {
            tooltip.add(Component.translatable(item.getDescriptionId() + ".tooltip.details"));
            int amount = switch (item.getDescriptionId()) {
                case "item.lhmiracleroad.broken_soul" -> 100;
                case "item.lhmiracleroad.group_soul" -> 500;
                case "item.lhmiracleroad.evil_soul" -> 2000;
                case "item.lhmiracleroad.extremely_evil_soul" -> 5000;
                case "item.lhmiracleroad.king_soul" -> 30000;
                default -> 0;
            };
            tooltip.add(Component.translatable("item.lhmiracleroad.soul",amount).withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
        ItemStack itemStack = playerIn.getItemInHand(hand);
        Item item = itemStack.getItem();
        if (playerIn instanceof ServerPlayer serverPlayer)
            switch (item.getDescriptionId()){
                case "item.lhmiracleroad.broken_soul" -> addSoul(100,serverPlayer,itemStack);
                case "item.lhmiracleroad.group_soul" -> addSoul(500,serverPlayer,itemStack);
                case "item.lhmiracleroad.evil_soul" -> addSoul(2000,serverPlayer,itemStack);
                case "item.lhmiracleroad.extremely_evil_soul" -> addSoul(5000,serverPlayer,itemStack);
                case "item.lhmiracleroad.king_soul" -> addSoul(30000,serverPlayer,itemStack);
                case "item.lhmiracleroad.death_soul" -> deathSoul(serverPlayer,itemStack);
            }
        return InteractionResultHolder.consume(itemStack);
    }

    private void addSoul(int amount, ServerPlayer player,ItemStack itemStack){
        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            playerOccupationAttribute.addOccupationExperience(amount);
        });
        itemStack.shrink(1);
    }

    private void deathSoul(ServerPlayer player,ItemStack itemStack){
        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            if (playerOccupationAttribute.getOccupationExperience() >= 100000){
                if (LHMiracleRoadTool.percentageProbability(50)){
                    playerOccupationAttribute.addOccupationExperience(playerOccupationAttribute.getOccupationExperience());
                    playerOccupationAttribute.addPoints(1);
                }else {
                    playerOccupationAttribute.setOccupationExperience(0);
                }
                itemStack.shrink(1);
            }else player.sendSystemMessage(Component.translatable("item.lhmiracleroad.death_soul.prompt").withStyle(ChatFormatting.RED));
        });
    }
}
