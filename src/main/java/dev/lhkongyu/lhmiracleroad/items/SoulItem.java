package dev.lhkongyu.lhmiracleroad.items;

import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
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

import javax.annotation.Nullable;
import java.util.List;

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
            Font font = Minecraft.getInstance().font;
            int baseMaxWidth = font.width("K");
            MutableComponent mutableComponent = Component.translatable(item.getDescriptionId() + ".tooltip.details");
            List<String> strings = LHMiracleRoadTool.baseTextWidthSplitText(font,mutableComponent,baseMaxWidth * 32,0,0);
            for (String string : strings){
                tooltip.add(Component.literal(string));
            }
            int amount = switch (item.getDescriptionId()) {
                case "item.lhmiracleroad.king_soul" -> 100000;
                case "item.lhmiracleroad.soon_elapse_soul" -> 100;
                case "item.lhmiracleroad.incomplete_soul" -> 300;
                case "item.lhmiracleroad.large_block_soul" -> 1000;
                case "item.lhmiracleroad.stray_large_block_soul" -> 1500;
                case "item.lhmiracleroad.adventurer_large_block_soul" -> 2000;
                case "item.lhmiracleroad.unknown_soldier_soul" -> 4000;
                case "item.lhmiracleroad.unknown_soldier_large_block_soul" -> 8000;
                case "item.lhmiracleroad.exhausted_knight_soul" -> 15000;
                case "item.lhmiracleroad.exhausted_general_soul" -> 30000;
                case "item.lhmiracleroad.liege_soul" -> 50000;
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
                case "item.lhmiracleroad.king_soul" -> addSoul(100000,serverPlayer,itemStack);
                case "item.lhmiracleroad.soon_elapse_soul" -> addSoul(100,serverPlayer,itemStack);
                case "item.lhmiracleroad.incomplete_soul" -> addSoul(300,serverPlayer,itemStack);
                case "item.lhmiracleroad.large_block_soul" -> addSoul(1000,serverPlayer,itemStack);
                case "item.lhmiracleroad.stray_large_block_soul" -> addSoul(1500,serverPlayer,itemStack);
                case "item.lhmiracleroad.adventurer_large_block_soul" -> addSoul(2000,serverPlayer,itemStack);
                case "item.lhmiracleroad.unknown_soldier_soul" -> addSoul(4000,serverPlayer,itemStack);
                case "item.lhmiracleroad.unknown_soldier_large_block_soul" -> addSoul(8000,serverPlayer,itemStack);
                case "item.lhmiracleroad.exhausted_knight_soul" -> addSoul(15000,serverPlayer,itemStack);
                case "item.lhmiracleroad.exhausted_general_soul" -> addSoul(30000,serverPlayer,itemStack);
                case "item.lhmiracleroad.liege_soul" -> addSoul(50000,serverPlayer,itemStack);
                case "item.lhmiracleroad.death_soul" -> deathSoul(serverPlayer,itemStack);
            }
        return InteractionResultHolder.consume(itemStack);
    }

    private void addSoul(int amount, ServerPlayer player,ItemStack itemStack){
        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            int soulStart = playerOccupationAttribute.getOccupationExperience();
            playerOccupationAttribute.addOccupationExperience(amount);
            LHMiracleRoadTool.synchronizationSoul(playerOccupationAttribute.getOccupationExperience(),player,soulStart);
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

            LHMiracleRoadTool.getSoulParticle(serverLevel,player,amount,100);
        });
        itemStack.shrink(1);
    }

    private void deathSoul(ServerPlayer player,ItemStack itemStack){
        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            if (playerOccupationAttribute.getOccupationExperience() >= 100000){
                ServerLevel serverLevel = (ServerLevel) player.level();
                if (LHMiracleRoadTool.percentageProbability(50)){
                    int soulStart = playerOccupationAttribute.getOccupationExperience();
                    playerOccupationAttribute.addOccupationExperience(playerOccupationAttribute.getOccupationExperience());
                    LHMiracleRoadTool.getSoulParticle(serverLevel,player,playerOccupationAttribute.getOccupationExperience(),150);

                    LHMiracleRoadTool.synchronizationSoul(playerOccupationAttribute.getOccupationExperience(),player,soulStart);

                    serverLevel.sendParticles(player, ParticleTypes.FLASH, true, player.getX(), player.getY(), player.getZ(), 1, 0.1, 0.1, 0.1,0.1);
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6F, 0.6F);
                }else {
                    serverLevel.sendParticles(player, ParticleTypes.SOUL_FIRE_FLAME, true, player.getX(), player.getY() + player.getBbHeight() * 0.5, player.getZ(), 50, 0.1, 0.1, 0.1,0.1);
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1F, 1F);
                    int soulStart = playerOccupationAttribute.getOccupationExperience();
                    playerOccupationAttribute.setOccupationExperience(0);

                    LHMiracleRoadTool.synchronizationSoul(playerOccupationAttribute.getOccupationExperience(),player,soulStart);
                }
                playerOccupationAttribute.addPoints(1);
                itemStack.shrink(1);
            }else player.sendSystemMessage(Component.translatable("item.lhmiracleroad.death_soul.prompt").withStyle(ChatFormatting.RED));
        });
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        Item item = stack.getItem();
        return switch (item.getDescriptionId()) {
            case "item.lhmiracleroad.king_soul","item.lhmiracleroad.death_soul" -> 1;
            case "item.lhmiracleroad.liege_soul" -> 8;
            case "item.lhmiracleroad.exhausted_general_soul" -> 16;
            default -> 64;
        };
    }
}
