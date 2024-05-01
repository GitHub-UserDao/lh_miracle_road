package dev.lhkongyu.lhmiracleroad.event;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.access.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

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
            playerOccupationAttribute.setShowAttribute(optional.getShowAttribute());
            playerOccupationAttribute.setEmpiricalCalculationFormula(optional.getEmpiricalCalculationFormula());

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
     * 玩家登录事件
     * @param event
     */
    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();
        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            againAttachAttribute(playerOccupationAttribute,player);
            LHMiracleRoadTool.synchronizationClient(playerOccupationAttribute, (ServerPlayer) player);
        });
//        player.sendSystemMessage(Component.translatable("lhmiracleroad.gui.prompt"));
    }

    private static void againAttachAttribute(PlayerOccupationAttribute playerOccupationAttribute,Player player){
        if (playerOccupationAttribute.getOccupationId() != null) {
            player.getAttribute(LHMiracleRoadAttributes.BURDEN).setBaseValue(LHMiracleRoadConfig.COMMON.INIT_BURDEN.get());
            playerOccupationAttribute.setEmpiricalCalculationFormula(LHMiracleRoadConfig.COMMON.EMPIRICAL_CALCULATION_FORMULA.get());
            Map<String, AttributeModifier> modifierMap = playerOccupationAttribute.getAttributeModifier();
            if (modifierMap == null || modifierMap.isEmpty()) return;
            for (String key : modifierMap.keySet()) {
                Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(key);
                player.getAttribute(attribute).addTransientModifier(modifierMap.get(key));
                if (attribute.getDescriptionId().equals(Attributes.MAX_HEALTH.getDescriptionId())){
                    player.setHealth((float) player.getAttribute(attribute).getValue());
                }
            }
            playerOccupationAttribute.setShowAttribute(LHMiracleRoadTool.setShowAttribute((ServerPlayer) player));
        }
    }
}
