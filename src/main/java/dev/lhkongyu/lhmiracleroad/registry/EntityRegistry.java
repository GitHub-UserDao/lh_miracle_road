package dev.lhkongyu.lhmiracleroad.registry;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.entity.player.PlayerSoulEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LHMiracleRoad.MODID);

    public static final RegistryObject<EntityType<PlayerSoulEntity>> PLAYER_SOUL =
            ENTITIES.register("player_soul", () -> EntityType.Builder.<PlayerSoulEntity>of(PlayerSoulEntity::new, MobCategory.MISC)
                    .sized(1f, 2f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(LHMiracleRoad.MODID, "player_soul").toString()));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
