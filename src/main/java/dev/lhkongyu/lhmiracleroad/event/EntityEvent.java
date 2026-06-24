package dev.lhkongyu.lhmiracleroad.event;

import com.mojang.datafixers.kinds.IdF;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalData;
import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalType;
import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalTool;
import dev.lhkongyu.lhmiracleroad.abnormal.capability.AbnormalProvider;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.entity.player.PlayerSoulEntity;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypes;
import dev.lhkongyu.lhmiracleroad.items.curio.bracelet.AbyssbindBracelet;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.HeartOfBloodLust;
import dev.lhkongyu.lhmiracleroad.items.gem.AttributeGem;
import dev.lhkongyu.lhmiracleroad.packet.AbyssbindActivatePacket;
import dev.lhkongyu.lhmiracleroad.packet.PlayerChannel;
import dev.lhkongyu.lhmiracleroad.registry.ParticleRegistry;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.NameTool;
import dev.lhkongyu.lhmiracleroad.tool.SyncTool;
import dev.lhkongyu.lhmiracleroad.tool.particle.ParticleTool;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = LHMiracleRoad.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvent {

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

        if (entity instanceof ServerPlayer player){
            if (AbyssbindBracelet.getIsEquipAbyssbindBracelet(player)){
                // 消耗饰品
                AbyssbindBracelet.consume(player);
                // 取消死亡并回血
                event.setCanceled(true);
                player.setHealth(player.getMaxHealth() * 0.3f);
                // 清除负面效果
                List<MobEffectInstance> list = new ArrayList<>(player.getActiveEffects());
                for (MobEffectInstance effect : list) {
                    if (effect.getEffect().isBeneficial())
                        continue;
                    player.removeEffect(effect.getEffect());
                }
                player.setTicksFrozen(0);
                player.clearFire();

                //生命回复2、抗火、伤害吸收1
                player.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION,
                        20 * 5,
                        1
                ));
                player.addEffect(new MobEffectInstance(
                        MobEffects.FIRE_RESISTANCE,
                        20 * 5,
                        0
                ));
                player.addEffect(new MobEffectInstance(
                        MobEffects.ABSORPTION,
                        20 * 60,
                        0
                ));

                // 传送回重生点
                LHMiracleRoadTool.teleportToRespawn(player);
                ParticleTool.nearbyPlayers(player.level(), player.getX(), player.getY(), player.getZ()).forEach(serverPlayer -> {
                    for (int i = 0; i < 40; i++) {
                        ((ServerLevel)player.level()).sendParticles(
                                serverPlayer,
                                (SimpleParticleType) ParticleRegistry.DARK.get(),
                                true,
                                serverPlayer.getRandomX(0.5), serverPlayer.getRandomY(),
                                serverPlayer.getRandomZ(0.5),
                                1, 0, 0.15, 0, 0.075);
                    }
                });
                PlayerChannel.sendToClient(new AbyssbindActivatePacket(), player);
            }else {
                if (LHMiracleRoadConfig.COMMON.SOUL_LOSS_COUNT.get() >= 1 || LHMiracleRoadConfig.COMMON.SOUL_LOSS_COUNT.get() < 0)
                    return;
                Level level = player.level();
                if (LHMiracleRoadConfig.COMMON.DARK_SOUL.get()) {
                    PlayerSoulEntity oldSoul = LHMiracleRoadTool.SOUL_ENTITY_MAP.get(player.getUUID());
                    if (oldSoul != null && oldSoul.isAlive()) {
                        oldSoul.discard();
                    }
                }

                PlayerSoulEntity playerSoulEntity = new PlayerSoulEntity(level, player);
                playerSoulEntity.setPos(player.position().add(0, 1.5, 0));
                level.addFreshEntity(playerSoulEntity);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
//        entity.invulnerableTime = 0;
        if (event.getSource().getEntity() instanceof LivingEntity source){
            if (event.getSource().is(DamageTypes.MOB_ATTACK) || event.getSource().is(DamageTypes.PLAYER_ATTACK) || event.getSource().is(DamageTypeTags.IS_PROJECTILE)) {
                AttributeGem.getAttributeDamage(source, target);
                AbnormalTool.attackAbnormalBuildup(target,source);

                AttributeGem.attackParticleSpecialEffects(source,target);
            }

            if (event.getSource().is(SpellDamageTypes.FLAME_MAGIC) && !(event.getSource().getEntity() instanceof Player)){
                AbnormalTool.attackAbnormalBurnBuildup(target,source,event.getAmount());
            }
        }

        target.getCapability(AbnormalProvider.CAPABILITY).ifPresent(cap -> {
            AbnormalData frost = cap.get(AbnormalType.FROST);
            if (frost.active) {
                if (event.getSource().is(SpellDamageTypes.FLAME_MAGIC) ||
                        event.getSource().is(DamageTypes.IN_FIRE) ||
                        event.getSource().is(DamageTypes.ON_FIRE)){

                    event.setAmount(event.getAmount() * 1.5f);
                    frost.active = false;
                    frost.buildup = 0;
                    frost.lastAttackTime = 0;
                    frost.lastAttacker = null;
                    SyncTool.abnormalSync(target);
                }else event.setAmount(event.getAmount() * 1.1f);
            }
        });
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

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        entity.getCapability(AbnormalProvider.CAPABILITY).ifPresent(cap -> {
            cap.tick(entity);
        });

        if (entity.level().isClientSide())  return;

        if (entity.tickCount % 100 != 0) return;

        ItemStack stack = entity.getMainHandItem();

        if (stack.isEmpty())  return;

        CompoundTag tag = stack.getTagElement("lh_gem");

        if (tag == null) return;

        String type = tag.getString("type");

        if (!NameTool.HOLY.equals(type)) return;

        // 回复血量
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(1.0F + Math.max((float) ((int) entity.getMaxHealth() * 0.02),1f));
        }
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {

        LivingEntity target = event.getEntity();

        DamageSource source = target.getLastDamageSource();

        if (source == null) {
            return;
        }

        if (LHMiracleRoadTool.isMagicDamage(source) || AbnormalTool.isAbnormalDamageTypes(source)) {
            event.setCanceled(true);
        }
    }
}
