package dev.lhkongyu.lhmiracleroad.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public abstract class AbstractBlastParticle
        extends AbstractSheetParticle {
    public static final Vector3f ROTATION_VECTOR =  Util.make(new Vector3f(0.5f, 0.5f, 0.5f), Vector3f::normalize);
    public static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0f, -1.0f, 0.0f);
    protected final float targetSize;
    protected final int isPlane;

    public AbstractBlastParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd, CommonParticleOption options) {
        super(level, x, y, z, spriteSet, xd, yd, zd);
        this.targetSize = options.getScale();
        this.isPlane = options.getIsPlane();
        this.quadSize = 1.0f;
        this.lifetime = 8;
        this.gravity = 0.1f;
        this.friction = 1.0f;
    }

    @Override
    public float getQuadSize(float partialTick) {
        float f = (partialTick + (float)this.age) / (float)this.lifetime;
        return this.quadSize * Mth.lerp(1.0f - (1.0f - f) * (1.0f - f), this.targetSize * 0.75f, this.targetSize);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprite);
            this.move(this.xd, this.yd, this.zd);
            this.yd *= 0.85;
            this.xd *= 0.94;
            this.zd *= 0.94;
        }
    }

    public boolean shouldCull() {
        return false;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (this.isPlane >= 0) {
            this.renderRotatedParticle(buffer, camera, partialTicks, p_234005_ -> {
                p_234005_.mul(Axis.XP.rotation(0.0f));
                p_234005_.mul(Axis.ZP.rotation(-1.5707964f));
            });
            this.renderRotatedParticle(buffer, camera, partialTicks, p_234000_ -> {
                p_234000_.mul(Axis.XP.rotation((float)(-Math.PI)));
                p_234000_.mul(Axis.ZP.rotation(1.5707964f));
            });
        } else {
            super.render(buffer, camera, partialTicks);
        }
    }

    private void renderRotatedParticle(VertexConsumer consumer, Camera camera, float partialTick, Consumer<Quaternionf> quaternionConsumer) {
        Vec3 vec3 = camera.getPosition();
        float f = (float)(Mth.lerp(partialTick, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp(partialTick, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp(partialTick, this.zo, this.z) - vec3.z());
        Quaternionf quaternion = new Quaternionf().setAngleAxis(0.0f, ROTATION_VECTOR.x(), ROTATION_VECTOR.y(), ROTATION_VECTOR.z());
        quaternionConsumer.accept(quaternion);
        quaternion.transform(TRANSFORM_VECTOR);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        float f3 = this.getQuadSize(partialTick);
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternion);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }
        int j = this.getLightColor(partialTick);
        this.makeCornerVertex(consumer, avector3f[0], this.getU0(), this.getV0(), j);
        this.makeCornerVertex(consumer, avector3f[1], this.getU0(), this.getV1(), j);
        this.makeCornerVertex(consumer, avector3f[2], this.getU1(), this.getV1(), j);
        this.makeCornerVertex(consumer, avector3f[3], this.getU1(), this.getV0(), j);
    }

    private void makeCornerVertex(VertexConsumer consumer, Vector3f vec, float u, float v, int lightColor) {
        consumer.vertex(vec.x(), vec.y() + 0.08f, vec.z()).uv(u, v).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(lightColor).endVertex();
    }
}
