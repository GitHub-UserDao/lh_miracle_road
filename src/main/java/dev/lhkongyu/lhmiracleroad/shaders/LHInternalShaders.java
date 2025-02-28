package dev.lhkongyu.lhmiracleroad.shaders;

import net.minecraft.client.renderer.ShaderInstance;

import javax.annotation.Nullable;

public class LHInternalShaders {

    private static ShaderInstance renderTypeMagic;

    @Nullable
    public static ShaderInstance getRenderTypeMagic() {
        return renderTypeMagic;
    }

    public static void setRenderTypeMagic(ShaderInstance instance) {
        renderTypeMagic = instance;
    }

}
