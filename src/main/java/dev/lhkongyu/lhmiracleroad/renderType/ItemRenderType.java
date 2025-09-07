package dev.lhkongyu.lhmiracleroad.renderType;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.NameTool;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class ItemRenderType extends RenderType {
    public ItemRenderType(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    private static final RenderType FLAME_GLINT;

    private static final RenderType LIGHTNING_GLINT;

    private static final RenderType DARK_GLINT;

    private static final RenderType BLOOD_GLINT;

    private static final RenderType HOLY_GLINT;

    private static final RenderType MAGIC_GLINT;

    private static final RenderType POISON_GLINT;

    private static final RenderType ICE_GLINT;

    private static final RenderType SHARP_GLINT;


    public static RenderType getFlameGlint() {
        return FLAME_GLINT;
    }

    public static RenderType getLightningGlint() {
        return LIGHTNING_GLINT;
    }

    public static RenderType getDarkGlint() {
        return DARK_GLINT;
    }

    public static RenderType getBloodGlint() {
        return BLOOD_GLINT;
    }

    public static RenderType getHolyGlint() {
        return HOLY_GLINT;
    }

    public static RenderType getMagicGlint() {
        return MAGIC_GLINT;
    }

    public static RenderType getPoisonGlint() {
        return POISON_GLINT;
    }

    public static RenderType getIceGlint() {
        return ICE_GLINT;
    }

    public static RenderType getSharpGlint() {
        return SHARP_GLINT;
    }

    public static ResourceLocation getTexture(String png){
        return LHMiracleRoadTool.resourceLocationId(png);
    }

    public static RenderType createPixelRenderType(String id,String png,float speed,float scale){
        return create("lh_"+id,
                DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.QUADS,
                256,
                false,
                false,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
                        .setTextureState(new TextureStateShard(getTexture(png), true, false))
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .setCullState(NO_CULL)
                        .setDepthTestState(EQUAL_DEPTH_TEST)
                        .setTransparencyState(GLINT_TRANSPARENCY)
                        .setTexturingState(new customizeTexturing(speed,scale))
                        .createCompositeState(false));
    }

    private static final class customizeTexturing extends TexturingStateShard {
        public customizeTexturing(float speed,float scale) {
            super("customize_texturing_scale", () -> {
                        long millis = Util.getMillis();
                        long l = (long) (millis * Minecraft.getInstance().options.glintSpeed().get() * speed);
                        float f = (float) (l % 100000L) / 100000.0F;
                        float g = (float) (l % 30000L) / 30000.0F;

                        RenderSystem.setTextureMatrix((new Matrix4f()).translation(f, g, 0.0F).scale(scale));
                    }
                    , RenderSystem::resetTextureMatrix);
        }
    }

    static {
        FLAME_GLINT = createPixelRenderType("fire_glint","textures/glints/fire_glint.png",16.0f,64.0F);
        LIGHTNING_GLINT = createPixelRenderType("lightning_glint","textures/glints/lightning_glint.png",64.0f,32.0F);
        DARK_GLINT = createPixelRenderType("dark_glint","textures/glints/dark_glint.png",16.0f,48.0F);
        BLOOD_GLINT = createPixelRenderType("blood_glint","textures/glints/blood_glint.png",16.0f,64.0F);
        HOLY_GLINT = createPixelRenderType("holy_glint","textures/glints/holy_glint.png",16.0f,64.0F);
        MAGIC_GLINT = createPixelRenderType("magic_glint","textures/glints/magic_glint.png",16.0f,64.0F);
        POISON_GLINT = createPixelRenderType("poison_glint","textures/glints/poison_glint.png",16.0f,64.0F);
        ICE_GLINT = createPixelRenderType("ice_glint","textures/glints/ice_glint.png",16.0f,32.0F);
        SHARP_GLINT  = createPixelRenderType("ice_glint","textures/glints/sharp_glint.png",16.0f,32.0F);
    }

    public static RenderType getGlint(String type){
        return switch (type){
            case NameTool.FLAME -> getFlameGlint();
            case NameTool.LIGHTNING -> getLightningGlint();
            case NameTool.DARK -> getDarkGlint();
            case NameTool.BLOOD -> getBloodGlint();
            case NameTool.MAGIC -> getMagicGlint();
            case NameTool.HOLY -> getHolyGlint();
            case NameTool.POISON -> getPoisonGlint();
            case NameTool.ICE -> getIceGlint();
            case NameTool.SHARP -> getSharpGlint();
            default -> RenderType.entityGlintDirect();
        };
    }

}
