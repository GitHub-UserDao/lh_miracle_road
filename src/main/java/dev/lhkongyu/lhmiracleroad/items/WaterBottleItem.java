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
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class WaterBottleItem extends Item {

    public WaterBottleItem(Properties properties) {
        super(properties);
    }

    public ItemStack getDefaultInstance() {
        return PotionUtils.setPotion(super.getDefaultInstance(), Potions.WATER);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Item item = stack.getItem();
        tooltip.add(Component.translatable(item.getDescriptionId()+".tooltip.details"));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        Item item = itemStack.getItem();
        if (livingEntity instanceof ServerPlayer player){
            switch (item.getDescriptionId()) {
                case "item.lhmiracleroad.forget_water" -> forgetWater(itemStack,player);
                case "item.lhmiracleroad.experience_convert_soul" -> experienceConvertSo0ul(itemStack,player);
            }
        }
        return itemStack;
    }

    public int getUseDuration(ItemStack p_43001_) {
        return 32;
    }

    public UseAnim getUseAnimation(ItemStack p_42997_) {
        return UseAnim.DRINK;
    }

    public InteractionResultHolder<ItemStack> use(Level p_42993_, Player p_42994_, InteractionHand p_42995_) {
        return ItemUtils.startUsingInstantly(p_42993_, p_42994_, p_42995_);
    }

    public String getDescriptionId(ItemStack p_43003_) {
        return PotionUtils.getPotion(p_43003_).getName(this.getDescriptionId() + ".effect.");
    }

    private void forgetWater(ItemStack itemStack,ServerPlayer player){
        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            if (playerOccupationAttribute.getOccupationId() != null && !playerOccupationAttribute.getOccupationId().isEmpty()){
                PlayerAttributeTool.resetLevelReturnPoints(player,playerOccupationAttribute);
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                LHMiracleRoadTool.synchronizationClient(playerOccupationAttribute, player);
            }else player.sendSystemMessage(Component.translatable("lhmiracleroad.instructions.prompt",player.getName()).withStyle(ChatFormatting.RED),false);
        });
    }

    private void experienceConvertSo0ul(ItemStack itemStack,ServerPlayer serverPlayer){
        int totalExperience = serverPlayer.totalExperience;
        serverPlayer.giveExperienceLevels((serverPlayer.experienceLevel + 1) * -1);
        serverPlayer.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            playerOccupationAttribute.addOccupationExperience(totalExperience);
        });
        LHMiracleRoadTool.addItemStack(serverPlayer,Items.GLASS_BOTTLE.getDefaultInstance());
        itemStack.shrink(1);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
