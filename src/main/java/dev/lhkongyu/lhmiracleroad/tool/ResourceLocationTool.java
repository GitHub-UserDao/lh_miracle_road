package dev.lhkongyu.lhmiracleroad.tool;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationTool {

    public static class Gui{
        public static final ResourceLocation ADD = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/add.png");
        public static final ResourceLocation ADD_TOUCH = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/add_touch.png");
        public static final ResourceLocation background = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/background.png");
        public static final ResourceLocation frame = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/frame.png");
        public static final ResourceLocation pageLeft = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/page_left.png");
        public static final ResourceLocation pageLeftButton = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/page_left_button.png");
        public static final ResourceLocation pageRight = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/page_right.png");
        public static final ResourceLocation pageRightButton = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/page_right_button.png");
        public static final ResourceLocation select = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/select.png");
        public static final ResourceLocation selectButton = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/select_button.png");
        public static final ResourceLocation title = new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/title.png");
    }

    public static final String OCCUPATION_IMAGE_PREFIX = "textures/occupation/";

    public static final String OCCUPATION_IMAGE_SUFFIX = ".png";

    public static final String OCCUPATION_NAME_PREFIX = "lhmiracleroad.gui.occupation.name.";

    public static final String OCCUPATION_DESCRIBE_PREFIX = "lhmiracleroad.gui.occupation.describe.";

    public static final String ATTRIBUTE_NAME_PREFIX = "lhmiracleroad.gui.attribute.name.";

    public static final String ATTRIBUTE_DETAILS_TEXT_PREFIX = "lhmiracleroad.gui.attribute.text.details.";

    public static final String ATTRIBUTE_TOOLTIP_DETAILS_PREFIX = "lhmiracleroad.tooltip.describe.";

    public static final String DEFAULT = "default";
}
