package dev.lhkongyu.lhmiracleroad.screen;

import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.data.reloader.AttributeReloadListener;
import dev.lhkongyu.lhmiracleroad.data.reloader.OccupationReloadListener;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributeChannel;
import dev.lhkongyu.lhmiracleroad.packet.PlayerOccupationMessage;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import dev.lhkongyu.lhmiracleroad.tool.ResourceLocationTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class LHMiracleRoadMainScreen extends Screen {

    private InitCoordinate initCoordinate;

    private InitMainCoordinate initMainCoordinate;

    private int currentGuiScale;

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

    private final PlayerOccupationAttribute playerOccupationAttribute;

    private int totalPage = 0;


    public LHMiracleRoadMainScreen(PlayerOccupationAttribute playerOccupationAttribute, int totalPage, int current) {
        super(Component.empty());
        super.minecraft = Minecraft.getInstance();
        this.totalPage = totalPage;
        this.current = current;
        this.playerOccupationAttribute = playerOccupationAttribute;
    }

    @Override
    public void init() {
        if (minecraft != null) {
            widthCore = (super.width - backgroundWidth) / 2;
            heightCore = (super.height - backgroundHeight) / 2;

            currentGuiScale = minecraft.options.guiScale().get();
            initCoordinate = new InitCoordinate(widthCore,heightCore,backgroundWidth,backgroundHeight,font,playerOccupationAttribute.getOccupationId());
            initMainCoordinate = new InitMainCoordinate(widthCore,heightCore,backgroundWidth,backgroundWidth,font,playerOccupationAttribute);
            if (current == 0) showButton(false,true);
            else showButton(true, current != totalPage - 1);
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

            if (detailsHoverTooltip != null) {
                graphics.renderComponentTooltip(font, detailsHoverTooltip, detailsHoverTooltipX, detailsHoverTooltipY);
            }
        }else if (current == 1){
            showLevelInformation(graphics);
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
        int textHeight = font.lineHeight;
        int initDetailsX = initCoordinate.getPageRightX() - (textWidth / 2);
        int initY = (int) (initCoordinate.getInitAttributeY() + (lineHeight * 1.75));
        int initLevel = 0;
        int attributeSize = 0;
        for (JsonObject attributeObject : AttributeReloadListener.ATTRIBUTE_TYPES) {
            //判断是否有前置mod的要求
            if(LHMiracleRoadTool.isJsonArrayModIdsExist(LHMiracleRoadTool.isAsJsonArray(attributeObject.get("conditions")))) continue;
            String id = LHMiracleRoadTool.isAsString(attributeObject.get("id"));
            String nameText = LHMiracleRoadTool.isAsString(attributeObject.get("name_text_id"));
            int level = initCoordinate.getInitAttributeLevel().get(id);
            graphics.drawString(font, Component.translatable(nameText), initNameTextX, initY, 0x6C5734, false);
            graphics.drawString(font, String.valueOf(level), initAttributeLevelX, initY, 0x6C5734, false);
            graphics.drawString(font, details, initDetailsX, initY, 0xC58360, false);

            // 检查鼠标是否悬停在物品上
            if (mouseX >= initDetailsX && mouseX <= initDetailsX + textWidth && mouseY >= initY && mouseY <= initY + textHeight) {
                List<Component> components = LHMiracleRoadTool.getDescribeText(LHMiracleRoadTool.isAsJsonArray(attributeObject.get("describes")),level,id,initCoordinate.getAttributePromoteValueShow());
                detailsHoverTooltip =  components;
                detailsHoverTooltipX = mouseX;
                detailsHoverTooltipY = mouseY;
            }

            initY += (int) (lineHeight * 1.5);
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
                            initCoordinate.getPageWidth(), initCoordinate.getPageHeight(), currentGuiScale);
            showPageLeftButton.setPressFunc(b -> lastPage());
            addRenderableWidget(showPageLeftButton);
        }

        if (rightShow) {
            ImageButton showPageRightButton =
                    new ImageButton(initCoordinate.getPageRightX(), initCoordinate.getPageY(), initCoordinate.getPageWidth(), initCoordinate.getPageHeight(), Component.empty(),
                            true, false, ResourceLocationTool.Gui.pageRight, ResourceLocationTool.Gui.pageRightButton, 0, 0, initCoordinate.getPageWidth(), initCoordinate.getPageHeight(),
                            initCoordinate.getPageWidth(), initCoordinate.getPageHeight(), currentGuiScale);
            showPageRightButton.setPressFunc(b -> nextPage());
            addRenderableWidget(showPageRightButton);
        }
    }

    private void showLevelInformation(GuiGraphics graphics){
        graphics.drawString(font, initMainCoordinate.getLevelComponent(), initMainCoordinate.getLevelX(), initMainCoordinate.getLevelY(), 0x6C5734, false);
        graphics.drawString(font, initMainCoordinate.getAttributeLevelComponent(), initMainCoordinate.getAttributeLevelX(), initMainCoordinate.getLevelY(), 0x6C5734, false);
        graphics.drawString(font, initMainCoordinate.getHoldExperienceComponent(), initMainCoordinate.getLevelX(), initMainCoordinate.getHoldExperienceY(), 0x6C5734, false);
        graphics.drawString(font, initMainCoordinate.getDemandExperienceComponent(), initMainCoordinate.getLevelX(), initMainCoordinate.getDemandExperienceY(), 0x6C5734, false);
    }

    private @Nonnull LocalPlayer getPlayer() {
        return Objects.requireNonNull(getMinecraft().player);
    }
}
