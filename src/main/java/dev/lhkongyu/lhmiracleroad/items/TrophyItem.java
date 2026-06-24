package dev.lhkongyu.lhmiracleroad.items;

import dev.lhkongyu.lhmiracleroad.data.loot.nbt.CreedTalismanData;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.MiraculousTalismanData;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.items.trophy.TrophyManager;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

public class TrophyItem extends Item {

    private final String type;

    public String getType() {
        return type;
    }

    public TrophyItem(Properties properties, String type) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            List<ItemStack> rewards = TrophyManager.getRewards(type, player.getLuck());
            if (!rewards.isEmpty()) {
                for (ItemStack reward : rewards) {
                    if (reward.is(ItemsRegistry.CREED_TALISMAN.get())){
                        CreedTalismanData.setCreedTalismanData(reward);
                    }else if (reward.is(ItemsRegistry.MIRACULOUS_TALISMAN.get())){
                        MiraculousTalismanData.setMiraculousTalismanData(reward);
                    }
                    ItemHandlerHelper.giveItemToPlayer(player, reward);
                }
                stack.shrink(1);
                player.awardStat(Stats.ITEM_USED.get(this));
                player.swing(hand, true);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Item item = stack.getItem();
        tooltip.add(Component.translatable(item.getDescriptionId() + ".tooltip.details"));
        super.appendHoverText(stack,worldIn,tooltip,flagIn);
    }
}
