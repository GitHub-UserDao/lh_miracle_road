package dev.lhkongyu.lhmiracleroad.event;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.abnormal.AbnormalTool;
import dev.lhkongyu.lhmiracleroad.attributes.AttributeInstanceAccess;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerCurioProvider;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.entity.player.PlayerSoulEntity;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypes;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.ConsecratedCombatPlume;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.HawkTalisman;
import dev.lhkongyu.lhmiracleroad.items.curio.talisman.HuntingBowTalisman;
import dev.lhkongyu.lhmiracleroad.registry.ItemsRegistry;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import dev.lhkongyu.lhmiracleroad.tool.SyncTool;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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
        Player oldPlayer = event.getOriginal();
        oldPlayer.reviveCaps();

        Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = oldPlayer.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
        if (optionalPlayerOccupationAttribute.isEmpty()) return;
        PlayerOccupationAttribute optional = optionalPlayerOccupationAttribute.get();

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
            playerOccupationAttribute.setMaxLevel(optional.getMaxLevel());
            playerOccupationAttribute.setPoints(optional.getPoints());

            SyncTool.synchronizationClient(playerOccupationAttribute, (ServerPlayer) oldPlayer);
        });
        oldPlayer.invalidateCaps();
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
            //死亡后的灵魂量
            int soulCount = (int) (playerOccupationAttribute.getOccupationExperience() * LHMiracleRoadConfig.COMMON.SOUL_LOSS_COUNT.get());
