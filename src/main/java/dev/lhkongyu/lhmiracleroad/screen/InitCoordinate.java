package dev.lhkongyu.lhmiracleroad.screen;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.data.reloader.OccupationReloadListener;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InitCoordinate {

    private final int pageY;

    private final int pageLeftX;

    private final int pageRightX;

    private final int pageWidth;

    private final int pageHeight;

    private final int frameX;

    private final int frameY;

    private final int frameWidth;

    private final int frameHeight;

    private int selectX;

    private int selectY;

    private int selectWidth;

    private int selectHeight;

    private MutableComponent selectComponent;

    private final int occupationX;

    private final int occupationY;

    private final int occupationWidth;

    private final int occupationHeight;

    private ResourceLocation occupationImage;

    private int initItemX;

    private int initItemY;

    private MutableComponent initItemComponent;

    private final int initAttributeX;

    private final int initAttributeY;

    private final MutableComponent initAttributeComponent;

    private final int describeOneLnInitX;

    private final int describeOtherLnInitX;

    private final int describeInitY;

    private List<String> describeTexts;

    private MutableComponent occupationNameComponent;

    private int occupationNameX;

    private int occupationNameY;

    private JsonObject occupation;

    private Map<String,Integer> initAttributeLevel;

    private List<JsonObject> initItem;

    private final Map<String,Map<String,String>> attributePromoteValueShow = Maps.newHashMap();

    public int getPageY() {
        return pageY;
    }

    public int getPageLeftX() {
        return pageLeftX;
    }

    public int getPageRightX() {
        return pageRightX;
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public int getFrameX() {
        return frameX;
    }

    public int getFrameY() {
        return frameY;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public int getSelectX() {
        return selectX;
    }

    public int getSelectY() {
        return selectY;
    }

    public int getSelectWidth() {
        return selectWidth;
    }

    public int getSelectHeight() {
        return selectHeight;
    }

    public int getOccupationX() {
        return occupationX;
    }

    public int getOccupationY() {
        return occupationY;
    }

    public int getOccupationWidth() {
        return occupationWidth;
    }

    public int getOccupationHeight() {
        return occupationHeight;
    }

    public int getInitItemX() {
        return initItemX;
    }

    public int getInitItemY() {
        return initItemY;
    }

    public int getInitAttributeX() {
        return initAttributeX;
    }

    public int getInitAttributeY() {
        return initAttributeY;
    }

    public int getDescribeOneLnInitX() {
        return describeOneLnInitX;
    }

    public int getDescribeOtherLnInitX() {
        return describeOtherLnInitX;
    }

    public int getDescribeInitY() {
        return describeInitY;
    }

    public List<String> getDescribeTexts() {
        return describeTexts;
    }

    public MutableComponent getSelectComponent() {
        return selectComponent;
    }

    public MutableComponent getInitItemComponent() {
        return initItemComponent;
    }

    public MutableComponent getInitAttributeComponent() {
        return initAttributeComponent;
    }

    public MutableComponent getOccupationNameComponent() {
        return occupationNameComponent;
    }

    public int getOccupationNameX() {
        return occupationNameX;
    }

    public int getOccupationNameY() {
        return occupationNameY;
    }

    public ResourceLocation getOccupationImage() {
        return occupationImage;
    }

    public JsonObject getOccupation() {
        return occupation;
    }

    public Map<String, Integer> getInitAttributeLevel() {
        return initAttributeLevel;
    }

    public List<JsonObject> getInitItem() {
        return initItem;
    }

    public Map<String, Map<String,String>> getAttributePromoteValueShow() {
        return attributePromoteValueShow;
    }

    public InitCoordinate(int widthCore, int heightCore,int backgroundWidth,int backgroundHeight,Font font,int current){
        int lineHeight = font.lineHeight;
        int lineWidth = font.width("测试");
        //计算切换页数的位置
        pageWidth = 35;
        pageHeight = 33;
        pageY = heightCore + backgroundHeight - 45;
        pageLeftX = widthCore + 12;
        pageRightX = widthCore + backgroundWidth - 46;

        //计算职业相框的位置
        frameWidth = backgroundWidth / 3;
        frameHeight = (int) (backgroundHeight * 0.5 );
        frameX = widthCore + (frameWidth / 3);
        frameY = heightCore;
        //计算选择框的位置以及文本
        selectWidth = (int) (backgroundWidth * 0.1);
        selectHeight = (int) (backgroundHeight * 0.055 );
        selectX = widthCore + (backgroundWidth / 6 + selectWidth / 2);
        selectY = pageY + (selectHeight / 2);
        selectComponent = Component.translatable("lhmiracleroad.gui.text.select");
        //计算职业图片的位置
        occupationWidth = (int) (backgroundWidth * 0.225);
        occupationHeight = (int) (backgroundHeight * 0.25 );
        occupationX = widthCore + (backgroundWidth / 6);
        occupationY = heightCore + (frameHeight / 5);
        // 计算初始物品的位置以及文本
        initItemX = (int) (widthCore + lineWidth * 1.5);
        initItemY = heightCore + (backgroundHeight / 2) + lineHeight * 3;
        initItemComponent = Component.translatable("lhmiracleroad.gui.titles.init_item");
        // 计算初始属性的位置以及文本
        initAttributeX = widthCore + (backgroundWidth / 2) + lineWidth;
        initAttributeY = heightCore + lineHeight * 2;
        initAttributeComponent = Component.translatable("lhmiracleroad.gui.titles.init_attribute");
        //设置描述文本的位置
        describeOneLnInitX = (int) (widthCore + lineWidth * 2.5);
        describeOtherLnInitX = (int) (widthCore + lineWidth * 1.5);
        describeInitY = heightCore + frameHeight - lineHeight * 2;

        //设置职业基本数据
        setOccupation(widthCore,heightCore,backgroundWidth,backgroundHeight,font,current);
    }

    public InitCoordinate(int widthCore, int heightCore,int backgroundWidth,int backgroundHeight,Font font,String occupationId){
        int lineHeight = font.lineHeight;
        int lineWidth = font.width("测试");
        //计算切换页数的位置
        pageWidth = 35;
        pageHeight = 33;
        pageY = heightCore + backgroundHeight - 45;
        pageLeftX = widthCore + 12;
        pageRightX = widthCore + backgroundWidth - 46;

        //计算职业相框的位置
        frameWidth = backgroundWidth / 3;
        frameHeight = (int) (backgroundHeight * 0.5 );
        frameX = widthCore + (frameWidth / 3);
        frameY = heightCore;
        //计算职业图片的位置
        occupationWidth = (int) (backgroundWidth * 0.225);
        occupationHeight = (int) (backgroundHeight * 0.25 );
        occupationX = widthCore + (backgroundWidth / 6);
        occupationY = heightCore + (frameHeight / 5);
        // 计算初始属性的位置以及文本
        initAttributeX = widthCore + (backgroundWidth / 2) + lineWidth;
        initAttributeY = heightCore + lineHeight * 2;
        initAttributeComponent = Component.translatable("lhmiracleroad.gui.titles.init_attribute");
        //设置描述文本的位置
        describeOneLnInitX = (int) (widthCore + lineWidth * 2.5);
        describeOtherLnInitX = (int) (widthCore + lineWidth * 1.5);
        describeInitY = heightCore + frameHeight - lineHeight * 2;
        //选择框y
        selectY = pageY + (selectHeight / 2);
        //设置职业基本数据
        setOccupation(widthCore,heightCore,backgroundWidth,backgroundHeight,font,occupationId);
    }

    private List<String> setDescribeTexts(int backgroundWidth,Font font){
        int baseMaxWidth = (int) (backgroundWidth  * 0.375);
        MutableComponent mutableComponent = Component.translatable(LHMiracleRoadTool.isAsString(occupation.get("describe_text")));
        return LHMiracleRoadTool.baseTextWidthSplitText(font,mutableComponent,baseMaxWidth,describeOneLnInitX,describeOtherLnInitX);
    }

    public void setOccupation(int widthCore, int heightCore,int backgroundWidth,int backgroundHeight, Font font, int current){
        int lineHeight = font.lineHeight;
        //获取职业基本数据
        occupation = OccupationReloadListener.OCCUPATION.get(current);

        //设置职业图片
        occupationImage = new ResourceLocation(LHMiracleRoad.MODID, LHMiracleRoadTool.isAsString(occupation.get("occupation_avatar")));
        //设置职业名称
        occupationNameComponent = Component.translatable(LHMiracleRoadTool.isAsString(occupation.get("name")));
        //设置职业名称的位置
        int textWidth = font.width(occupationNameComponent);
        occupationNameX = widthCore + (backgroundWidth / 6 + frameWidth / 3) - (textWidth / 2);
        occupationNameY = (int) (heightCore + frameHeight - lineHeight * 3.75);
        //填充初始属性等级数据
        initAttributeLevel = LHMiracleRoadTool.setInitAttributeLevel(occupation);
        //设置描述文本
        describeTexts = setDescribeTexts(backgroundWidth,font);
        //填充初始物品信息
        initItem = LHMiracleRoadTool.setInitItem(occupation);
    }

    public void setOccupation(int widthCore, int heightCore,int backgroundWidth,int backgroundHeight, Font font,String occupationId){
        int lineHeight = font.lineHeight;
        //获取职业基本数据
        occupation = LHMiracleRoadTool.getOccupation(occupationId);
        //设置职业图片
        occupationImage = new ResourceLocation(LHMiracleRoad.MODID, LHMiracleRoadTool.isAsString(occupation.get("occupation_avatar")));
        //设置职业名称
        occupationNameComponent = Component.translatable(LHMiracleRoadTool.isAsString(occupation.get("name")));
        //设置职业名称的位置
        int textWidth = font.width(occupationNameComponent);
        occupationNameX = widthCore + (backgroundWidth / 6 + frameWidth / 3) - (textWidth / 2);
        occupationNameY = (int) (heightCore + frameHeight - lineHeight * 3.75);
        //填充初始属性等级数据
        initAttributeLevel = LHMiracleRoadTool.setInitAttributeLevel(occupation);
        //设置描述文本
        describeTexts = setDescribeTexts(backgroundWidth,font);
    }
}
