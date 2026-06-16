package dev.lhkongyu.lhmiracleroad.client.particle.common;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lhkongyu.lhmiracleroad.registry.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;

public class BlastParticleOption
        implements ParticleOptions {
    protected final Vector3f color;
    protected final float scale;
    protected final int isPlane;
    protected final float alpha;
    public static final ParticleOptions.Deserializer<BlastParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<BlastParticleOption>(){

        @Override
        public BlastParticleOption fromCommand(ParticleType<BlastParticleOption> particleTypeIn, com.mojang.brigadier.StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float red = reader.readFloat();
            reader.expect(' ');
            float green = reader.readFloat();
            reader.expect(' ');
            float blue = reader.readFloat();
            reader.expect(' ');
            float scale = reader.readFloat();
            reader.expect(' ');
            float alpha = reader.readInt();
            reader.expect(' ');
            int isPlane = reader.readInt();
            reader.expect(' ');
            return new BlastParticleOption(new Vector3f(red, green, blue), scale, alpha, isPlane);
        }

        @Override
        public BlastParticleOption fromNetwork(@NotNull ParticleType<BlastParticleOption> particleTypeIn, @NotNull FriendlyByteBuf buffer) {
            return new BlastParticleOption(new Vector3f(buffer.readFloat(),
                    buffer.readFloat(), buffer.readFloat()), buffer.readFloat(), buffer.readFloat(),
                    buffer.readInt());
        }
    };
    public static final Codec<BlastParticleOption> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("red").forGetter(d -> d.color.x),
                    Codec.FLOAT.fieldOf("green").forGetter(d -> d.color.y),
                    Codec.FLOAT.fieldOf("blue").forGetter(d -> d.color.z),
                    Codec.FLOAT.fieldOf("scale").forGetter(d -> d.scale),
                    Codec.FLOAT.fieldOf("alpha").forGetter(d -> d.alpha),
                    Codec.INT.fieldOf("isPlane").forGetter(d -> d.isPlane))
                    .apply(instance, BlastParticleOption::new));

    public BlastParticleOption(Vector3f color, float scale, float alpha, int isPlane) {
        this.color = color;
        this.scale = scale;
        this.alpha = alpha;
        this.isPlane = isPlane;
    }

    public BlastParticleOption(float red, float green, float blue, float scale, float alpha, int isPlane) {
        this.color = new Vector3f(red, green, blue);
        this.scale = scale;
        this.alpha = alpha;
        this.isPlane = isPlane;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public float getScale() {
        return this.scale;
    }

    public int getIsPlane() {
        return this.isPlane;
    }

    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.BLAST_WAVE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeFloat(this.color.x());
        pBuffer.writeFloat(this.color.y());
        pBuffer.writeFloat(this.color.z());
        pBuffer.writeFloat(this.scale);
        pBuffer.writeFloat(this.alpha);
        pBuffer.writeInt(this.isPlane);
    }

    @Override
    public String writeToString() {
        float red = this.color.x;
        float green = this.color.y;
        float blue = this.color.z;
        float scale = this.scale;
        float alpha = this.alpha;
        int isPlane = this.isPlane;
        return String.format(Locale.ROOT,
                "%s %.2f %.2f %.2f %.2f %.2f %d",
                this.color.x(), red, green, blue, scale, alpha, isPlane);
    }
}
