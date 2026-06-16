package dev.lhkongyu.lhmiracleroad.client.particle.common;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lhkongyu.lhmiracleroad.registry.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;

public class ElementParticleOption implements ParticleOptions {

    protected final Vector3f color;
    public static final Deserializer<ElementParticleOption> DESERIALIZER = new Deserializer<ElementParticleOption>(){

        @Override
        public ElementParticleOption fromCommand(ParticleType<ElementParticleOption> particleTypeIn, com.mojang.brigadier.StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float red = reader.readFloat();
            reader.expect(' ');
            float green = reader.readFloat();
            reader.expect(' ');
            float blue = reader.readFloat();
            reader.expect(' ');
            return new ElementParticleOption(new Vector3f(red, green, blue));
        }

        @Override
        public ElementParticleOption fromNetwork(@NotNull ParticleType<ElementParticleOption> particleTypeIn, @NotNull FriendlyByteBuf buffer) {
            return new ElementParticleOption(new Vector3f(buffer.readFloat(),
                    buffer.readFloat(), buffer.readFloat()));
        }
    };
    public static final Codec<ElementParticleOption> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("red").forGetter(d -> d.color.x),
                    Codec.FLOAT.fieldOf("green").forGetter(d -> d.color.y),
                    Codec.FLOAT.fieldOf("blue").forGetter(d -> d.color.z))
                    .apply(instance, ElementParticleOption::new));

    public ElementParticleOption(Vector3f color) {
        this.color = color;
    }

    public ElementParticleOption(float red, float green, float blue) {
        this.color = new Vector3f(red, green, blue);
    }

    public Vector3f getColor() {
        return this.color;
    }

    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.ELEMENT.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeFloat(this.color.x());
        pBuffer.writeFloat(this.color.y());
        pBuffer.writeFloat(this.color.z());
    }

    @Override
    public String writeToString() {
        float red = this.color.x;
        float green = this.color.y;
        float blue = this.color.z;
        return String.format(Locale.ROOT,
                "%s %.2f %.2f %.2f %.2f %.2f %d",
                this.color.x(), red, green, blue);
    }
}
