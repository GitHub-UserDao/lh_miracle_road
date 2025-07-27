package dev.lhkongyu.lhmiracleroad.screen;

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

    public int setShowDetailedAttributePage(){
        showDetailedAttributePages = new ArrayList<>();
        List<JsonObject> showGuiAttributeList = ClientData.SHOW_GUI_ATTRIBUTE;

        //设置每一面能放多少条数据
        int eachPageMaxSize = 15;

        if (showGuiAttributeList.size() > eachPageMaxSize){

            //将第一页右边显示的属性文本进行默认填充
            Map<String,List<JsonObject>> showGuiAttribute = Maps.newLinkedHashMap();
            List<JsonObject> attributeInitPartList = new ArrayList<>();
            for (int i = 0; i < eachPageMaxSize; i++) {
                attributeInitPartList.add(showGuiAttributeList.get(i));
            }
            showGuiAttribute.put("right",attributeInitPartList);
            showDetailedAttributePages.add(showGuiAttribute);

            //设置每页能放多少条数据
            int subsequentPageMaxSize = 30;
            int page = Math.max(1,(showGuiAttributeList.size() - eachPageMaxSize) / subsequentPageMaxSize);

            for (int i = 0; i < page; i++) {
                int size = (subsequentPageMaxSize * (i + 1)) + eachPageMaxSize;
                if (size > (showGuiAttributeList.size() - eachPageMaxSize)){
                    size = showGuiAttributeList.size();
                }

                int frequency = 0;
                Map<String,List<JsonObject>> showGuiAttributeTail = Maps.newLinkedHashMap();
                List<JsonObject> attributeTailList = new ArrayList<>();
                for (int j = eachPageMaxSize + (subsequentPageMaxSize * i); j < size; j++) {
                    frequency++;
                    attributeTailList.add(showGuiAttributeList.get(j));
                    //当前页前15条设置是左边的
                    if (frequency == eachPageMaxSize){
                        showGuiAttributeTail.put("left",new ArrayList<>(attributeTailList));
                        attributeTailList.clear();
                    }
                }
                //如果循环次数大于15就代表该页面有右边的数据
                if (frequency > eachPageMaxSize){
                    showGuiAttributeTail.put("right",attributeTailList);
                }else {
                    //因为有可能小于15条那就填充左边的数据
                    showGuiAttributeTail.putIfAbsent("left", attributeTailList);
                }
                showDetailedAttributePages.add(showGuiAttributeTail);
            }
        }else {
            //将第一页右边显示的属性文本进行默认填充
            Map<String,List<JsonObject>> showGuiAttributeObject = Maps.newLinkedHashMap();
            showGuiAttributeObject.put("right",showGuiAttributeList);
            showDetailedAttributePages.add(showGuiAttributeObject);
        }
        return showDetailedAttributePages.size() - 1;
    }

}
