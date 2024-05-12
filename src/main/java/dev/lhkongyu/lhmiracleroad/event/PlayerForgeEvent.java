package dev.lhkongyu.lhmiracleroad.event;

import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Optional;

/**
 * player event 玩家事件
 */
@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerForgeEvent {

    /**
     * 玩家克隆时触发
     * 重生触发克隆事件和末地回归时触发克隆事件
     * @param event
     */
    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        Player player = event.getOriginal();
        player.reviveCaps();
        PlayerOccupationAttribute optional = player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve().get();

        event.getEntity().getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            playerOccupationAttribute.setOccupationLevel(optional.getOccupationLevel());
            playerOccupationAttribute.setOccupationExperience(optional.getOccupationExperience());
            playerOccupationAttribute.setOccupationId(optional.getOccupationId());
            playerOccupationAttribute.setOffhandHeavy(optional.getOffhandHeavy());
            playerOccupationAttribute.setAttributeModifier(optional.getAttributeModifier());
            playerOccupationAttribute.setPunishmentAttributeModifier(optional.getPunishmentAttributeModifier());
            playerOccupationAttribute.setOccupationAttributeLevel(optional.getOccupationAttributeLevel());
            playerOccupationAttribute.setHeavyAttributeModifier(optional.getHeavyAttributeModifier());
            playerOccupationAttribute.setEmpiricalCalculationFormula(optional.getEmpiricalCalculationFormula());
            playerOccupationAttribute.setBurden(optional.getBurden());
            playerOccupationAttribute.setAttributeMaxLevel(optional.getAttributeMaxLevel());

            LHMiracleRoadTool.synchronizationClient(playerOccupationAttribute, (ServerPlayer) player);
        });
    }

    /**
     * 玩家重生事件
     * @param event
     */
    @SubscribeEvent
    public static void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent event){
        Player player = event.getEntity();
        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            againAttachAttribute(playerOccupationAttribute,player);
            if (!LHMiracleRoadTool.percentageProbability(LHMiracleRoadConfig.COMMON.IGNORE_DEATH_PENALTY_PROBABILITY.get())){
                playerOccupationAttribute.setOccupationExperience(0);
            }
        });
    }

    /**
     * 切换维度事件
     * @param event
     */
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        Optional<PlayerOccupationAttribute> playerOccupationAttribute = event.getEntity().getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
        if (playerOccupationAttribute.isEmpty()) return;
        LHMiracleRoadTool.synchronizationClient(playerOccupationAttribute.get(), (ServerPlayer) player);
    }

    /**
     * 玩家登录事件
     * @param event
     */
    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();
        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            loggedInSyncAttribute(playerOccupationAttribute, (ServerPlayer) player);
            LHMiracleRoadTool.synchronizationClient(playerOccupationAttribute, (ServerPlayer) player);
            LHMiracleRoadTool.synchronizationShowAttribute((ServerPlayer) player);
        });
    }

    private static void againAttachAttribute(PlayerOccupationAttribute playerOccupationAttribute,Player player){
        if (playerOccupationAttribute.getOccupationId() != null) {
            Map<String, AttributeModifier> modifierMap = playerOccupationAttribute.getAttributeModifier();
            if (modifierMap == null || modifierMap.isEmpty()) return;
            for (String key : modifierMap.keySet()) {
                Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(key);
                player.getAttribute(attribute).addTransientModifier(modifierMap.get(key));
                if (attribute.getDescriptionId().equals(Attributes.MAX_HEALTH.getDescriptionId())){
                    player.setHealth((float) player.getAttribute(attribute).getValue());
                }
            }
            LHMiracleRoadTool.synchronizationClient(playerOccupationAttribute, (ServerPlayer) player);
        }
    }

    /**
     * 登录时 配置文件有可能进行修改之类的，或加上属性所需要的mod，需根据现有的条件下进行重新计算属性来达到同步
     * @param playerOccupationAttribute
     * @param player
     */
    private static void loggedInSyncAttribute(PlayerOccupationAttribute playerOccupationAttribute,ServerPlayer player){
        if (playerOccupationAttribute.getOccupationId() != null) {
            JsonObject occupation = LHMiracleRoadTool.getOccupation(playerOccupationAttribute.getOccupationId());
            if (occupation == null){
                PlayerAttributeTool.resetOccupation(player,playerOccupationAttribute);
                return;
            }
            Map<String,Integer> newAttributeLevel = LHMiracleRoadTool.setInitAttributeLevel(occupation);
            Map<String,Integer> attributeLevel = playerOccupationAttribute.getOccupationAttributeLevel();
            for (String key : newAttributeLevel.keySet()){
                Integer attributeLevelValue = attributeLevel.get(key);
                if (attributeLevelValue != null) {
                    Integer actualValue = newAttributeLevel.get(key);
                    actualValue = Integer.max(attributeLevelValue,actualValue);
                    newAttributeLevel.put(key,actualValue);
                }
            }
            //重新附上属性值
            PlayerAttributeTool.calculateAttribute(player,newAttributeLevel,playerOccupationAttribute);
        }else {
            player.sendSystemMessage(Component.translatable("lhmiracleroad.gui.prompt"));
        }
    }
}
