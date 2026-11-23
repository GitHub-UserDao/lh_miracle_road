package dev.lhkongyu.lhmiracleroad.items;

import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.SoulItemConfig;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.SyncTool;
import dev.lhkongyu.lhmiracleroad.tool.particle.ParticleTool;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class SoulItem extends Item {
    public SoulItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        var itemId = ForgeRegistries.ITEMS.getKey(this);
        var rewards = SoulItemConfig.forSide(worldIn == null || worldIn.isClientSide);
        String descriptionId = getDescriptionId();
        if (SoulItemConfig.DEATH_SOUL_ID.equals(itemId)) {
            var reward = rewards.deathSoul();
            tooltip.add(Component.translatable(descriptionId + ".tooltip.details.1").withStyle(ChatFormatting.RED));
            tooltip.add(Component.translatable(descriptionId + ".tooltip.details.2", reward.skillPoints(), reward.multiplier())
                    .withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable(descriptionId + ".tooltip.details.3").withStyle(ChatFormatting.DARK_PURPLE));
        }else {
            Font font = Minecraft.getInstance().font;
            int baseMaxWidth = font.width("K");
            MutableComponent mutableComponent = Component.translatable(descriptionId + ".tooltip.details");
            List<String> strings = LHMiracleRoadTool.baseTextWidthSplitText(font,mutableComponent,baseMaxWidth * 32,0,0);
            for (String string : strings){
                tooltip.add(Component.literal(string));
            }
            Integer amount = rewards.amount(itemId);
            if (amount != null) tooltip.add(Component.translatable("item.lhmiracleroad.soul", amount).withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
        ItemStack itemStack = playerIn.getItemInHand(hand);
        if (playerIn instanceof ServerPlayer serverPlayer) {
            var itemId = ForgeRegistries.ITEMS.getKey(this);
            var rewards = SoulItemConfig.forSide(false);
            if (SoulItemConfig.DEATH_SOUL_ID.equals(itemId)) {
                deathSoul(serverPlayer, itemStack, rewards.deathSoul());
            } else {
                Integer amount = rewards.amount(itemId);
                if (amount != null) addSoul(amount, serverPlayer, itemStack);
            }
        }
        return InteractionResultHolder.consume(itemStack);
    }

    private void addSoul(int amount, ServerPlayer player,ItemStack itemStack){
        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            int soulStart = playerOccupationAttribute.getOccupationExperience();
            int soulEnd = addReward(soulStart, amount);
            playerOccupationAttribute.setOccupationExperience(soulEnd);
            SyncTool.synchronizationSoul(soulEnd, player, soulStart);
            ServerLevel serverLevel = (ServerLevel) player.level();
            if (amount >= 100000) {
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6F, 0.6F);
            }
            else if (amount >= 10000) {
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.4F, 0.5F);
            }
            else if (amount >= 1000){
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.2F, 0.4F);
            }

            ParticleTool.getSoulParticle(serverLevel,player,amount,100);
        });
        itemStack.shrink(1);
    }

    private void deathSoul(ServerPlayer player, ItemStack itemStack, SoulItemConfig.DeathSoul reward) {
        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            int soulStart = playerOccupationAttribute.getOccupationExperience();
            if (soulStart >= 100000){
                ServerLevel serverLevel = (ServerLevel) player.level();
                double luck = LHMiracleRoadTool.getLuckBonus(30.0,player.getLuck());
                int soulEnd;
                if (LHMiracleRoadTool.percentageProbability(50 + luck)){
                    soulEnd = reward.successTotal(soulStart);
                    ParticleTool.getSoulParticle(serverLevel, player, soulEnd, 150);

                    serverLevel.sendParticles(player, ParticleTypes.FLASH, true, player.getX(), player.getY(), player.getZ(), 1, 0.1, 0.1, 0.1,0.1);
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6F, 0.6F);
                }else {
                    serverLevel.sendParticles(player, ParticleTypes.SOUL_FIRE_FLAME, true, player.getX(), player.getY() + player.getBbHeight() * 0.5, player.getZ(), 50, 0.1, 0.1, 0.1,0.1);
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1F, 1F);
                    soulEnd = 0;
                }
                int points = addReward(playerOccupationAttribute.getPoints(), reward.skillPoints());
                playerOccupationAttribute.setOccupationExperience(soulEnd);
                playerOccupationAttribute.setPoints(points);
                SyncTool.synchronizationSoul(soulEnd, player, soulStart, points);
                itemStack.shrink(1);
            }else player.sendSystemMessage(Component.translatable("item.lhmiracleroad.death_soul.prompt").withStyle(ChatFormatting.RED));
        });
    }

    private static int addReward(int current, int reward) {
        return (int) Math.min(Integer.MAX_VALUE, (long) current + reward);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
//        Item item = stack.getItem();
//        return switch (item.getDescriptionId()) {
//            case "item.lhmiracleroad.king_soul","item.lhmiracleroad.death_soul" -> 1;
//            case "item.lhmiracleroad.liege_soul" -> 8;
//            case "item.lhmiracleroad.exhausted_general_soul" -> 16;
//            default -> 64;
//        };
        return 64;
    }
}
