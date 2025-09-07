package dev.lhkongyu.lhmiracleroad.registry;

import com.mojang.serialization.Codec;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.client.particle.SoulParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegistry {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LHMiracleRoad.MODID);

    public static final RegistryObject<ParticleType<SoulParticleOption>> SOUL_PARTICLE = PARTICLE_TYPES.register("soul", () -> new net.minecraft.core.particles.ParticleType<>(false, SoulParticleOption.DESERIALIZER) {
        @Override
        public Codec codec() {
            return SoulParticleOption.CODEC;
        }
    });

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
