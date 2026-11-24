package dev.lhkongyu.lhmiracleroad.client.screen.occupation;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationAttribute;
import dev.lhkongyu.lhmiracleroad.config.occupation.PointsReward;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.ResourceLocationTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class OccupationTextureScreen extends Screen {

    private static final float BASE_SCALE = 1.5F;

    private static final float BACKGROUND_ZOOM = 1.15F;

    private static final float LAYOUT_SCALE = BASE_SCALE * BACKGROUND_ZOOM;

    private static final int TEXT_COLOR = 0x000000;

    protected static final int BACKGROUND_WIDTH = 256;

    protected static final int BACKGROUND_HEIGHT = 128;

    protected static final int ATTRIBUTE_LEFT_X = 154;

    private static final int ATTRIBUTE_CENTER_X = 198;

    protected static final int ATTRIBUTE_RIGHT_X = 242;

    private static final int ATTRIBUTE_START_Y = 30;

    private static final int ATTRIBUTE_ROW_STEP = 10;

    protected static final ResourceLocation PAGE =
            new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/new/page.png");

    protected static final ResourceLocation SELECT =
            new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/new/select.png");

    protected static final ResourceLocation TITLE =
            new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/new/title.png");

    protected static final ResourceLocation TITLE_FRAME =
            new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/new/title_frame.png");

    protected static final ResourceLocation OC_TITLE_FRAME =
            new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/new/oc_title_frame.png");

    private static final int PAGE_BUTTON_WIDTH = 9;

    private static final int PAGE_HEIGHT = 6;

    private static final int PAGE_RIGHT_U = 10;

    private static final int PAGE_TEXTURE_WIDTH = 19;

    private static final int PAGE_TEXTURE_HEIGHT = 13;

    protected static final int SELECT_X = 30;
    protected static final int SELECT_Y = 106;
    protected static final int SELECT_WIDTH = 22;

    protected static final int SELECT_HEIGHT = 11;

    private static final int TITLE_WIDTH = 60;

    private static final int TITLE_HEIGHT = 7;

    private static final int TITLE_FRAME_WIDTH = 60;

    private static final int TITLE_FRAME_HEIGHT = 60;

    private static final int OC_TITLE_FRAME_WIDTH = 60;

    private static final int OC_TITLE_FRAME_HEIGHT = 54;

    private static final int ITEM_SIZE = 16;

    private static final int ARMOR_SLOT_CENTER_X = 19;

    protected static final int INITIAL_ITEM_START_X = 77;

    protected static final int INITIAL_ITEM_START_Y = 64;

    private static final int INITIAL_ITEM_COLUMNS = 4;

    private static final int INITIAL_ITEM_COLUMN_STEP = 15;

    private static final int INITIAL_ITEM_ROW_STEP = 10;

    private static final float INITIAL_ITEM_SCALE = 0.85F;

    private static final int PLAYER_X = 28;

    private static final int PLAYER_Y = 52;

    private static final int PLAYER_SCALE = 24;

    private static final int LV_TEXT_X = 13;

    private static final int LV_TEXT_Y = 13;

    private static final int ID_TEXT_X = 42;

    private static final int ID_TEXT_Y = 13;

    private static final int OCCUPATION_DESCRIPTION_X = 77;

    private static final int OCCUPATION_DESCRIPTION_Y = 29;

    private static final int OCCUPATION_DESCRIPTION_RIGHT_X = 133;

    private static final int OCCUPATION_DESCRIPTION_MAX_LINES = 3;

    private static final int STATUS_LABEL_X = 73;

    private static final int STATUS_VALUE_X = 106;

    private static final int STATUS_START_Y = 29;

    private static final float STATUS_TEXT_SCALE = 1F;

    private final ResourceLocation background;

    private final List<ButtonDefinition> buttonDefinitions;

    private final List<ImageDefinition> imageDefinitions;

    private final Component titleFrameText;

    private final Component rightTitleText;

    private int leftPos;

    private int topPos;

    private List<ItemRenderData> itemRenderData = List.of();

    private AttributeHover attributeHover;

    protected OccupationTextureScreen(ResourceLocation background, List<ButtonDefinition> buttonDefinitions,
                                      List<ImageDefinition> imageDefinitions, Component titleFrameText,
                                      Component rightTitleText) {
        super(Component.empty());
        super.minecraft = Minecraft.getInstance();
        this.background = background;
        this.buttonDefinitions = buttonDefinitions;
        this.imageDefinitions = imageDefinitions;
        this.titleFrameText = titleFrameText;
        this.rightTitleText = rightTitleText;
    }

    @Override
    protected void init() {
        leftPos = (width - scalePosition(BACKGROUND_WIDTH)) / 2;
        topPos = (height - scalePosition(BACKGROUND_HEIGHT)) / 2;

        for (ButtonDefinition definition : buttonDefinitions) {
            addImageButton(definition);
        }
    }

    protected final OccupationImageButton addImageButton(ButtonDefinition definition) {
        return addRenderableWidget(definition.create(this));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        renderInfoText(graphics);
        Player model = displayedPlayer();
        if (model != null) {
            renderPlayer(graphics, mouseX, mouseY, model);
            renderEquipmentItems(graphics, model);
        } else {
            itemRenderData = List.of();
        }
        attributeHover = null;
        renderAdditionalContent(graphics, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderItemTooltip(graphics, mouseX, mouseY);
        renderAttributeTooltip(graphics, mouseX, mouseY);
    }

    protected void renderAdditionalContent(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    protected final boolean isTextHovered(int mouseX, int mouseY, int x, int y, int textWidth) {
        return mouseX >= x && mouseX < x + textWidth && mouseY >= y && mouseY < y + font.lineHeight;
    }

    protected final void setAttributeHover(OccupationAttribute attribute, int level) {
        setAttributeHover(attribute, level, null);
    }

    protected final void setAttributeHover(OccupationAttribute attribute, int level, @Nullable Component header) {
        attributeHover = new AttributeHover(attribute, level, header);
    }

    protected final int screenX(int x) {
        return leftPos + scalePosition(x);
    }

    protected final int screenY(int y) {
        return topPos + scalePosition(y);
    }

    protected final int attributeRowY(int rowIndex) {
        return screenY(ATTRIBUTE_START_Y + rowIndex * ATTRIBUTE_ROW_STEP);
    }

    protected final void drawAttributeNameAndLevel(GuiGraphics graphics, Component name, int level, int maxLevel,
                                                  int y, int mouseX, int mouseY) {
        String expandedLevel = level + "/" + maxLevel;
        int expandedWidth = font.width(expandedLevel);
        int expandedX = attributeLevelLeft(expandedWidth);
        boolean hovered = isTextHovered(mouseX, mouseY, expandedX, y, expandedWidth);
        String levelText = hovered ? expandedLevel : Integer.toString(level);
        int levelX = hovered ? expandedX : attributeLevelLeft(font.width(levelText));
        graphics.drawString(font, name, screenX(ATTRIBUTE_LEFT_X), y, TEXT_COLOR, false);
        graphics.drawString(font, levelText, levelX, y, TEXT_COLOR, false);
    }

    private int attributeLevelLeft(int width) {
        return screenX(ATTRIBUTE_CENTER_X) - width / 2;
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics) {
        super.renderBackground(graphics);
        graphics.pose().pushPose();
        graphics.pose().translate(leftPos, topPos, 0);
        graphics.pose().scale(LAYOUT_SCALE, LAYOUT_SCALE, 1.0F);
        graphics.blit(background, 0, 0, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        for (ImageDefinition definition : imageDefinitions) {
            definition.render(graphics, font);
        }
        graphics.pose().popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected static ButtonDefinition pageLeftButton(int x, int y) {
        return pageButton(x, y, 0, ButtonAction.PAGE_LEFT);
    }

    protected static ButtonDefinition pageRightButton(int x, int y) {
        return pageButton(x, y, PAGE_RIGHT_U, ButtonAction.PAGE_RIGHT);
    }

    private static ButtonDefinition pageButton(int x, int y, int u, ButtonAction action) {
        return new ButtonDefinition(x, y, PAGE_BUTTON_WIDTH, PAGE_HEIGHT, Component.empty(), button -> {},
                PAGE, u, 0, u, 7, PAGE_TEXTURE_WIDTH, PAGE_TEXTURE_HEIGHT, action);
    }

    protected static ButtonDefinition selectButton(int x, int y, Button.OnPress onPress) {
        return new ButtonDefinition(x, y, SELECT_WIDTH, SELECT_HEIGHT,
                Component.translatable("lhmiracleroad.gui.text.select"), onPress,
                SELECT, 0, 0, 0, 0, SELECT_WIDTH, SELECT_HEIGHT, ButtonAction.NONE);
    }

    protected static ImageDefinition titleImage(int x, int y) {
        return new ImageDefinition(x, y, TITLE_WIDTH, TITLE_HEIGHT, TITLE);
    }

    protected static ImageDefinition titleFrameImage(int x, int y) {
        return new ImageDefinition(x, y, TITLE_FRAME_WIDTH, TITLE_FRAME_HEIGHT, TITLE_FRAME);
    }

    protected static ImageDefinition ocTitleFrameImage(int x, int y) {
        return new ImageDefinition(x, y, OC_TITLE_FRAME_WIDTH, OC_TITLE_FRAME_HEIGHT, OC_TITLE_FRAME);
    }

    protected Player displayedPlayer() {
        return minecraft.player;
    }

    private void renderPlayer(GuiGraphics graphics, int mouseX, int mouseY, Player model) {
        // Keep the preview's existing anchor and scale its position independently of the model.
        int playerX = screenX(basePixels(PLAYER_X));
        int playerY = screenY(basePixels(PLAYER_Y));
        graphics.pose().pushPose();
        graphics.pose().translate(playerX, playerY, 0);
        graphics.pose().scale(BASE_SCALE, BASE_SCALE, 1.0F);
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, 0, 0, PLAYER_SCALE,
                playerX - mouseX, playerY - 50 - mouseY, model);
        graphics.pose().popPose();
    }

    private void renderInfoText(GuiGraphics graphics) {
        if (minecraft == null || minecraft.player == null) return;

        drawScaledString(graphics, occupationLevelText(), LV_TEXT_X, LV_TEXT_Y);
        drawScaledString(graphics, Component.literal("ID:" + minecraft.player.getName().getString()), ID_TEXT_X, ID_TEXT_Y);

        if (!imageDefinitions.isEmpty()) {
            ImageDefinition occupationTitle = imageDefinitions.get(0);
            drawCenteredString(graphics, occupationTitle(), occupationTitle.x + occupationTitle.width / 2, occupationTitle.y - 5);
        }
        if (imageDefinitions.size() > 1) {
            ImageDefinition titleFrame = imageDefinitions.get(1);
            drawCenteredString(graphics, titleFrameText, titleFrame.x + titleFrame.width / 2, titleFrame.y - 5);
        }
        drawCenteredString(graphics, rightTitleText, 198, 16);
        drawOccupationDescription(graphics);
        drawStatusRows(graphics);
    }

    protected Component occupationLevelText() {
        Occupation occupation = displayedOccupation();
        if (occupation == null || occupation.getOccupationAttributes() == null) {
            return Component.literal("LV:");
        }

        int initialLevelSum = 0;
        int attributeCount = 0;
        for (OccupationAttribute attribute : occupation.getOccupationAttributes()) {
            if (attribute == null || attribute.getType_id() == null || attribute.getType_id().isBlank()) continue;
            initialLevelSum += attribute.getInit_lv();
            attributeCount++;
        }

        int level = initialLevelSum - attributeCount * LHMiracleRoadConfig.COMMON.LEVEL_BASE.get() + 1;
        return Component.literal("LV:" + level);
    }

    protected abstract Occupation displayedOccupation();

    protected Component occupationDescription() {
        return Component.empty();
    }

    private Component occupationTitle() {
        Occupation occupation = displayedOccupation();
        return occupation == null ? Component.empty()
                : Component.translatableWithFallback(ResourceLocationTool.OCCUPATION_NAME_PREFIX + occupation.getId(),
                        occupation.getName() == null ? occupation.getId() : occupation.getName());
    }

    private void drawOccupationDescription(GuiGraphics graphics) {
        Component description = occupationDescription();
        if (description.getString().isEmpty()) return;
        int x = screenX(OCCUPATION_DESCRIPTION_X);
        int width = screenX(OCCUPATION_DESCRIPTION_RIGHT_X) - x;
        List<FormattedText> lines = font.getSplitter().splitLines(description, width, Style.EMPTY);
        int visibleLines = Math.min(lines.size(), OCCUPATION_DESCRIPTION_MAX_LINES);
        for (int i = 0; i < visibleLines; i++) {
            FormattedText line = lines.get(i);
            if (i == visibleLines - 1 && lines.size() > visibleLines) {
                String ellipsis = "...";
                line = FormattedText.composite(font.substrByWidth(line, width - font.width(ellipsis)),
                        FormattedText.of(ellipsis));
            }
            graphics.drawString(font, Language.getInstance().getVisualOrder(line), x,
                    textRowY(OCCUPATION_DESCRIPTION_Y, i), TEXT_COLOR, false);
        }
    }

    protected List<StatusRow> statusRows() {
        return List.of();
    }

    private void drawStatusRows(GuiGraphics graphics) {
        int labelX = screenX(STATUS_LABEL_X);
        int valueX = screenX(STATUS_VALUE_X);
        int maxLabelWidth = valueX - labelX - 2;
        int rowIndex = 0;
        for (StatusRow row : statusRows()) {
            int y = textRowY(STATUS_START_Y, rowIndex++);
            String label = row.label.getString();
            float labelScale = Math.min(STATUS_TEXT_SCALE, maxLabelWidth / (float) Math.max(1, font.width(label)));
            drawStatusText(graphics, label, labelX, y, labelScale);
            drawStatusText(graphics, row.value.getString(), valueX, y, STATUS_TEXT_SCALE);
        }
    }

    private int textRowY(int startY, int rowIndex) {
        return screenY(startY) + Math.round(rowIndex * font.lineHeight * BACKGROUND_ZOOM);
    }

    private void drawStatusText(GuiGraphics graphics, String text, int x, int y, float scale) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y + (STATUS_TEXT_SCALE - scale) * font.lineHeight / 2, 0);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.drawString(font, text, 0, 0, TEXT_COLOR, false);
        graphics.pose().popPose();
    }

    private void drawScaledString(GuiGraphics graphics, Component component, int x, int y) {
        graphics.drawString(font, component, screenX(x), screenY(y), TEXT_COLOR, false);
    }

    private void drawCenteredString(GuiGraphics graphics, Component component, int centerX, int y) {
        int textX = screenX(centerX) - font.width(component) / 2;
        graphics.drawString(font, component, textX, screenY(y), TEXT_COLOR, false);
    }

    private void renderEquipmentItems(GuiGraphics graphics, Player model) {
        List<ItemRenderData> renderedItems = new ArrayList<>(List.of(
                renderArmorItem(graphics, model.getItemBySlot(EquipmentSlot.HEAD), 43),
                renderArmorItem(graphics, model.getItemBySlot(EquipmentSlot.CHEST), 57),
                renderArmorItem(graphics, model.getItemBySlot(EquipmentSlot.LEGS), 71),
                renderArmorItem(graphics, model.getItemBySlot(EquipmentSlot.FEET), 85),
                renderItem(graphics, model.getMainHandItem(), 29, 88),
                renderItem(graphics, model.getOffhandItem(), 44, 88)
        ));
        List<ItemStack> extraItems = additionalItems();
        for (int i = 0; i < extraItems.size(); i++) {
            int x = INITIAL_ITEM_START_X + (i % INITIAL_ITEM_COLUMNS) * INITIAL_ITEM_COLUMN_STEP;
            int y = INITIAL_ITEM_START_Y + (i / INITIAL_ITEM_COLUMNS) * INITIAL_ITEM_ROW_STEP;
            renderedItems.add(renderItem(graphics, extraItems.get(i), x, y, INITIAL_ITEM_SCALE));
        }
        itemRenderData = List.copyOf(renderedItems);
    }

    protected List<ItemStack> additionalItems() {
        return List.of();
    }

    private ItemRenderData renderArmorItem(GuiGraphics graphics, ItemStack stack, int centerY) {
        int x = screenX(ARMOR_SLOT_CENTER_X) - ITEM_SIZE / 2;
        int y = screenY(centerY) - ITEM_SIZE / 2;
        return renderItemAt(graphics, stack, x, y, 1.0F);
    }

    private ItemRenderData renderItem(GuiGraphics graphics, ItemStack itemStack, int x, int y) {
        return renderItem(graphics, itemStack, x, y, 1.0F);
    }

    private ItemRenderData renderItem(GuiGraphics graphics, ItemStack itemStack, int x, int y, float itemScale) {
        return renderItemAt(graphics, itemStack, screenX(x), screenY(y), itemScale);
    }

    private ItemRenderData renderItemAt(GuiGraphics graphics, ItemStack itemStack, int itemX, int itemY, float itemScale) {
        int renderedSize = Math.round(ITEM_SIZE * itemScale);
        if (!itemStack.isEmpty()) {
            graphics.pose().pushPose();
            graphics.pose().translate(itemX, itemY, 0);
            graphics.pose().scale(itemScale, itemScale, 1.0F);
            graphics.renderItem(itemStack, 0, 0);
            graphics.renderItemDecorations(font, itemStack, 0, 0);
            graphics.pose().popPose();
        }
        return new ItemRenderData(itemStack, itemX, itemY, renderedSize);
    }

    private void renderItemTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        for (ItemRenderData renderData : itemRenderData) {
            if (!renderData.itemStack.isEmpty() && renderData.isMouseOver(mouseX, mouseY)) {
                graphics.renderTooltip(font, renderData.itemStack, mouseX, mouseY);
                return;
            }
        }
    }

    private void renderAttributeTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (attributeHover == null) return;

        List<Component> tooltip = new ArrayList<>();
        if (attributeHover.header() != null) tooltip.add(attributeHover.header());
        List<PointsReward> rewards = attributeHover.attribute().getPointsRewards();
        if (rewards == null) rewards = List.of();
        for (PointsReward reward : rewards) {
            if (reward == null || reward.getAttribute_id() == null || reward.getAttribute_id().isBlank()) continue;

            AttributeModifier.Operation operation = LHMiracleRoadTool.stringConversionOperation(reward.getOperation());
            if (operation == null) continue;

            String amount = formatRewardAmount(attributeHover.level(), reward, operation);
            tooltip.add(rewardAttributeName(reward).copy().append(amount));
        }
        if (!tooltip.isEmpty()) {
            graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }
    }

    private static String formatRewardAmount(int level, PointsReward reward, AttributeModifier.Operation operation) {
        double increase = LHMiracleRoadTool.calculateTotalIncrease(level, reward.getValue(),
                reward.getLv_promote_value(), reward.getLv_promote(), reward.getMin());
        boolean percentage = operation != AttributeModifier.Operation.ADDITION;
        BigDecimal amount = BigDecimal.valueOf(increase);
        if (percentage) amount = amount.movePointRight(2);

        String sign = amount.signum() < 0 ? " - " : " + ";
        return sign + amount.abs().stripTrailingZeros().toPlainString() + (percentage ? "%" : "");
    }

    private static Component rewardAttributeName(PointsReward reward) {
        String attributeId = reward.getAttribute_id();
        String namespace = reward.getMod_id();
        if (namespace == null || namespace.isBlank()) namespace = "minecraft";
        ResourceLocation id = ResourceLocation.tryParse(attributeId.contains(":")
                ? attributeId : namespace + ":" + attributeId);
        if (id == null) return Component.literal(attributeId);

        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(id);
        // The config also accepts vanilla short IDs such as max_health.
        if (attribute == null && id.getNamespace().equals("minecraft") && !id.getPath().contains(".")) {
            attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("minecraft", "generic." + id.getPath()));
        }
        if (attribute != null && Language.getInstance().has(attribute.getDescriptionId())) {
            return Component.translatable(attribute.getDescriptionId());
        }

        String translationKey = "attribute.name." + id.getNamespace() + "." + id.getPath();
        if (Language.getInstance().has(translationKey)) {
            return Component.translatable(translationKey);
        }
        return Component.literal(id.toString());
    }

    private record AttributeHover(OccupationAttribute attribute, int level, @Nullable Component header) {
    }

    protected record ButtonDefinition(int x, int y, int width, int height, Component message, Button.OnPress onPress,
                                      ResourceLocation texture, int normalU, int normalV, int hoverU, int hoverV,
                                      int textureWidth, int textureHeight, ButtonAction action) {

        private OccupationImageButton create(OccupationTextureScreen screen) {
            Button.OnPress press = switch (action) {
                case PAGE_LEFT -> button -> screen.onPageLeft();
                case PAGE_RIGHT -> button -> screen.onPageRight();
                case NONE -> onPress;
            };
            return new OccupationImageButton(screen.screenX(x), screen.screenY(y), basePixels(width), basePixels(height),
                    message, press, texture, normalU, normalV, hoverU, hoverV, textureWidth, textureHeight, BASE_SCALE,
                    width, height);
        }
    }

    protected enum ButtonAction {
        NONE,
        PAGE_LEFT,
        PAGE_RIGHT
    }

    protected void onPageLeft() {
    }

    protected void onPageRight() {
    }

    protected record StatusRow(Component label, Component value) {
    }

    protected record ImageDefinition(int x, int y, int width, int height, ResourceLocation texture) {

        private void render(GuiGraphics graphics, net.minecraft.client.gui.Font font) {
            graphics.blit(texture, x, y, 0, 0, width, height, width, height);
        }
    }

    private record ItemRenderData(ItemStack itemStack, int x, int y, int size) {

        private boolean isMouseOver(int mouseX, int mouseY) {
            return mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size;
        }
    }

    protected static int basePixels(int value) {
        return Math.round(value * BASE_SCALE);
    }

    private static int scalePosition(int value) {
        return Math.round(value * LAYOUT_SCALE);
    }
}
