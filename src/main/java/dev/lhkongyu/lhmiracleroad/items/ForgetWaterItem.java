package dev.lhkongyu.lhmiracleroad.items;

import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ForgetWaterItem extends PotionItem {

    public ForgetWaterItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Item item = stack.getItem();
        tooltip.add(Component.translatable(item.getDescriptionId()+".tooltip.details"));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player){
            player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
                if (playerOccupationAttribute.getOccupationId() != null && !playerOccupationAttribute.getOccupationId().isEmpty()){
                    PlayerAttributeTool.resetLevelReturnPoints(player,playerOccupationAttribute);
                    itemStack.shrink(1);
                }else player.sendSystemMessage(Component.translatable("lhmiracleroad.instructions.prompt",player.getName()).withStyle(ChatFormatting.RED),false);
            });
        }
        return itemStack;
    }
}
