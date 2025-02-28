package dev.lhkongyu.lhmiracleroad.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lhkongyu.lhmiracleroad.registry.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public class SoulParticleOption implements ParticleOptions {

    public static final Codec<SoulParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("ownerId").forGetter(d -> d.ownerId)
    ).apply(instance, SoulParticleOption::new));

    public static final Deserializer<SoulParticleOption> DESERIALIZER = new Deserializer<SoulParticleOption>() {
        public SoulParticleOption fromCommand(ParticleType<SoulParticleOption> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int ownerId = reader.readInt();
            reader.expect(' ');
            return new SoulParticleOption(ownerId);
        }

        public SoulParticleOption fromNetwork(ParticleType<SoulParticleOption> particleTypeIn, FriendlyByteBuf buffer) {
            return new SoulParticleOption(buffer.readInt());
        }
    };

    private final int ownerId;

    public SoulParticleOption(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.SOUL_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(this.ownerId);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.ownerId);

    }
}
