package dev.lhkongyu.lhmiracleroad.screen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.attributes.ShowAttributesTypes;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttributeProvider;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributeChannel;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributePointsMessage;
import dev.lhkongyu.lhmiracleroad.tool.AttributesNameTool;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.ResourceLocationTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Stream;

public class LHMiracleRoadMainScreen extends Screen {

    private InitCoordinate initCoordinate;

    private InitMainCoordinate initMainCoordinate;

    private int current;

    private List<Component> itemHoverTooltip = null;

    private int itemHoverTooltipX = 0;

    private int itemHoverTooltipY = 0;

    private List<Component> detailsHoverTooltip = null;

    private int detailsHoverTooltipX = 0;

    private int detailsHoverTooltipY = 0;

    private final int backgroundWidth = 450;

    private final int backgroundHeight = 245;

    private int widthCore;

    private int heightCore;

    private PlayerOccupationAttribute playerOccupationAttribute;

    private int totalPage;

    private boolean isShowPointsButton;


    public LHMiracleRoadMainScreen(int current) {
        super(Component.empty());
        super.minecraft = Minecraft.getInstance();
        this.current = current;
        this.isShowPointsButton = true;
    }

    @Override
    public void init() {
        if (minecraft != null) {
            syncAbility();
            widthCore = (super.width - backgroundWidth) / 2;
            heightCore = (super.height - backgroundHeight) / 2;

//            currentGuiScale = minecraft.options.guiScale().get();
            initCoordinate = new InitCoordinate(widthCore,heightCore,backgroundWidth,backgroundHeight,font,playerOccupationAttribute.getOccupationId());
            initMainCoordinate = new InitMainCoordinate(widthCore,heightCore,backgroundWidth,backgroundWidth,font,playerOccupationAttribute);
            totalPage = 2;
            totalPage += initMainCoordinate.setShowDetailedAttributePage();
            if (current == 0) showButton(false,true);
            else showButton(true, current != totalPage - 1);
            showPointsButton();
        }
    }

    @Override
    public void tick() {
        textFields().forEach(EditBox::tick);
    }

    private Stream<EditBox> textFields() {
        return children().stream().filter(EditBox.class::isInstance).map(EditBox.class::cast);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics); // 渲染背景
        super.render(graphics, mouseX, mouseY, partialTick);
        syncAbility();
        if (current == 0) {
            // 渲染文字
            showTitle(graphics);
            showOccupationName(graphics);
            showDescribe(graphics);
            showOccupation(graphics);
            showAttribute(graphics, mouseX, mouseY);

            if (itemHoverTooltip != null) {
                graphics.renderComponentTooltip(font, itemHoverTooltip, itemHoverTooltipX, itemHoverTooltipY);
            }
        }else if (current == 1){
            showLevelInformation(graphics);
            showAttributePoints(graphics, mouseX, mouseY);
            showBurden(graphics, mouseX, mouseY);
        }
        showDetailedAttribute(graphics);

