package dev.lhkongyu.lhmiracleroad.client.screen.occupation;

import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.occupation.Occupation;
import dev.lhkongyu.lhmiracleroad.config.occupation.OccupationAttribute;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributePointsMessage;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributeValuesRequest;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributeValuesSnapshot.AttributeValue;
import dev.lhkongyu.lhmiracleroad.packet.PlayerChannel;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.PlayerAttributeTool;
import dev.lhkongyu.lhmiracleroad.tool.ResourceLocationTool;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class UpgradeInterfaceScreen extends OccupationTextureScreen {

    private static final int MAX_DISPLAY_VALUE = 999_999_999;

    private static final int ATTRIBUTES_PER_PAGE = 9;

    private static final int VALUE_PANEL_RIGHT_X = 133;

    private static final int VALUE_PAGE_BUTTON_Y = 110;

    private static final int VALUE_TEXT_COLOR = 0x000000;

    private static final int VALUE_COLUMN_GAP = 4;

    private static final float VALUE_LIST_SCALE = 0.85F;

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/new/occupation_background.png");

    private static final ResourceLocation ONE_UPGRADE =
            new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/new/one_upgrade.png");

    private static final ResourceLocation TEN_UPGRADE =
            new ResourceLocation(LHMiracleRoad.MODID, "textures/gui/new/ten_upgrade.png");

    private static final int UPGRADE_FRAME_SIZE = 16;

    private static final int UPGRADE_ICON_SIZE = UPGRADE_FRAME_SIZE / 2;

    private static final int UPGRADE_ICON_GAP = 2;

    private final List<UpgradeRow> rows = new ArrayList<>();

    private Occupation buttonOccupation;

    private List<AttributeValue> valueAttributes = List.of();

    private boolean attributeValuesRequested;

    private int valuePage;

    private Component hoveredText;

    private OccupationImageButton previousValuePageButton;

    private OccupationImageButton nextValuePageButton;

    public UpgradeInterfaceScreen() {
        super(BACKGROUND, List.of(), List.of(
                titleImage(75, 21),
                ocTitleFrameImage(75, 56)
        ), Component.translatable("lhmiracleroad.gui.titles.attribute"),
                Component.translatable("lhmiracleroad.gui.titles.upgrade"));
    }

    @Override
    protected void init() {
        super.init();
        previousValuePageButton = addImageButton(pageLeftButton(78, VALUE_PAGE_BUTTON_Y));
        nextValuePageButton = addImageButton(pageRightButton(124, VALUE_PAGE_BUTTON_Y));
        setValuePage(valuePage);
        if (!attributeValuesRequested && minecraft != null && minecraft.getConnection() != null) {
            attributeValuesRequested = true;
            PlayerChannel.sendToServer(new PlayerAttributeValuesRequest());
        }
        rows.clear();
        buttonOccupation = displayedOccupation();
        if (buttonOccupation == null || buttonOccupation.getOccupationAttributes() == null) return;

        int tenX = ATTRIBUTE_RIGHT_X - UPGRADE_ICON_SIZE;
        int oneX = tenX - UPGRADE_ICON_GAP - UPGRADE_ICON_SIZE;
        for (OccupationAttribute attribute : buttonOccupation.getOccupationAttributes()) {
            if (attribute == null || attribute.getType_id() == null || attribute.getType_id().isBlank()) continue;

            int y = attributeRowY(rows.size());
            rows.add(new UpgradeRow(attribute, addUpgradeButton(attribute, ONE_UPGRADE, oneX, y, 1),
                    addUpgradeButton(attribute, TEN_UPGRADE, tenX, y, 10)));
        }
        updateUpgradeButtons();
    }

    @Override
    public void tick() {
        if (displayedOccupation() != buttonOccupation) {
            rebuildWidgets();
        } else {
            updateUpgradeButtons();
        }
    }

    private void updateUpgradeButtons() {
        if (rows.isEmpty()) return;
        PlayerOccupationAttribute attributes = playerOccupationAttribute();
        boolean canPayOne = attributes != null && PlayerAttributeTool.canPayForUpgrade(attributes, 1);
        boolean canPayTen = attributes != null && PlayerAttributeTool.canPayForUpgrade(attributes, 10);

        for (UpgradeRow row : rows) {
            OccupationAttribute type = row.attribute();
            Integer level = attributes == null ? null : attributes.getOccupationAttributeLevel().get(type.getType_id());
            row.one().active = canPayOne && level != null
                    && PlayerAttributeTool.isWithinAttributeLimit(attributes, level, type.getMax_lv(), 1);
            row.ten().active = canPayTen && level != null
                    && PlayerAttributeTool.isWithinAttributeLimit(attributes, level, type.getMax_lv(), 10);
        }
    }

    @Override
    protected Occupation displayedOccupation() {
        return occupationFor(playerOccupationAttribute());
    }

    @Override
    protected Component occupationLevelText() {
        PlayerOccupationAttribute playerAttributes = playerOccupationAttribute();
        if (playerAttributes == null || playerAttributes.getOccupationId() == null) {
            return Component.literal("LV:");
        }

        int level = playerAttributes.getOccupationLevel();
        for (String typeId : playerAttributes.getCurioAttributeLevel().keySet()) {
            level += playerAttributes.getCurioAttributeLevelValue(typeId);
        }
        return Component.literal("LV:" + level);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        hoveredText = null;
        super.render(graphics, mouseX, mouseY, partialTick);
        if (hoveredText != null) {
            graphics.renderTooltip(font, hoveredText, mouseX, mouseY);
        }
    }

    @Override
    protected void renderAdditionalContent(GuiGraphics graphics, int mouseX, int mouseY) {
        drawAttributeValues(graphics, mouseX, mouseY);
        PlayerOccupationAttribute playerAttributes = playerOccupationAttribute();
        if (playerAttributes == null) return;
        drawBurden(graphics, mouseX, mouseY, playerAttributes);

        for (int i = 0; i < rows.size(); i++) {
            OccupationAttribute attribute = rows.get(i).attribute();
            String typeId = attribute.getType_id();
            int level = playerAttributes.getOccupationAttributeLevel().getOrDefault(typeId, attribute.getInit_lv())
                    + playerAttributes.getCurioAttributeLevelValue(typeId);
            int y = attributeRowY(i);
            Component name = Component.translatable(ResourceLocationTool.ATTRIBUTE_NAME_PREFIX + typeId);
            drawAttributeNameAndLevel(graphics, name, level, y);
            if (isTextHovered(mouseX, mouseY, screenX(ATTRIBUTE_LEFT_X), y, font.width(name))) {
                setAttributeHover(attribute, level);
            }
        }
    }

    private void drawBurden(GuiGraphics graphics, int mouseX, int mouseY, PlayerOccupationAttribute attributes) {
        AttributeInstance heavyAttribute = minecraft.player.getAttribute(LHMiracleRoadAttributes.HEAVY);
        if (heavyAttribute == null) return;
        int heavy = (int) heavyAttribute.getValue() + (int) attributes.getOffhandHeavy();
        int burden = attributes.getBurden();
        Component label = Component.translatable("lhmiracleroad.gui.attribute.text.heavy", heavy, burden);
        float centerX = screenX(SELECT_X) + basePixels(SELECT_WIDTH) / 2.0F;
        float centerY = screenY(SELECT_Y) + basePixels(SELECT_HEIGHT) / 2.0F;
        int width = font.width(label);
        float scale = Math.min(1.0F, 2 * (centerX - screenX(12)) / Math.max(1, width));
        float left = centerX - width * scale / 2;
        float top = centerY - font.lineHeight * scale / 2;
        graphics.pose().pushPose();
        graphics.pose().translate(left, top, 0);
        graphics.pose().scale(scale, scale, 1);
        graphics.drawString(font, label, 0, 0, VALUE_TEXT_COLOR, false);
        graphics.pose().popPose();

        if (mouseX >= left && mouseX < left + width * scale && mouseY >= top && mouseY < top + font.lineHeight * scale) {
            String state;
            if (heavy > burden) state = "overweight";
            else if (burden > 0 && heavy >= burden * 0.75) state = "biased_weight";
            else if (burden > 0 && heavy >= burden * 0.40) state = "normal";
            else state = "light";
            hoveredText = Component.translatable("lhmiracleroad.gui.attribute.text.details.heavy." + state);
        }
    }

    public void setAttributeValues(List<AttributeValue> values) {
        valueAttributes = List.copyOf(values);
        setValuePage(valuePage);
    }

    private int valuePageCount() {
        return Math.max(1, (valueAttributes.size() + ATTRIBUTES_PER_PAGE - 1) / ATTRIBUTES_PER_PAGE);
    }

    private void setValuePage(int page) {
        valuePage = Math.max(0, Math.min(page, valuePageCount() - 1));
        previousValuePageButton.active = valuePage > 0;
        nextValuePageButton.active = valuePage + 1 < valuePageCount();
    }

    @Override
    protected void onPageLeft() {
        setValuePage(valuePage - 1);
    }

    @Override
    protected void onPageRight() {
        setValuePage(valuePage + 1);
    }

    private void drawAttributeValues(GuiGraphics graphics, int mouseX, int mouseY) {
        int start = valuePage * ATTRIBUTES_PER_PAGE;
        int end = Math.min(start + ATTRIBUTES_PER_PAGE, valueAttributes.size());
        if (start == end) return;

        int left = screenX(INITIAL_ITEM_START_X);
        int right = screenX(VALUE_PANEL_RIGHT_X);
        int top = screenY(INITIAL_ITEM_START_Y);
        int listWidth = (int) ((right - left) / VALUE_LIST_SCALE);
        int nameWidth = (listWidth - VALUE_COLUMN_GAP) / 2;
        int valueWidth = listWidth - nameWidth - VALUE_COLUMN_GAP;
        float localMouseX = (mouseX - left) / VALUE_LIST_SCALE;
        float localMouseY = (mouseY - top) / VALUE_LIST_SCALE;

        graphics.pose().pushPose();
        graphics.pose().translate(left, top, 0);
        graphics.pose().scale(VALUE_LIST_SCALE, VALUE_LIST_SCALE, 1.0F);
        for (int i = start; i < end; i++) {
            AttributeValue attribute = valueAttributes.get(valueAttributes.size() - 1 - i);
            String name = attributeValueName(attribute.id());
            String visibleName = font.plainSubstrByWidth(name, nameWidth);
            String value = formatAttributeValue(attribute.value());
            boolean clippedValue = font.width(value) > valueWidth;
            String visibleValue = clippedValue
                    ? "..." + font.plainSubstrByWidth(value, valueWidth - font.width("..."), true) : value;
            int valueX = listWidth - font.width(visibleValue);
            int y = (i - start) * font.lineHeight;
            graphics.drawString(font, visibleName, 0, y, VALUE_TEXT_COLOR, false);
            graphics.drawString(font, visibleValue, valueX, y, VALUE_TEXT_COLOR, false);

            if (localMouseY >= y && localMouseY < y + font.lineHeight) {
                if (localMouseX >= 0 && localMouseX < font.width(visibleName)) {
                    hoveredText = Component.literal(name);
                } else if (clippedValue && localMouseX >= valueX && localMouseX < listWidth) {
                    hoveredText = Component.literal(value);
                }
            }
        }
        graphics.pose().popPose();

        if (valuePageCount() > 1) {
            String pageNumber = (valuePage + 1) + " / " + valuePageCount();
            graphics.drawString(font, pageNumber, (left + right - font.width(pageNumber)) / 2,
                    screenY(VALUE_PAGE_BUTTON_Y), VALUE_TEXT_COLOR, false);
        }
    }

    private static String attributeValueName(ResourceLocation id) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(id);
        if (attribute != null && Language.getInstance().has(attribute.getDescriptionId())) {
            return Component.translatable(attribute.getDescriptionId()).getString();
        }
        return id.toString();
    }

    private static String formatAttributeValue(double value) {
        if (!Double.isFinite(value)) return Double.toString(value);
        BigDecimal rounded = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
        BigDecimal trimmed = rounded.stripTrailingZeros();
        return (trimmed.scale() <= 0 ? trimmed : rounded).toPlainString();
    }

    private OccupationImageButton addUpgradeButton(OccupationAttribute attribute, ResourceLocation texture,
                                                   int logicalX, int rowY, int count) {
        int x = screenX(logicalX);
        int size = basePixels(UPGRADE_ICON_SIZE);
        int y = rowY - (size - font.lineHeight) / 2;
        return addRenderableWidget(new OccupationImageButton(x, y, size, size, Component.literal("+" + count),
                button -> {
                    PlayerChannel.sendToServer(new PlayerAttributePointsMessage(attribute.getType_id(), count));
                    PlayerChannel.sendToServer(new PlayerAttributeValuesRequest());
                },
                texture, 0, 0, 0, UPGRADE_FRAME_SIZE, UPGRADE_FRAME_SIZE, UPGRADE_FRAME_SIZE * 2,
                size / (float) UPGRADE_FRAME_SIZE, UPGRADE_FRAME_SIZE, UPGRADE_FRAME_SIZE, false));
    }

    @Override
    protected List<StatusRow> statusRows() {
        int points = 0;
        int heldSoul = 0;
        int requiredSoul = 0;

        PlayerOccupationAttribute occupationAttribute = playerOccupationAttribute();
        if (occupationAttribute != null) {
            points = occupationAttribute.getPoints();
            heldSoul = occupationAttribute.getOccupationExperience();
            String formula = occupationAttribute.getEmpiricalCalculationFormula();
            if (formula != null && !formula.isBlank()) {
                requiredSoul = LHMiracleRoadTool.evaluateFormula(formula, occupationAttribute.getOccupationLevel());
            }
        }

        return List.of(
                new StatusRow(Component.translatable("lhmiracleroad.gui.attribute.text.points_label"), displayValue(points)),
                new StatusRow(Component.translatable("lhmiracleroad.gui.attribute.text.hold_soul"), displayValue(heldSoul)),
                new StatusRow(Component.translatable("lhmiracleroad.gui.attribute.text.demand_soul"), displayValue(requiredSoul))
        );
    }

    private PlayerOccupationAttribute playerOccupationAttribute() {
        if (minecraft == null || minecraft.player == null) return null;
        return minecraft.player.getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER)
                .resolve().orElse(null);
    }

    private static Occupation occupationFor(PlayerOccupationAttribute playerAttributes) {
        if (playerAttributes == null || playerAttributes.getOccupationId() == null) return null;
        return ClientData.OCCUPATION_CATALOG.get(playerAttributes.getOccupationId());
    }

    private static Component displayValue(int value) {
        return Component.literal(String.valueOf(Math.min(value, MAX_DISPLAY_VALUE)));
    }

    private record UpgradeRow(OccupationAttribute attribute, OccupationImageButton one, OccupationImageButton ten) {
    }
}
