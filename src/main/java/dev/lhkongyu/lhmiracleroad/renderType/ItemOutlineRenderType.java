package dev.lhkongyu.lhmiracleroad.renderType;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.lhkongyu.lhmiracleroad.client.shaders.LHInternalShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.InventoryMenu;

public class ItemOutlineRenderType extends RenderType {

    private static final ShaderStateShard RENDERTYPE_ITEM_OUTLINE = new ShaderStateShard(LHInternalShaders::getRenderTypeItemOutline);

    private static final RenderType OUTLINE = create("lh_item_outline",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_ITEM_OUTLINE)
                    .setTextureState(new TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));

    public ItemOutlineRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType getRenderType() {
        return OUTLINE;
    }
}
