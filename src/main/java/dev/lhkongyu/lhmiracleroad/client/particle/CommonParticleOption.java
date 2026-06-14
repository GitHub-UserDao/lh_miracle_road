package dev.lhkongyu.lhmiracleroad.client.particle;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.io.StringReader;
import java.util.Locale;

public class CommonParticleOption
        implements ParticleOptions {
    protected final Vector3f color;
    protected final float scale;
    protected final int particleType;
    protected final int isPlane;
    protected final float alpha;
    protected final int survivalTime;
    protected final int hasPhysics;
    public static final ParticleOptions.Deserializer<CommonParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<CommonParticleOption>(){

        @Override
        public CommonParticleOption fromCommand(ParticleType<CommonParticleOption> particleTypeIn, com.mojang.brigadier.StringReader reader) throws CommandSyntaxException {
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
            int survivalTime = reader.readInt();
            reader.expect(' ');
            int hasPhysics = reader.readInt();
            reader.expect(' ');
            int isPlane = reader.readInt();
            reader.expect(' ');
            int particleType = reader.readInt();
            reader.expect(' ');
            return new CommonParticleOption(new Vector3f(red, green, blue), scale, alpha, survivalTime, hasPhysics, isPlane, particleType);
        }

        @Override
        public CommonParticleOption fromNetwork(@NotNull ParticleType<CommonParticleOption> particleTypeIn, @NotNull FriendlyByteBuf buffer) {
            return new CommonParticleOption(new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
    };
    public static final Codec<CommonParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.FLOAT.fieldOf("red").forGetter(d -> Float.valueOf(d.color.x)), Codec.FLOAT.fieldOf("green").forGetter(d -> Float.valueOf(d.color.y)), Codec.FLOAT.fieldOf("blue").forGetter(d -> Float.valueOf(d.color.z)), Codec.FLOAT.fieldOf("scale").forGetter(d -> Float.valueOf(d.scale)), Codec.FLOAT.fieldOf("alpha").forGetter(d -> Float.valueOf(d.alpha)), Codec.INT.fieldOf("survivalTime").forGetter(d -> d.survivalTime), Codec.INT.fieldOf("hasPhysics").forGetter(d -> d.hasPhysics), Codec.INT.fieldOf("isPlane").forGetter(d -> d.isPlane), Codec.INT.fieldOf("particleType").forGetter(d -> d.particleType)).apply((Applicative)instance, CommonParticleOption::new));

    public CommonParticleOption(Vector3f color, int particleType) {
        this.color = color;
        this.scale = 1.0f;
        this.particleType = particleType;
        this.isPlane = -1;
        this.alpha = 1.0f;
        this.survivalTime = 10;
        this.hasPhysics = 1;
    }

    public CommonParticleOption(float scale, int particleType) {
        this.color = new Vector3f();
        this.scale = scale;
        this.particleType = particleType;
        this.isPlane = -1;
        this.alpha = 1.0f;
        this.survivalTime = 10;
        this.hasPhysics = 1;
    }

    public CommonParticleOption(Vector3f color, float scale, int particleType, boolean isPlane) {
        this.color = color;
        this.scale = scale;
        this.particleType = particleType;
        this.isPlane = isPlane ? 1 : -1;
        this.alpha = 1.0f;
        this.survivalTime = 10;
        this.hasPhysics = 1;
    }

    public CommonParticleOption(Vector3f color, float scale, int particleType) {
        this.color = color;
        this.scale = scale;
        this.particleType = particleType;
        this.isPlane = -1;
        this.alpha = 1.0f;
        this.survivalTime = 10;
        this.hasPhysics = 1;
    }

    public CommonParticleOption(Vector3f color, float scale, int survivalTime, int particleType) {
        this.color = color;
        this.scale = scale;
        this.survivalTime = survivalTime;
        this.particleType = particleType;
        this.isPlane = -1;
        this.alpha = 1.0f;
        this.hasPhysics = 1;
    }

    public CommonParticleOption(Vector3f color, float scale, float alpha, int survivalTime, int particleType) {
        this.color = color;
        this.scale = scale;
        this.alpha = alpha;
        this.survivalTime = survivalTime;
        this.particleType = particleType;
        this.isPlane = -1;
        this.hasPhysics = 1;
    }

    public CommonParticleOption(Vector3f color, float scale, float alpha, int survivalTime, int hasPhysics, int particleType) {
        this.color = color;
        this.scale = scale;
        this.alpha = alpha;
        this.survivalTime = survivalTime;
        this.hasPhysics = hasPhysics;
        this.particleType = particleType;
        this.isPlane = -1;
    }

    public CommonParticleOption(Vector3f color, float scale, float alpha, int survivalTime, int hasPhysics, int isPlane, int particleType) {
        this.color = color;
        this.scale = scale;
        this.alpha = alpha;
        this.survivalTime = survivalTime;
        this.hasPhysics = hasPhysics;
        this.isPlane = isPlane;
        this.particleType = particleType;
    }

    public CommonParticleOption(float red, float green, float blue, float scale, float alpha, int survivalTime, int hasPhysics, int isPlane, int particleType) {
        this.color = new Vector3f(red, green, blue);
        this.scale = scale;
        this.alpha = alpha;
        this.survivalTime = survivalTime;
        this.hasPhysics = hasPhysics;
        this.isPlane = isPlane;
        this.particleType = particleType;
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

    public int getSurvivalTime() {
        return this.survivalTime;
    }

    public int isHasPhysics() {
        return this.hasPhysics;
    }

    public int getIsPlane() {
        return this.isPlane;
    }

    @Override
    public ParticleType<?> getType() {
        return null;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeFloat(this.color.x());
        pBuffer.writeFloat(this.color.y());
        pBuffer.writeFloat(this.color.z());
        pBuffer.writeFloat(this.scale);
        pBuffer.writeFloat(this.alpha);
        pBuffer.writeInt(this.survivalTime);
        pBuffer.writeInt(this.hasPhysics);
        pBuffer.writeInt(this.isPlane);
        pBuffer.writeInt(this.particleType);
    }

    @Override
    public String writeToString() {
        float red = this.color.x;
        float green = this.color.y;
        float blue = this.color.z;
        float scale = this.scale;
        float alpha = this.alpha;
        int survivalTime = this.survivalTime;
        int hasPhysics = this.hasPhysics;
        int isPlane = this.isPlane;
        int particleType = this.particleType;
        return String.format(Locale.ROOT,
                "%s %.2f %.2f %.2f %.2f %.2f %d %d %d %d",
                this.color.x(), red, green, blue, scale, alpha, survivalTime, hasPhysics, isPlane, particleType);
    }
}
