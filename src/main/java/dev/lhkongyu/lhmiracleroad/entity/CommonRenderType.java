package dev.lhkongyu.lhmiracleroad.entity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.lhkongyu.lhmiracleroad.shaders.LHInternalShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class CommonRenderType extends RenderType {

    protected static final ShaderStateShard RENDERTYPE_MAGIC = new ShaderStateShard(LHInternalShaders::getRenderTypeMagic);


    public CommonRenderType(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    public static RenderType magic(ResourceLocation locationIn) {
        return create("magic",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536, false, true,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_MAGIC)
                        .setTextureState(new TextureStateShard(locationIn, false, false))
                        .setTexturingState(new OffsetTexturingStateShard(0, 0))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(NO_OVERLAY)
                        .createCompositeState(false));
    }
}
