package dev.lhkongyu.lhmiracleroad.registry;

import com.mojang.serialization.Codec;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.client.particle.common.PhotonParticleOption;
import dev.lhkongyu.lhmiracleroad.client.particle.soul.SoulParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegistry {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;

    public static final RegistryObject<ParticleType<SoulParticleOption>> SOUL_PARTICLE;
    public static final RegistryObject<ParticleType<SimpleParticleType>> FIRE_BOTTOM_PARTICLE;
    public static final RegistryObject<ParticleType<SimpleParticleType>> BLEED;
    public static final RegistryObject<ParticleType<SimpleParticleType>> BLEED_GROUND;
    public static final RegistryObject<ParticleType<PhotonParticleOption>> PHOTON;
    public static final RegistryObject<ParticleType<SimpleParticleType>> LIGHTNING;
    public static final RegistryObject<ParticleType<SimpleParticleType>> POISON;
    public static final RegistryObject<ParticleType<SimpleParticleType>> DARK;
    public static final RegistryObject<ParticleType<SimpleParticleType>> SNOW_FLAKE;

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }

    static {
        PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LHMiracleRoad.MODID);
        SOUL_PARTICLE = PARTICLE_TYPES.register("soul", () -> new net.minecraft.core.particles.ParticleType<>(false, SoulParticleOption.DESERIALIZER) {
            public Codec codec() {
                return SoulParticleOption.CODEC;
            }
        });
        FIRE_BOTTOM_PARTICLE = PARTICLE_TYPES.register("fire_bottom", () -> new SimpleParticleType(false));
        BLEED = PARTICLE_TYPES.register("bleed", () -> new SimpleParticleType(false));
        BLEED_GROUND = PARTICLE_TYPES.register("bleed_ground", () -> new SimpleParticleType(false));
        PHOTON = PARTICLE_TYPES.register("photon", () -> new net.minecraft.core.particles.ParticleType<>(false, PhotonParticleOption.DESERIALIZER) {
            public Codec codec() {
                return PhotonParticleOption.CODEC;
            }
        });
        LIGHTNING = PARTICLE_TYPES.register("lightning", () -> new SimpleParticleType(false));
        POISON = PARTICLE_TYPES.register("poison", () -> new SimpleParticleType(false));
        DARK = PARTICLE_TYPES.register("dark", () -> new SimpleParticleType(false));
        SNOW_FLAKE = PARTICLE_TYPES.register("snow", () -> new SimpleParticleType(false));
    }
}
