package dev.lhkongyu.lhmiracleroad.client.shaders;

import net.minecraft.client.renderer.ShaderInstance;

import javax.annotation.Nullable;

public class LHInternalShaders {

    private static ShaderInstance renderTypeMagic;

    private static ShaderInstance renderTypeItemOutline;

    @Nullable
    public static ShaderInstance getRenderTypeMagic() {
        return renderTypeMagic;
    }

    public static void setRenderTypeMagic(ShaderInstance instance) {
        renderTypeMagic = instance;
    }

    @Nullable
    public static ShaderInstance getRenderTypeItemOutline() {
        return renderTypeItemOutline;
    }

    public static void setRenderTypeItemOutline(ShaderInstance instance) {
        renderTypeItemOutline = instance;
    }

}
