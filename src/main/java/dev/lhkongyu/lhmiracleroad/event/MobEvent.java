package dev.lhkongyu.lhmiracleroad.event;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
    }
}
