package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.CreedTalismanData;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.MiraculousTalismanData;
import dev.lhkongyu.lhmiracleroad.data.reloader.AttributePointsRewardsReloadListener;
import dev.lhkongyu.lhmiracleroad.items.curio.CreedTalismanItem;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.Map;
import java.util.Optional;

public class CreedTalisman {

    public static TalismanItem createCreedTalisman(){

        return new CreedTalismanItem(new Item.Properties().rarity(Rarity.EPIC),null);
    }

    public static void equipCreedTalisman(LivingEntity livingEntity, ItemStack stack, boolean isEquipCreedTalisman){
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            String key = CreedTalismanData.getCreedTalismanData(stack);
            if (key == null || key.isEmpty()) return;

            Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = serverPlayer.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
            if (optionalPlayerOccupationAttribute.isEmpty()) return;
            PlayerOccupationAttribute playerOccupationAttribute = optionalPlayerOccupationAttribute.get();

            if (isEquipCreedTalisman)
                playerOccupationAttribute.addCurioAttributeLevel(key, CreedTalismanData.level);
            else
                playerOccupationAttribute.subtractCurioAttributeLevel(key, CreedTalismanData.level);

            Integer level = playerOccupationAttribute.getOccupationAttributeLevel().get(key);
            if (level == null ) return;

            JsonObject jsonObject = AttributePointsRewardsReloadListener.ATTRIBUTE_POINTS_REWARDS.get(key);
            if (jsonObject == null) return;
            //给对应属性类型进行 属性增长/减少
            PlayerAttributeTool.setAttributeNotRecoverHP(serverPlayer, jsonObject, level, playerOccupationAttribute, key);

            LHMiracleRoadTool.synchronizationClient(playerOccupationAttribute,serverPlayer);
            //更新玩家奖惩状态
            LHMiracleRoadTool.playerPunishmentStateUpdate(serverPlayer, playerOccupationAttribute);
        }
    }
}
