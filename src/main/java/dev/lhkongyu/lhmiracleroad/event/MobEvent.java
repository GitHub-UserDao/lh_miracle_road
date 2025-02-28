package dev.lhkongyu.lhmiracleroad.event;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.entity.player.PlayerSoulEntity;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobEvent {

    /**
     * 生物死亡时触发
     * @param event
     */
    @SubscribeEvent
    public static void killMob(LivingDeathEvent event){
        LivingEntity entity = event.getEntity();
        LivingEntity credit = entity.getKillCredit();
        if (credit instanceof Player player) {
            player.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
                if (playerCurio.isEquipHunterMark()){
                    playerCurio.addHunterMarkKillAmount();
                }
            });
        }

        if (entity instanceof Player player){
            Level level = event.getEntity().level();
            PlayerSoulEntity playerSoulEntity = new PlayerSoulEntity(level,player);
            playerSoulEntity.setPos(player.position());
            level.addFreshEntity(playerSoulEntity);
        }
    }

    /**
     * 生物掉落经验时触发
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExperienceDrop(LivingExperienceDropEvent event){
        Entity target = event.getEntity();
        Player player = event.getAttackingPlayer();
        if (player == null) return;
        int levelExperience = event.getDroppedExperience();
        LHMiracleRoadTool.getSoulParticle((ServerLevel) player.level(), (ServerPlayer) player,levelExperience,150,2,10,target);
    }
}