//            int soulCount = 0;
            playerOccupationAttribute.setOccupationExperience(soulCount);
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
        SyncTool.synchronizationClient(playerOccupationAttribute.get(), (ServerPlayer) player);
    }

    /**
     * 玩家登录事件
     * @param event
     */
    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();
        Optional<PlayerOccupationAttribute> playerOccupationAttributeOptional = event.getEntity().getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
        if (playerOccupationAttributeOptional.isPresent()) {
            PlayerOccupationAttribute playerOccupationAttribute = playerOccupationAttributeOptional.get();
            loggedInSyncAttribute(playerOccupationAttribute, (ServerPlayer) player);
            playerOccupationAttribute.setCurioAttributeLevel(Maps.newHashMap());
            SyncTool.synchronizationClient(playerOccupationAttribute, (ServerPlayer) player);
            SyncTool.synchronizationShowAttribute((ServerPlayer) player);
            //更新玩家奖惩状态
            LHMiracleRoadTool.playerPunishmentStateUpdate((ServerPlayer) player, playerOccupationAttribute);
        }else {
            SyncTool.synchronizationSoul(0, (ServerPlayer) player,0);
        }
    }

    //交互事件
    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide()) {
            Entity target = event.getTarget();
            Player player = event.getEntity();

            if (target instanceof PlayerSoulEntity soulEntity) {
                soulEntity.getSoul(player,event.getLevel());
                player.swing(event.getHand()); // 播放玩家挥手动画
            }
        }
    }

    private static void againAttachAttribute(PlayerOccupationAttribute playerOccupationAttribute,Player player){
        if (playerOccupationAttribute.getOccupationId() != null) {
            Map<String, AttributeModifier> modifierMap = playerOccupationAttribute.getAttributeModifier();
            if (modifierMap == null || modifierMap.isEmpty()) return;

            player.getAttribute(LHMiracleRoadAttributes.BURDEN).setBaseValue(LHMiracleRoadConfig.COMMON.INIT_BURDEN.get());
            for (String key : modifierMap.keySet()) {
                Attribute attribute = LHMiracleRoadTool.stringConversionAttribute(key);
                if (attribute == null) continue;

                player.getAttribute(attribute).addTransientModifier(modifierMap.get(key));
                if (attribute.getDescriptionId().equals(Attributes.MAX_HEALTH.getDescriptionId())){
                    player.setHealth((float) player.getAttribute(attribute).getValue());
                }
            }
            SyncTool.synchronizationClient(playerOccupationAttribute, (ServerPlayer) player);
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
            playerOccupationAttribute.clearAttributeModifier();
            //重新附上属性值
            PlayerAttributeTool.calculateAttribute(player,newAttributeLevel,playerOccupationAttribute);
        }else {
            player.sendSystemMessage(Component.translatable("lhmiracleroad.gui.prompt"));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void damageAddition(LivingDamageEvent event) {
        if (event.isCanceled()) return;
        Entity directEntity = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        if (directEntity instanceof  Player player){
            ItemStack weapon = player.getMainHandItem();
            //魔法伤害加成
          if (LHMiracleRoadTool.isMagicDamageTypes(event.getSource())){
                AttributeInstance magicInstance = player.getAttribute(LHMiracleRoadAttributes.MAGIC_DAMAGE_ADDITION);
                if (magicInstance != null) {
                    var attribute = ((AttributeInstanceAccess) magicInstance);
                    float magnification = (float) attribute.lh_miracle_road$computeIncreasedValueForInitial(magicInstance.getBaseValue() > 0 ? 0 : 1);
                    float damage = event.getAmount() * magnification;
                    event.setAmount(damage);
                }
            }

            if (LHMiracleRoadTool.isMagic(event.getSource())){
                AttributeInstance magicInstance = player.getAttribute(LHMiracleRoadAttributes.MAGIC_ATTRIBUTE_DAMAGE);
                if (magicInstance != null) {
                    var attribute = ((AttributeInstanceAccess) magicInstance);
                    float damage = (float) attribute.lh_miracle_road$computeIncreasedValueForInitial(event.getAmount());
                    event.setAmount(damage);
                }
            }

            //饰品伤害加成
            player.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
                if (playerCurio.isEquipHuntingBowTalisman() && event.getSource().getDirectEntity() instanceof Arrow){
                    HuntingBowTalisman.damageCount(player,target, event);
                }

                if (playerCurio.isEquipConsecratedCombatPlume()){
                    ConsecratedCombatPlume.damageCount(player, event);
                }

                if (playerCurio.isEquipHawkTalisman()){
                    event.setAmount(HawkTalisman.airDamageAdd(player,event.getAmount()));
                }

            });

            //伤害加成
            AttributeInstance attributeInstance = player.getAttribute(LHMiracleRoadAttributes.DAMAGE_ADDITION);
            if (attributeInstance != null) {
                float damage = (float) (event.getAmount() * attributeInstance.getValue());
                event.setAmount(damage);
            }

            //异常伤害加成
            AbnormalTool.setAbnormalDamage(event,player);


            //爆伤加成
            double criticalHitRate = player.getAttribute(LHMiracleRoadAttributes.CRITICAL_HIT_RATE).getValue();
            double criticalHitDamage = player.getAttribute(LHMiracleRoadAttributes.CRITICAL_HIT_DAMAGE).getValue();

            // 判断是否为原版暴击
            boolean critical =
                    player.fallDistance > 0.0F
                            && !player.onGround()
                            && !player.isInWater()
                            && !player.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS)
                            && !player.isPassenger();

            if (critical && !LHMiracleRoadTool.isMagicDamageTypes(event.getSource()) && !AbnormalTool.isAbnormalDamageTypes(event.getSource())){
                if (weapon.is(ItemsRegistry.HAMMER_IRON.get()) || weapon.is(ItemsRegistry.HAMMER_NETHERITE.get()) || weapon.is(ItemsRegistry.HAMMER_ANCIENT.get())) {
                    criticalHitDamage += 0.5;
                }
                float damage = (float) (event.getAmount() * (criticalHitDamage / 1.5f));
                event.setAmount(damage);
            }else if (LHMiracleRoadTool.percentageProbability(criticalHitRate) && !AbnormalTool.isAbnormalDamageTypes(event.getSource())) {
                float damage = (float) (event.getAmount() * criticalHitDamage);
                event.setAmount(damage);
                int particleCount = (int) (15 * target.getBbWidth() * target.getBbHeight()); // 基于实体体积调整粒子数量
                ServerLevel serverLevel = (ServerLevel) player.level();
                serverLevel.sendParticles((ServerPlayer) player, ParticleTypes.CRIT, true, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), particleCount, target.getBbWidth() / 2, target.getBbHeight() / 2, target.getBbWidth() / 2,0.2);
                serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.5F, 1.0F);
            }

            if (event.getSource().is(SpellDamageTypes.FLAME_MAGIC)){
                AbnormalTool.attackAbnormalBurnBuildup(target, player,event.getAmount());
            }

            if (event.getSource().is(SpellDamageTypes.SOUL_MAGIC)) {
                Optional<PlayerOccupationAttribute> optional =
                        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
                if (optional.isPresent()) {
                    PlayerOccupationAttribute playerOccupationAttribute = optional.get();
                    //获取基础灵魂伤害的 50倍灵魂数量
                    int pSoulStart = playerOccupationAttribute.getOccupationExperience();
                    playerOccupationAttribute.addOccupationExperience((int) (event.getAmount() * 50));
                    SyncTool.synchronizationSoul(playerOccupationAttribute.getOccupationExperience(), (ServerPlayer) player, pSoulStart);

                }
            }
        }

        //玩家受到伤害最后计算
        if (target instanceof ServerPlayer player) {
            AttributeInstance attributeInstance = player.getAttribute(LHMiracleRoadAttributes.DAMAGE_REDUCTION);
            if (attributeInstance != null) {
                double damageReduction = Math.max(1 - attributeInstance.getValue() + 1,0.1);
                float damage = (float) (event.getAmount() * damageReduction);
                event.setAmount(damage);
            }
            //饰品伤害减免
            player.getCapability(PlayerCurioProvider.PLAYER_CURIO_PROVIDER).ifPresent(playerCurio -> {
                if (playerCurio.isEquipConsecratedCombatPlume()){
                    ConsecratedCombatPlume.damageReduction(player, event);
                }
            });
        }
    }

//    @SubscribeEvent
//    public static void onArrowLoose(ArrowLooseEvent event) {
//        Player player = event.getEntity();
//        AttributeInstance attr = player.getAttribute(LHMiracleRoadAttributes.BOW_MINING_SPEED);
//        if (attr != null) {
//            float speed = (float) attr.getValue();
//            event.setCharge((int) (event.getCharge() * speed));
//        }
//    }
}
