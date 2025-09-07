package dev.lhkongyu.lhmiracleroad.event;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.entity.player.PlayerSoulEntity;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.HeartOfBloodLust;
import dev.lhkongyu.lhmiracleroad.items.gem.AttributeGem;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingEvent {

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

                if (playerCurio.isEquipHeartOfBloodLust()){
                    HeartOfBloodLust.killRestoreHp(player);
                }
            });
        }

        if (entity instanceof Player player){
            if (LHMiracleRoadConfig.COMMON.SOUL_LOSS_COUNT.get() >= 1 || LHMiracleRoadConfig.COMMON.SOUL_LOSS_COUNT.get() < 0) return;
            Level level = player.level();
            if (LHMiracleRoadConfig.COMMON.DARK_SOUL.get()) {
                PlayerSoulEntity oldSoul = LHMiracleRoadTool.SOUL_ENTITY_MAP.get(player.getUUID());
                if (oldSoul != null && oldSoul.isAlive()) {
                    oldSoul.discard();
                }
            }

            PlayerSoulEntity playerSoulEntity = new PlayerSoulEntity(level,player);
            playerSoulEntity.setPos(player.position().add(0,1.5,0));
            level.addFreshEntity(playerSoulEntity);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
//        entity.invulnerableTime = 0;
        if (event.getSource().getEntity() instanceof ServerPlayer player){
            float amount = event.getAmount();
            if (event.getSource().is(DamageTypes.PLAYER_ATTACK) || event.getSource().is(DamageTypeTags.IS_PROJECTILE)){
                amount = AttributeGem.getAttributeDamage(amount,player,entity);
            }
//            if (!LHMiracleRoadTool.isMagicDamage(event.getSource())) amount = AttributeGem.getAttributeDamage(amount,player,entity);
            event.setAmount(amount);
        }

    }

//    /**
//     * 生物掉落经验时触发
//     * @param event
//     */
//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    public static void onExperienceDrop(LivingExperienceDropEvent event){
//        Entity target = event.getEntity();
//        Player player = event.getAttackingPlayer();
//        if (player == null) return;
//        int levelExperience = event.getDroppedExperience();
//        LHMiracleRoadTool.getSoulParticle((ServerLevel) player.level(), (ServerPlayer) player,levelExperience,150,2,10,target);
//    }
}