        if (detailsHoverTooltip != null) {
            graphics.renderComponentTooltip(font, detailsHoverTooltip, detailsHoverTooltipX, detailsHoverTooltipY);
        }
        if ((initMainCoordinate.getHoldExperienceValue() > initMainCoordinate.getDemandExperienceValue() || playerOccupationAttribute.getPoints() > 0) && isShowPointsButton) {
            this.clearWidgets();
            showPointsButton();
            if (current == 0) showButton(false,true);
            else showButton(true, current != totalPage - 1);
        }
    }

    private void lastPage() {
        if (current > 0) {
            current--;
            this.clearWidgets();
            if (current == 0){
                showButton(false,true);
            }else {
                showButton(true,true);
            }
            showPointsButton();
        }
    }

    private void nextPage() {
        if (current < totalPage - 1) {
            current++;
            this.clearWidgets();
            if (current == totalPage - 1){
                showButton(true,false);
            }else {
                showButton(true,true);
            }
            showPointsButton();
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);
        //背景图
        graphics.blit(ResourceLocationTool.Gui.background, widthCore, heightCore, 0, 0F, 0F, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
        if (current == 0) graphics.blit(ResourceLocationTool.Gui.frame, initCoordinate.getFrameX(), initCoordinate.getFrameY(), 0, 0, initCoordinate.getFrameWidth(),initCoordinate.getFrameHeight(),
                initCoordinate.getFrameWidth(),  initCoordinate.getFrameHeight());
    }

    private void showTitle(GuiGraphics graphics){
        graphics.drawString(font, initCoordinate.getInitAttributeComponent(), initCoordinate.getInitAttributeX(), initCoordinate.getInitAttributeY(), 0x6C5734, false);
    }

    private void showOccupationName(GuiGraphics graphics){
        int x = initCoordinate.getOccupationNameX();
        int y = initCoordinate.getOccupationNameY();

        graphics.drawString(font, initCoordinate.getOccupationNameComponent(), x, y, 0x000000, false);
    }

    private void showDescribe(GuiGraphics graphics){
        int startY = initCoordinate.getDescribeInitY();
        List<String> lines = initCoordinate.getDescribeTexts();
        for (int i = 0; i < lines.size(); i++) {
            if (i == 0){
                graphics.drawString(font, Component.literal(lines.get(i)), initCoordinate.getDescribeOneLnInitX(), startY, 0x000000, false);
            }else {
                graphics.drawString(font, Component.literal(lines.get(i)), initCoordinate.getDescribeOtherLnInitX(), startY, 0x000000, false);
            }
            startY += font.lineHeight;
        }
    }

    private void showOccupation(GuiGraphics graphics){
        graphics.blit(initCoordinate.getOccupationImage(),  initCoordinate.getOccupationX(), initCoordinate.getOccupationY(), 0F, 0F,
                initCoordinate.getOccupationWidth(), initCoordinate.getOccupationHeight(), initCoordinate.getOccupationWidth(), initCoordinate.getOccupationHeight());
    }

    private void showAttribute(GuiGraphics graphics,int mouseX, int mouseY){
        int lineHeight = font.lineHeight;
        detailsHoverTooltip = null;
        detailsHoverTooltipX = 0;
        detailsHoverTooltipY = 0;
        int initNameTextX = initCoordinate.getInitAttributeX();
        int initAttributeLevelX = (int) (initNameTextX + (backgroundWidth * 0.18));
        Component details = Component.translatable("lhmiracleroad.gui.attribute.text.details");
        int textWidth = font.width(details);
        int initDetailsX = initCoordinate.getPageRightX() - (textWidth / 2);
        int initY = (int) (initCoordinate.getInitAttributeY() + (lineHeight * 1.75));
        int initLevel = 0;
        int attributeSize = 0;
        for (String key : ClientData.ATTRIBUTE_TYPES.keySet()) {
            JsonObject attributeObject = ClientData.ATTRIBUTE_TYPES.get(key);
            String nameText = ResourceLocationTool.ATTRIBUTE_NAME_PREFIX + LHMiracleRoadTool.isAsString(attributeObject.get("id"));
            int level = initCoordinate.getInitAttributeLevel().get(key);
            graphics.drawString(font, Component.translatable(nameText), initNameTextX, initY, 0x6C5734, false);
            graphics.drawString(font, String.valueOf(level), initAttributeLevelX, initY, 0x6C5734, false);
            graphics.drawString(font, details, initDetailsX, initY, 0xC58360, false);

            // 检查鼠标是否悬停在指定位置上
            if (mouseX >= initDetailsX && mouseX <= initDetailsX + textWidth && mouseY >= initY && mouseY <= initY + lineHeight) {
                detailsHoverTooltip = LHMiracleRoadTool.getDescribeText(LHMiracleRoadTool.isAsJsonArray(attributeObject.get("describes")),level,key);
                detailsHoverTooltipX = mouseX;
                detailsHoverTooltipY = mouseY;
            }

            initY += (int) (lineHeight * 1.75);
            initLevel += level;
            attributeSize ++;
        }

        initLevel = (initLevel - attributeSize * LHMiracleRoadConfig.COMMON.LEVEL_BASE.get()) + 1;
        Component initLevelcomponent = Component.translatable("lhmiracleroad.gui.attribute.text.init_level",initLevel);
        Component difficultyLevelcomponent = Component.translatable("lhmiracleroad.gui.attribute.text.difficulty_level",LHMiracleRoadTool.isAsInt(initCoordinate.getOccupation().get("init_difficulty_level")));
        String initLevelText = initLevelcomponent.getString();
        String difficultyLevelText = difficultyLevelcomponent.getString();
        String levelText = initLevelText+" "+difficultyLevelText;
        textWidth = font.width(levelText);
        graphics.drawString(font, levelText, initAttributeLevelX - (textWidth / 2), initCoordinate.getSelectY(), 0x6C5734, false);
    }

    private void showButton(boolean leftShow,boolean rightShow){

        if (leftShow) {
            //切换页数按钮
            ImageButton showPageLeftButton =
                    new ImageButton(initCoordinate.getPageLeftX(), initCoordinate.getPageY(), initCoordinate.getPageWidth(), initCoordinate.getPageHeight(), Component.empty(),
                            true, false, ResourceLocationTool.Gui.pageLeft, ResourceLocationTool.Gui.pageLeftButton, 0, 0, initCoordinate.getPageWidth(), initCoordinate.getPageHeight(),
                            initCoordinate.getPageWidth(), initCoordinate.getPageHeight());
            showPageLeftButton.setPressFunc(b -> lastPage());
            addRenderableWidget(showPageLeftButton);
        }

        if (rightShow) {
            ImageButton showPageRightButton =
                    new ImageButton(initCoordinate.getPageRightX(), initCoordinate.getPageY(), initCoordinate.getPageWidth(), initCoordinate.getPageHeight(), Component.empty(),
                            true, false, ResourceLocationTool.Gui.pageRight, ResourceLocationTool.Gui.pageRightButton, 0, 0, initCoordinate.getPageWidth(), initCoordinate.getPageHeight(),
                            initCoordinate.getPageWidth(), initCoordinate.getPageHeight());
            showPageRightButton.setPressFunc(b -> nextPage());
            addRenderableWidget(showPageRightButton);
        }
    }

    private void showLevelInformation(GuiGraphics graphics){
        graphics.drawString(font, initMainCoordinate.getLevelComponent(), initMainCoordinate.getLevelX(), initMainCoordinate.getLevelY(), 0x6C5734, false);
        graphics.drawString(font, initMainCoordinate.getPointsComponent(), initMainCoordinate.getPointsX(), initMainCoordinate.getLevelY(), 0x6C5734, false);
        graphics.drawString(font, initMainCoordinate.getHoldExperienceComponent(), initMainCoordinate.getLevelX(), initMainCoordinate.getHoldExperienceY(), 0x6C5734, false);
        graphics.drawString(font, initMainCoordinate.getDemandExperienceComponent(), initMainCoordinate.getLevelX(), initMainCoordinate.getDemandExperienceY(), 0x6C5734, false);

        graphics.drawString(font, String.valueOf(initMainCoordinate.getHoldExperienceValue()), initMainCoordinate.getPointsX(), initMainCoordinate.getHoldExperienceY(), 0x6C5734, false);
        graphics.drawString(font, String.valueOf(initMainCoordinate.getDemandExperienceValue()), initMainCoordinate.getPointsX(), initMainCoordinate.getDemandExperienceY(), 0x6C5734, false);


    }

    private void showAttributePoints(GuiGraphics graphics,int mouseX, int mouseY){
        int lineHeight = font.lineHeight;
        detailsHoverTooltip = null;
        detailsHoverTooltipX = 0;
        detailsHoverTooltipY = 0;
        int initNameTextX = initMainCoordinate.getLevelX();
        int initY = initMainCoordinate.getAttributePointsY();
        int initAttributeLevelX = initMainCoordinate.getAttributePointsLevelX();

        for (String key : ClientData.ATTRIBUTE_TYPES.keySet()) {
            JsonObject attributeObject = ClientData.ATTRIBUTE_TYPES.get(key);
            Integer level = playerOccupationAttribute.getOccupationAttributeLevel().get(key);
            String nameText = ResourceLocationTool.ATTRIBUTE_NAME_PREFIX + LHMiracleRoadTool.isAsString(attributeObject.get("id"));
            Component componentNameText = Component.translatable(nameText);

            graphics.drawString(font, componentNameText, initNameTextX, initY, 0x6C5734, false);
            graphics.drawString(font, String.valueOf(level), initAttributeLevelX, initY, 0x6C5734, false);

            int textWidth = font.width(componentNameText);
            // 检查鼠标是否悬停在指定位置上
            if (mouseX >= initNameTextX && mouseX <= initNameTextX + textWidth && mouseY >= initY && mouseY <= initY + lineHeight) {
                detailsHoverTooltip = LHMiracleRoadTool.getDescribeText(LHMiracleRoadTool.isAsJsonArray(attributeObject.get("describes")),level,key);
                detailsHoverTooltipX = mouseX;
                detailsHoverTooltipY = mouseY;
            }

            initY += (int) (lineHeight * 1.55);
        }
    }

    private void showDetailedAttribute(GuiGraphics graphics){
        int current = this.current - 1;
        if (current < 0) return;
        if (current > initMainCoordinate.getShowDetailedAttributePages().size() - 1) return;
        Map<String,List<JsonObject>> showDetailedAttributePage = initMainCoordinate.getShowDetailedAttributePages().get(this.current - 1);
        if (showDetailedAttributePage == null) return;
        for (String key : showDetailedAttributePage.keySet()) {

            int initX = initCoordinate.getInitAttributeX();
            //如果为左边的话改一下x的位置
            if (key.equals("left")) initX = initMainCoordinate.getLevelX();

            int initY = initCoordinate.getInitAttributeY();
            int lineHeight = font.lineHeight;

            for (JsonElement showGuiAttributeJsonElement : showDetailedAttributePage.get(key)) {
                JsonObject showGuiAttribute = LHMiracleRoadTool.isAsJsonObject(showGuiAttributeJsonElement);
                String attributeName = LHMiracleRoadTool.isAsString(showGuiAttribute.get("attribute"));
                JsonObject attributeObject = ClientData.SHOW_ATTRIBUTE.get(attributeName);
                if (attributeObject == null) continue;

                String attributeText = ResourceLocationTool.ATTRIBUTE_DETAILS_TEXT_PREFIX + LHMiracleRoadTool.isAsString(showGuiAttribute.get("attribute_text"));
                ShowAttributesTypes showValueType = ShowAttributesTypes.fromString(LHMiracleRoadTool.isAsString(showGuiAttribute.get("show_value_type")));
                double value = LHMiracleRoadTool.isAsDouble(attributeObject.get("v"));
                double baseValue = LHMiracleRoadTool.isAsDouble(attributeObject.get("b"));
                JsonArray modifiers = LHMiracleRoadTool.isAsJsonArray(attributeObject.get("m"));

                String showValue = "";
                int percentageBase = 100;
                if (attributeName.equals(AttributesNameTool.MOVEMENT_SPEED)) percentageBase = 1000;
                if (LHMiracleRoadTool.isLHMiracleRoadAttribute(attributeName)) {
                    showValue = LHMiracleRoadTool.getShowLHMiracleRoadValueType(modifiers, attributeName, percentageBase);
                } else {
                    showValue = LHMiracleRoadTool.getShowValueType(showValueType, value, baseValue, percentageBase, attributeName);
                }
                Component showText = Component.translatable(attributeText, showValue);
                graphics.drawString(font, showText, initX, initY, 0x6C5734, false);
                initY += (int) (lineHeight * 1.4);
            }
        }
    }

    private void showBurden(GuiGraphics graphics,int mouseX, int mouseY){
        int lineHeight = font.lineHeight;
        int initY = initCoordinate.getSelectY();
        int burdenValue = playerOccupationAttribute.getBurden();
        AttributeInstance heavy = getPlayer().getAttribute(LHMiracleRoadAttributes.HEAVY);
        int heavyValue = (int) heavy.getValue();
        heavyValue += (int) playerOccupationAttribute.getOffhandHeavy();
        Component component = Component.translatable("lhmiracleroad.gui.attribute.text.heavy",heavyValue,burdenValue);
        int textWidth = font.width(component);
        int initX = (int) (initCoordinate.getInitAttributeX() + (backgroundWidth * 0.18)) - (textWidth / 2);
        graphics.drawString(font, component, initX, initY, 0x6C5734, false);
        // 检查鼠标是否悬停在指定位置上
        if (mouseX >= initX && mouseX <= initX + textWidth && mouseY >= initY && mouseY <= initY + lineHeight) {
            List<Component> components = new ArrayList<>();
            double proportion = ((double) heavyValue / burdenValue) * 100;
            if (proportion > 100){
                components.add(Component.translatable("lhmiracleroad.gui.attribute.text.details.heavy.overweight"));
            }else if (proportion >= 75){
                components.add(Component.translatable("lhmiracleroad.gui.attribute.text.details.heavy.biased_weight"));
            }else if (proportion >= 40){
                components.add(Component.translatable("lhmiracleroad.gui.attribute.text.details.heavy.normal"));
            }else {
                components.add(Component.translatable("lhmiracleroad.gui.attribute.text.details.heavy.light"));
            }
            detailsHoverTooltip = components;
            detailsHoverTooltipX = mouseX;
            detailsHoverTooltipY = mouseY;
        }
    }

    private void showPointsButton(){
        if (current != 1) return;
        if (initMainCoordinate.getHoldExperienceValue() < initMainCoordinate.getDemandExperienceValue() && playerOccupationAttribute.getPoints() < 1) return;
        int initY = initMainCoordinate.getAttributePointsY();
        int initAttributeLevelX = initMainCoordinate.getAttributePointsButtonX();
        int lineHeight = font.lineHeight;
        for (String key : ClientData.ATTRIBUTE_TYPES.keySet()){
            JsonObject data = ClientData.ATTRIBUTE_POINTS_REWARDS.get(key);
            int maxLevel = LHMiracleRoadTool.isAsInt(data.get("max_level"));
            int currentLevel = playerOccupationAttribute.getOccupationAttributeLevel().get(key);
            int attributeMaxLevel = playerOccupationAttribute.getAttributeMaxLevel();
            if (LHMiracleRoadTool.isShowPointsButton(currentLevel,maxLevel,attributeMaxLevel)){
                ImageButton showPointsButton =
                        new ImageButton(initAttributeLevelX, initY, 12, 12, Component.empty(),
                                true, false, ResourceLocationTool.Gui.ADD, ResourceLocationTool.Gui.ADD_TOUCH, 0, 0, 12, 12,
                                12, 12);
                showPointsButton.setPressFunc(b -> pointsAttributeName(key));
                addRenderableWidget(showPointsButton);
            }
            initY += (int) (lineHeight * 1.55);
        }
        isShowPointsButton = false;
    }

    private void pointsAttributeName(String attributeTypeName){
        PlayerAttributeChannel.sendToServer(new PlayerAttributePointsMessage(attributeTypeName));
        PlayerOccupationAttribute occupationAttribute = getPlayer().getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve().get();
        occupationAttribute.setOccupationExperience(0);
        occupationAttribute.setPoints(0);
        isShowPointsButton = true;
        this.clearWidgets();
        if (current == 0) showButton(false,true);
        else showButton(true, current != totalPage - 1);
        syncAbility();
    }

    private @Nonnull Player getPlayer() {

        return Objects.requireNonNull(getMinecraft().player);
    }

    private void syncAbility(){
        Optional<PlayerOccupationAttribute> optionalPlayerOccupationAttribute = getPlayer().getCapability(PlayerOccupationAttributeProvider.PLAYER_OCCUPATION_ATTRIBUTE_PROVIDER).resolve();
        optionalPlayerOccupationAttribute.ifPresent(occupationAttribute -> this.playerOccupationAttribute = occupationAttribute);
        if (initMainCoordinate != null) initMainCoordinate.calculateAttribute(playerOccupationAttribute);
    }
}
