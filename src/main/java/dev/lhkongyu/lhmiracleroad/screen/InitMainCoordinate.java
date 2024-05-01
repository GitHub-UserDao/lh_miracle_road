package dev.lhkongyu.lhmiracleroad.screen;

import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class InitMainCoordinate {

    private int levelY;

    private int levelX;

    private MutableComponent levelComponent;

    private int attributeLevelX;

    private MutableComponent attributeLevelComponent;

    private int holdExperienceY;

    private int holdExperienceValue;

    private MutableComponent holdExperienceComponent;

    private int demandExperienceY;

    private int demandExperienceValue;

    private MutableComponent demandExperienceComponent;

    public int getLevelY() {
        return levelY;
    }

    public int getLevelX() {
        return levelX;
    }

    public MutableComponent getLevelComponent() {
        return levelComponent;
    }

    public int getAttributeLevelX() {
        return attributeLevelX;
    }

    public MutableComponent getAttributeLevelComponent() {
        return attributeLevelComponent;
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

    public  InitMainCoordinate(int widthCore, int heightCore, int backgroundWidth, int backgroundHeight, Font font, PlayerOccupationAttribute playerOccupationAttribute){
        int lineHeight = font.lineHeight;
        int lineWidth = font.width("测试");
        levelY = heightCore + lineHeight * 2;
        levelX = (int) (widthCore + lineWidth * 1.5);
        attributeLevelX = levelX + (backgroundWidth / 5);
        holdExperienceY = levelY + (lineHeight * 2);
        demandExperienceY = (int) (holdExperienceY + (lineHeight * 1.75));

        levelComponent = Component.translatable("lhmiracleroad.gui.attribute.text.level",playerOccupationAttribute.getOccupationLevel());
        attributeLevelComponent = Component.translatable("lhmiracleroad.gui.attribute.text.points", playerOccupationAttribute.getPoints());
        holdExperienceComponent = Component.translatable("lhmiracleroad.gui.attribute.text.hold_soul");
        demandExperienceComponent = Component.translatable("lhmiracleroad.gui.attribute.text.demand_soul");

        calculateAttribute(playerOccupationAttribute);
    }

    public void calculateAttribute(PlayerOccupationAttribute playerOccupationAttribute){
        holdExperienceValue = playerOccupationAttribute.getOccupationExperience();
        demandExperienceValue = LHMiracleRoadTool.evaluateFormula(playerOccupationAttribute.getEmpiricalCalculationFormula(),playerOccupationAttribute.getOccupationLevel());
    }

}
