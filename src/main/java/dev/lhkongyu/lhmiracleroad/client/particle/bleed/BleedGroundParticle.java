package dev.lhkongyu.lhmiracleroad.client.particle.bleed;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.lhkongyu.lhmiracleroad.client.particle.AbstractSheetParticle;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class BleedGroundParticle extends  AbstractSheetParticle{
    public static final Vector3f ROTATION_VECTOR = Util.make(new Vector3f(0.5f, 0.5f, 0.5f), Vector3f::normalize);
    public static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0f, -1.0f, 0.0f);
    private static final float DEGREES_90 = Mth.PI / 2f;
    private final TextureAtlasSprite textureAtlasSprite;

    public BleedGroundParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord,spriteSet, xd, yd, zd);

        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 1f;
        this.scale(2.5f + (float) Math.random());
        this.lifetime = 40 + (int) (Math.random() * 41);
        this.gravity = 1.0F;
        this.textureAtlasSprite = spriteSet.get(this.random.nextInt(8), 8);
        this.setSprite(textureAtlasSprite);
        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    @Override
    protected ParticleRenderType renderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSprite(textureAtlasSprite);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new BleedGroundParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

    public boolean shouldCull() {
        return false;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        this.alpha = 1.0F - Mth.clamp((this.age + partialTicks) / (float) this.lifetime, 0, 1F);
        this.renderRotatedParticle(buffer, camera, partialTicks, (p_234005_) -> {
            p_234005_.mul(Axis.YP.rotation(0));
            p_234005_.mul(Axis.XP.rotation(-DEGREES_90));
        });
        this.renderRotatedParticle(buffer, camera, partialTicks, (p_234000_) -> {
            p_234000_.mul(Axis.YP.rotation(-(float) Math.PI));
            p_234000_.mul(Axis.XP.rotation(DEGREES_90));
        });
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
