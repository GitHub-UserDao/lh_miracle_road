package dev.lhkongyu.lhmiracleroad.entity.player;

import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.particle.SoulParticleOption;
import dev.lhkongyu.lhmiracleroad.registry.EntityRegistry;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraftforge.fluids.FluidType;

import java.util.UUID;

public class PlayerSoulEntity extends Entity {

    private BlockPos lastLightPos; // 记录上一次光源位置

    private UUID ownerUUID;

    private Entity cachedOwner;

    private int soulCount;

    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);

    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    public void setOwner(Entity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.cachedOwner = owner;

            this.setCustomName(owner.getDisplayName());
            this.setCustomNameVisible(true);
        }
    }

    public int getDuration() {
        return this.getEntityData().get(DURATION);
    }

    public void setDuration(int duration) {
        this.getEntityData().set(DURATION, duration);
    }

    public int getSoulCount() {
        return soulCount;
    }

    public void setSoulCount(int soulCount) {
        this.soulCount = soulCount;
    }

    public PlayerSoulEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public PlayerSoulEntity(Level level,Player owner) {
        this(EntityRegistry.PLAYER_SOUL.get(), level);
        setOwner(owner);
        this.setDuration(LHMiracleRoadTool.getDuration(1800));
        this.setInvulnerable(true);
        this.setNoGravity(true);
        owner.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            int soulCount = (int) (playerOccupationAttribute.getOccupationExperience() * LHMiracleRoadConfig.COMMON.SOUL_LOSS_COUNT.get());
            this.setSoulCount(soulCount);
        });
    }

    public void trailParticles() {
        if (!this.level().isClientSide && tickCount % 5 == 0) {
            level().getServer().getPlayerList().getPlayers().forEach(player -> {
                ((ServerLevel) level()).sendParticles(player,new SoulParticleOption(-1), true, this.getX(), this.getY() + this.getBbHeight() * .25f, this.getZ(), 4, 0.25, 0.25, 0.25,0.05);
            });
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > getDuration()) {
            this.clean();
        }
        trailParticles();

        if (tickCount == 1 || lastLightPos == null) {
            // 更新光源位置
            BlockPos currentPos = this.blockPosition();
            if (lastLightPos == null || !lastLightPos.equals(currentPos)) {
                // 移除旧位置光源
                if (lastLightPos != null) {
                    level().setBlock(lastLightPos, Blocks.AIR.defaultBlockState(), 3);
                }
                // 设置新位置光源（使用隐形光源方块，如 light_block）
                level().setBlock(currentPos, Blocks.LIGHT.defaultBlockState()
                        .setValue(LightBlock.LEVEL, LightBlock.MAX_LEVEL), 3);
                lastLightPos = currentPos;
            }
        }
    }

    public void clean() {
        // 实体消失时移除光源
        if (lastLightPos != null) {
            level().setBlock(lastLightPos, Blocks.AIR.defaultBlockState(), 3);
        }
        super.discard();
    }

    public void getSoul(Player player,Level level){
        if (getOwner() == null) return;
        player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).ifPresent(playerOccupationAttribute -> {
            int soulCount = getSoulCount();
            if (!getOwner().equals(player)){
                soulCount = (int) (soulCount * .3);
            }

            LHMiracleRoadTool.getSoulParticle((ServerLevel) level, (ServerPlayer) player,soulCount,150,200,this);
            playerOccupationAttribute.setOccupationExperience(playerOccupationAttribute.getOccupationExperience() + soulCount);
            clean();
        });
    }

    @Override
    public void defineSynchedData() {
        this.getEntityData().define(DURATION, LHMiracleRoadTool.getDuration(1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("Duration", this.getDuration());
        compoundTag.putInt("SoulCount", this.getSoulCount());

        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }

        if (this.getCustomName() != null) {
            compoundTag.putString("CustomName", Component.Serializer.toJson(this.getCustomName()));
        }
        compoundTag.putBoolean("CustomNameVisible", this.isCustomNameVisible());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setDuration(compoundTag.getInt("Duration"));
        this.setSoulCount(compoundTag.getInt("SoulCount"));
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
            this.cachedOwner = null;
        }

        if (compoundTag.contains("CustomName", 8)) {
            this.setCustomName(Component.Serializer.fromJson(compoundTag.getString("CustomName")));
        }
        this.setCustomNameVisible(compoundTag.getBoolean("CustomNameVisible"));
    }

    //不让流体推动法术实体
    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return true; // 允许玩家右键交互
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        // 此处逻辑由事件监听器处理，这里只需返回 SUCCESS 允许交互
        return InteractionResult.SUCCESS;
    }

    // 允许实体被选中
    @Override
    public boolean isPickable() {
        return true;
    }

}
