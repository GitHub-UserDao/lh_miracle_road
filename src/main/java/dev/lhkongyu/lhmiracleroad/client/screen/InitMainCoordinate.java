package dev.lhkongyu.lhmiracleroad.client.screen;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import dev.lhkongyu.lhmiracleroad.data.reloader.AttributePointsRewardsReloadListener;
import dev.lhkongyu.lhmiracleroad.data.reloader.ShowGuiAttributeReloadListener;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InitMainCoordinate {

    private final int levelY;

    private final int levelX;

    private MutableComponent levelComponent;

    private final int pointsX;

    private MutableComponent pointsComponent;

    private final int holdExperienceY;

    private int holdExperienceValue;

    private final MutableComponent holdExperienceComponent;

    private final int demandExperienceY;

    private int demandExperienceValue;

    private final MutableComponent demandExperienceComponent;

    private final int attributePointsY;

    private final int attributePointsLevelX;

    private final int attributePointsButtonX;

//    private final Map<String,String> detailedAttribute = Maps.newHashMap();

    private List<Map<String,List<JsonObject>>> showDetailedAttributePages;

    public int getLevelY() {
        return levelY;
    }

    public int getLevelX() {
        return levelX;
    }

    public MutableComponent getLevelComponent() {
        return levelComponent;
    }

    public int getHoldExperienceY() {
        return holdExperienceY;
    }

    public MutableComponent getHoldExperienceComponent() {
        return holdExperienceComponent;
    }

    public int getDemandExperienceY() {
        return demandExperienceY;
    }

    public MutableComponent getDemandExperienceComponent() {
        return demandExperienceComponent;
    }

    public int getHoldExperienceValue() {
        return holdExperienceValue;
    }

    public int getDemandExperienceValue() {
        return demandExperienceValue;
    }

    public int getPointsX() {
        return pointsX;
    }

    public MutableComponent getPointsComponent() {
        return pointsComponent;
    }

    public int getAttributePointsY() {
        return attributePointsY;
    }

    public int getAttributePointsLevelX() {
        return attributePointsLevelX;
    }

    public int getAttributePointsButtonX() {
        return attributePointsButtonX;
    }

    public List<Map<String, List<JsonObject>>> getShowDetailedAttributePages() {
        return showDetailedAttributePages;
    }

    public  InitMainCoordinate(int widthCore, int heightCore, int backgroundWidth, int backgroundHeight, Font font, PlayerOccupationAttribute playerOccupationAttribute){
        int lineHeight = font.lineHeight;
        int lineWidth = font.width("测试");
        levelY = heightCore + lineHeight * 2;
        levelX = widthCore + lineWidth * 2;
        pointsX = levelX + (backgroundWidth / 5);
        holdExperienceY = levelY + (lineHeight * 2);
        demandExperienceY = (int) (holdExperienceY + (lineHeight * 1.75));

        holdExperienceComponent = Component.translatable("lhmiracleroad.gui.attribute.text.hold_soul");
        demandExperienceComponent = Component.translatable("lhmiracleroad.gui.attribute.text.demand_soul");
        attributePointsY = demandExperienceY + (lineHeight * 2);

        attributePointsLevelX = (int) (levelX + backgroundWidth * 0.325);
        attributePointsButtonX = (int) (levelX + backgroundWidth * 0.18);

        calculateAttribute(playerOccupationAttribute);
    }

    public void calculateAttribute(PlayerOccupationAttribute playerOccupationAttribute){
        holdExperienceValue = playerOccupationAttribute.getOccupationExperience();
        demandExperienceValue = LHMiracleRoadTool.evaluateFormula(playerOccupationAttribute.getEmpiricalCalculationFormula(),playerOccupationAttribute.getOccupationLevel());
        pointsComponent = Component.translatable("lhmiracleroad.gui.attribute.text.points", playerOccupationAttribute.getPoints());
        int occupationLevel = playerOccupationAttribute.getOccupationLevel();
        for (String key : playerOccupationAttribute.getCurioAttributeLevel().keySet()) {
            occupationLevel += playerOccupationAttribute.getCurioAttributeLevelValue(key);
        }
        levelComponent = Component.translatable("lhmiracleroad.gui.attribute.text.level",occupationLevel);
    }

    public int setShowDetailedAttributePage() {

        showDetailedAttributePages = new ArrayList<>();

        List<JsonObject> attributes = ClientData.SHOW_GUI_ATTRIBUTE;

        final int firstPageSize = 15;
        final int pageSize = 30;
        final int columnSize = 15;

        if (attributes.isEmpty()) {
            return 0;
        }

        // 第一页（右边15条）
        Map<String, List<JsonObject>> firstPage = new LinkedHashMap<>();

        int firstEnd = Math.min(firstPageSize, attributes.size());

        firstPage.put("right", new ArrayList<>(attributes.subList(0, firstEnd)));

        showDetailedAttributePages.add(firstPage);

        // 后续页面
        for (int start = firstEnd; start < attributes.size(); start += pageSize) {

            int end = Math.min(start + pageSize, attributes.size());

            List<JsonObject> pageData = attributes.subList(start, end);

            Map<String, List<JsonObject>> page = new LinkedHashMap<>();

            int split = Math.min(columnSize, pageData.size());

            // 左边
            page.put("left", new ArrayList<>(pageData.subList(0, split)));

            // 右边
            if (pageData.size() > columnSize)
                page.put("right", new ArrayList<>(pageData.subList(split, pageData.size())));


            showDetailedAttributePages.add(page);
        }

        return showDetailedAttributePages.size() - 1;
    }

}
