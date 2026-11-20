package dev.lhkongyu.lhmiracleroad.items.curio.talisman;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.data.loot.nbt.MiraculousTalismanData;
import dev.lhkongyu.lhmiracleroad.items.curio.MiraculousTalismanItem;
import dev.lhkongyu.lhmiracleroad.items.curio.TalismanItem;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import dev.lhkongyu.lhmiracleroad.tool.SyncTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.Optional;

public class MiraculousTalisman {

    public static TalismanItem createCreedTalisman(){

        return new MiraculousTalismanItem(new Item.Properties().rarity(Rarity.UNCOMMON),null);
    }

    public static void equipMiraculousTalisman(LivingEntity livingEntity, ItemStack stack, boolean isEquipMiraculousTalisman){
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            JsonArray keys = MiraculousTalismanData.getMiraculousTalismanData(stack);
            if (keys == null || keys.isEmpty()) return;

            Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = serverPlayer.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
            if (optionalPlayerOccupationAttribute.isEmpty()) return;
            PlayerOccupationAttribute playerOccupationAttribute = optionalPlayerOccupationAttribute.get();

            for (JsonElement keyElement : keys) {
                String key = keyElement.getAsString();
                Integer level = playerOccupationAttribute.getOccupationAttributeLevel().get(key);
                if (level == null) continue;

                if (isEquipMiraculousTalisman)
                    playerOccupationAttribute.addCurioAttributeLevel(key, MiraculousTalismanData.level);
                else
                    playerOccupationAttribute.subtractCurioAttributeLevel(key, MiraculousTalismanData.level);

                PlayerAttributeTool.refreshAttributeRewards(serverPlayer, playerOccupationAttribute, key, level);

                SyncTool.synchronizationClient(playerOccupationAttribute,serverPlayer);
                //更新玩家奖惩状态
                LHMiracleRoadTool.playerPunishmentStateUpdate(serverPlayer, playerOccupationAttribute);
            }

        }
    }


}
