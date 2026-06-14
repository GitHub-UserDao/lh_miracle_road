package dev.lhkongyu.lhmiracleroad.client.particle.common;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lhkongyu.lhmiracleroad.registry.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector3f;

import java.util.Locale;

public class PhotonParticleOption implements ParticleOptions {
    private final Vector3f color;

    private final float scale;

    private final int lifetime;

    public PhotonParticleOption(Vector3f color, float scale,int lifetime){
        this.color = color;
        this.scale = scale;
        this.lifetime = lifetime;
    }

    public PhotonParticleOption(float red,float green,float blue, float scale,int lifetime){
        this.color = new Vector3f(red,green,blue);
        this.scale = scale;
        this.lifetime = lifetime;
    }

    public Vector3f getColor() {
        return color;
    }

    public float getScale() {
        return scale;
    }

    public int getLifetime() {
        return lifetime;
    }

    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.PHOTON.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.color.x());
        buffer.writeFloat(this.color.y());
        buffer.writeFloat(this.color.z());
        buffer.writeFloat(this.scale);
        buffer.writeInt(this.lifetime);
    }

    @Override
    public String writeToString() {
        float red = this.color.x;
        float green = this.color.y;
        float blue = this.color.z;
        float scale = this.scale;
        int lifetime =this.lifetime;
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), red,green,blue,scale,lifetime);
    }

    public static final Codec<PhotonParticleOption> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(d -> d.getColor().x),
            Codec.FLOAT.fieldOf("green").forGetter(d -> d.getColor().y),
            Codec.FLOAT.fieldOf("blue").forGetter(d -> d.getColor().z),
            Codec.FLOAT.fieldOf("scale").forGetter(PhotonParticleOption::getScale),
            Codec.INT.fieldOf("lifetime").forGetter(PhotonParticleOption::getLifetime)

    ).apply(instance, PhotonParticleOption::new));

    public static final Deserializer<PhotonParticleOption> DESERIALIZER = new Deserializer<PhotonParticleOption>() {
        public PhotonParticleOption fromCommand(ParticleType<PhotonParticleOption> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float red = reader.readFloat();
            reader.expect(' ');
            float green = reader.readFloat();
            reader.expect(' ');
            float blue = reader.readFloat();
            reader.expect(' ');
            float scale = reader.readFloat();
            reader.expect(' ');
            int lifetime = reader.readInt();
            reader.expect(' ');


            return new PhotonParticleOption(red,green,blue,scale,lifetime);
        }

        public PhotonParticleOption fromNetwork(ParticleType<PhotonParticleOption> particleTypeIn, FriendlyByteBuf buffer) {
            return new PhotonParticleOption(buffer.readFloat(),buffer.readFloat(),buffer.readFloat(),buffer.readFloat(),buffer.readInt());
        }
    };
}
