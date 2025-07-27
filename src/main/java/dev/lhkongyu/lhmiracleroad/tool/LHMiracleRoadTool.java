package dev.lhkongyu.lhmiracleroad.tool;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.attributes.ShowAttributesTypes;
import dev.lhkongyu.lhmiracleroad.capability.ItemStackPunishmentAttribute;
import dev.lhkongyu.lhmiracleroad.capability.ItemStackPunishmentAttributeProvider;
import dev.lhkongyu.lhmiracleroad.capability.PlayerOccupationAttribute;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.data.ClientData;
import dev.lhkongyu.lhmiracleroad.data.reloader.*;
import dev.lhkongyu.lhmiracleroad.packet.ClientDataMessage;
import dev.lhkongyu.lhmiracleroad.packet.ClientOccupationMessage;
import dev.lhkongyu.lhmiracleroad.packet.PlayerAttributeChannel;
import dev.lhkongyu.lhmiracleroad.particle.SoulParticleOption;
import dev.lhkongyu.lhmiracleroad.tool.mathcalculator.MathCalculatorUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.joml.Vector3f;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class LHMiracleRoadTool {

    public static RandomSource randomSource = RandomSource.create();

    /**
     * 转入路径转换为 带 modid 的 ResourceLocation对象
     * @param path
     * @return
     */
    public static ResourceLocation resourceLocationId(String path){

        return new ResourceLocation(LHMiracleRoad.MODID,path);
    }

    /**
     * rgb 转换 为 Vector3f
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public static Vector3f RGBChangeVector3f(int red, int green, int blue){
        // 将通道值归一化到范围[0, 1]
        float normalizedRed = (float) red / 255.0f;
        float normalizedGreen = (float) green / 255.0f;
        float normalizedBlue = (float) blue / 255.0f;

        return new Vector3f(normalizedRed, normalizedGreen, normalizedBlue);
    }

    /**
     * 将秒 转换为 tick
     * @param duration
     * @return
     */
    public static int getDuration(int duration){
        return duration * 20;
    }

    /**
     * 根据文本的宽度来进行拆分文本
     *
     * @param font             mc文本字体
     * @param mutableComponent 渲染的文本
     * @param baseMaxWidth     文字每行的最大宽度基础数值
     * @param oneLnInitX       文字第一行开头的x轴位置
     * @param otherLnInitX     文字除第一行开头的x轴位置
     * @return
     */
    public static List<String> baseTextWidthSplitText(Font font, MutableComponent mutableComponent, int baseMaxWidth, int oneLnInitX, int otherLnInitX) {
        String text = mutableComponent.getString();
        List<String> lines = new ArrayList<>();
        int maxWidth = baseMaxWidth; // 最大宽度
        int currentWidth = 0;
        StringBuilder currentLine = new StringBuilder();

        for (char c : text.toCharArray()) {
            currentWidth += font.width(Character.toString(c));
            if (currentWidth > maxWidth) {
                maxWidth = baseMaxWidth + (oneLnInitX - otherLnInitX);
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                currentWidth = font.width(Character.toString(c));
            }
            currentLine.append(c);
        }
        lines.add(currentLine.toString());
        return lines;
    }

    /**
     * 获取描述文本
     *
     * @param jsonArray
     * @param level
     * @param id
     * @return
     */
    public static List<Component> getDescribeText(JsonArray jsonArray, int level, String id) {
        List<Component> components = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            JsonObject object = jsonElement.getAsJsonObject();
            String modId = isAsString(object.get("condition"));
            String describe = ResourceLocationTool.ATTRIBUTE_DETAILS_TEXT_PREFIX + isAsString(object.get("describe"));
            String attribute = isAsString(object.get("attribute"));
            int percentageBase = 100;
            if (modId != null && !modId.isEmpty()) {
                if (isModExist(modId))
                    components.add(Component.translatable(describe, getDescribeTextValue(level, id, attribute,percentageBase)));
            } else
                components.add(Component.translatable(describe, getDescribeTextValue(level, id, attribute,percentageBase)));
        }
        return components;
    }

    /**
     * 计算出描述文本后显示的值
     *
     * @param level
     * @param id
     * @param attribute
     * @return
     */
    private static String getDescribeTextValue(int level, String id, String attribute,int percentageBase) {
        String showValue = null;
//        Map<String, String> valueMap = attributePromoteValueShow.get(id);
//        if (valueMap != null) {
//            showValue = valueMap.get(attribute);
//            if (showValue != null) return showValue;
//        }
        JsonObject data = ClientData.ATTRIBUTE_POINTS_REWARDS.get(id);
        if (data == null) return "";
        JsonArray pointsRewards = isAsJsonArray(data.get("points_rewards"));
        if (pointsRewards == null || pointsRewards.isEmpty()) return "";
        for (JsonElement jsonElement : pointsRewards) {
            JsonObject jsonObject = isAsJsonObject(jsonElement);
            String rewardsAttribute = isAsString(jsonObject.get("attribute"));
            if (rewardsAttribute == null) return "";
            if (attribute.equals(rewardsAttribute)) {
                double value = isAsDouble(jsonObject.get("value"));
                int levelPromote = isAsInt(jsonObject.get("level_promote"));
                double levelPromoteValue = isAsDouble(jsonObject.get("level_promote_value"));
                AttributeModifier.Operation operation = stringConversionOperation(isAsString(jsonObject.get("operation")));
                if (operation == null) return "";
                double min = LHMiracleRoadTool.isAsDouble(jsonObject.get("min"));
                double attributeValue = LHMiracleRoadTool.calculateTotalIncrease(level, value, levelPromoteValue, levelPromote,min);
                showValue = switch (operation) {
                    case ADDITION -> "+ " + attributeValue;
                    case MULTIPLY_BASE, MULTIPLY_TOTAL -> "+ " + new BigDecimal(attributeValue * percentageBase).setScale(4, RoundingMode.HALF_UP).doubleValue() + "%";
                };
//                Map<String, String> map = Maps.newHashMap();
//                map.put(attribute, showValue);
//                attributePromoteValueShow.put(id, map);
                return showValue;
            }

        }
        return "";
    }

    public static boolean isJsonArrayModIdsExist(JsonArray jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty()) return false;
        for (int i = 0; i < jsonArray.size(); i++) {
            if (isModExist(jsonArray.get(i).getAsString())) return false;
        }
        return true;
    }

    /**
     * 获取该mod是否存在
     *
     * @param modId
     * @return
     */
    public static boolean isModExist(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static String isAsString(JsonElement jsonElement) {
        return jsonElement != null ? jsonElement.getAsString() : null;
    }

    public static JsonArray isAsJsonArray(JsonElement jsonElement) {
        return jsonElement != null ? jsonElement.getAsJsonArray() : new JsonArray();
    }

    public static JsonObject isAsJsonObject(JsonElement jsonElement) {
        return jsonElement != null ? jsonElement.getAsJsonObject() : null;
    }

    public static int isAsInt(JsonElement jsonElement) {
        return jsonElement != null ? jsonElement.getAsInt() : 0;
    }

    public static double isAsDouble(JsonElement jsonElement) {
        return jsonElement != null ? jsonElement.getAsDouble() : 0.0;
    }

    public static Boolean isAsBoolean(JsonElement jsonElement) {
        return jsonElement != null && jsonElement.getAsBoolean();
    }

    /**
     * 计算出该属性当前等级所提升的值
     *
     * @param level
     * @param value
     * @param levelPromoteValue
     * @param levelPromote
     * @return
     */
    public static double calculateTotalIncrease(int level, double value, double levelPromoteValue, int levelPromote,double min) {
        level = level - LHMiracleRoadConfig.COMMON.LEVEL_BASE.get();
        if (level == 0) return 0.0;
        double returnValue = 0.0;
        BigDecimal dfReturnValue = null;
        if (levelPromote > 0) {
            // 根据当前等级计算提升次数 也就是组
            int twenties = level / levelPromote;

            if (levelPromoteValue < 0) {
                levelPromoteValue = levelPromoteValue > value ? value - min : levelPromoteValue;
            }
            double increase = 0;
            if (twenties > 0) {
                double levelIncrease = value * twenties * levelPromote;
//                increase = levelIncrease + ((double) (twenties * (twenties - 1)) / 2) * levelPromote * levelPromoteValue;
                double decreasingValue = 0;
                for (int i = 0;i < twenties;i++){
                    double thisRoundPromoteValue = levelPromoteValue * i;
                    decreasingValue += levelPromote * (value + thisRoundPromoteValue < min ? (value - min) * -1 : thisRoundPromoteValue);
                }
                increase = levelIncrease + decreasingValue;
            }
            // 用于计算 当前等级不满足一组的部分
            int remainingLevels = level % levelPromote;
            double especiallyDecreasing = levelPromoteValue * twenties + value;
            double increaseTheRest = Math.max(especiallyDecreasing,min) * remainingLevels;

            returnValue = increase + increaseTheRest;
            dfReturnValue = new BigDecimal(returnValue);
            return dfReturnValue.setScale(4, RoundingMode.HALF_UP).doubleValue();
        }
        returnValue = value * level;
        dfReturnValue = new BigDecimal(returnValue);
        return dfReturnValue.setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 通过职业信息获取，职业每个属性的初始等级
     *
     * @param occupation
     * @return
     */
    public static Map<String, Integer> setInitAttributeLevel(JsonObject occupation) {
        Map<String, Integer> map = Maps.newHashMap();
        JsonArray initAttribute = LHMiracleRoadTool.isAsJsonArray(occupation.get("init_attribute"));
        for (JsonElement jsonElement : initAttribute) {
            JsonObject object = LHMiracleRoadTool.isAsJsonObject(jsonElement);
            if (object == null) continue;
            String id = object.get("id").getAsString();
            JsonArray attributeObject = AttributeReloadListener.ATTRIBUTE_TYPES.get(id);
            if (attributeObject == null || attributeObject.isEmpty()) continue;
            int level = object.get("level").getAsInt();
            map.put(id, level);
        }

        return map;
    }

    /**
     * 通过职业信息获取，职业每个属性的初始等级(客户端)
     * @param occupation
     * @return
     */
    public static Map<String, Integer> setInitAttributeLevelClient(JsonObject occupation) {
        Map<String, Integer> map = Maps.newHashMap();
        JsonArray initAttribute = LHMiracleRoadTool.isAsJsonArray(occupation.get("init_attribute"));
        for (JsonElement jsonElement : initAttribute) {
            JsonObject object = LHMiracleRoadTool.isAsJsonObject(jsonElement);
            if (object == null) continue;
            String id = object.get("id").getAsString();
            JsonArray attributeObject = ClientData.ATTRIBUTE_TYPES.get(id);
            if (attributeObject == null || attributeObject.isEmpty()) continue;
            int level = object.get("level").getAsInt();
            map.put(id, level);
        }

        return map;
    }

    /**
     * 通过职业信息获取该职业初始物品信息
     *
     * @param occupation
     * @return
     */
    public static List<JsonObject> setInitItem(JsonObject occupation) {
        List<JsonObject> objects = new ArrayList<>();
        JsonArray initItem = InitItemResourceReloadListener.INIT_ITEM.get(isAsString(occupation.get("id")));
        if (initItem == null || initItem.isEmpty()) return objects;
        for (JsonElement jsonElement : initItem) {
            JsonObject object = LHMiracleRoadTool.isAsJsonObject(jsonElement);
            if (object == null) continue;
            objects.add(object);
        }
        return objects;
    }

    /**
     * 通过职业信息获取该职业客户端的初始物品信息
     *
     * @param occupation
     * @return
     */
    public static List<JsonObject> setInitItemClient(JsonObject occupation) {
        List<JsonObject> objects = new ArrayList<>();
        JsonArray initItem = ClientData.INIT_ITEM.get(isAsString(occupation.get("id")));
        if (initItem == null || initItem.isEmpty()) return objects;
        for (JsonElement jsonElement : initItem) {
            JsonObject object = LHMiracleRoadTool.isAsJsonObject(jsonElement);
            if (object == null) continue;
            objects.add(object);
        }
        return objects;
    }

    /**
     * 设置始物品标签
     *
     * @param itemStack
     * @param tagObj
     */
    public static void setTag(ItemStack itemStack, String tagObj) {
        if (tagObj != null) {
            CompoundTag modTag;
            try {
                modTag = NbtUtils.snbtToStructure(tagObj);
                modTag.remove("palette");
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
            itemStack.setTag(modTag);
        }
    }

    /**
     * 将string转换成 根据基础属性增加值(addition)还是 根据基础属性百分比提升（multiply_base）或是 根据总属性按百分比提升(multiply_total)
     *
     * @param operationString
     * @return
     */
    public static AttributeModifier.Operation stringConversionOperation(String operationString) {
        if (operationString == null) return null;
        return switch (operationString) {
            case "addition" -> AttributeModifier.Operation.ADDITION;
            case "multiply_base" -> AttributeModifier.Operation.MULTIPLY_BASE;
            case "multiply_total" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
            default -> null;
        };
    }

    /**
     * 将属性名称转换成属性对象
     *
     * @param attributeName
     * @return
     */
    public static Attribute stringConversionAttribute(String attributeName) {
        if (attributeName == null) return null;
        int index = attributeName.indexOf('#');
        if (index != -1) {
            attributeName = attributeName.substring(0, index).trim();
        }

        return switch (attributeName) {
            case AttributesNameTool.MAX_HEALTH -> Attributes.MAX_HEALTH;
            case AttributesNameTool.ATTACK_DAMAGE -> Attributes.ATTACK_DAMAGE;
            case AttributesNameTool.RANGED_DAMAGE -> LHMiracleRoadAttributes.RANGED_DAMAGE;
            case AttributesNameTool.BURDEN -> LHMiracleRoadAttributes.BURDEN;
            case AttributesNameTool.HEAVY -> LHMiracleRoadAttributes.HEAVY;
            case AttributesNameTool.ARMOR -> Attributes.ARMOR;
            case AttributesNameTool.ARMOR_TOUGHNESS -> Attributes.ARMOR_TOUGHNESS;
            case AttributesNameTool.ATTACK_SPEED -> Attributes.ATTACK_SPEED;
            case AttributesNameTool.MOVEMENT_SPEED -> Attributes.MOVEMENT_SPEED;
            case AttributesNameTool.LUCK -> Attributes.LUCK;
            case AttributesNameTool.HEALING -> LHMiracleRoadAttributes.HEALING;
            case AttributesNameTool.HUNGER -> LHMiracleRoadAttributes.HUNGER;
            case AttributesNameTool.JUMP -> LHMiracleRoadAttributes.JUMP;
            case AttributesNameTool.CRITICAL_HIT_RATE -> LHMiracleRoadAttributes.CRITICAL_HIT_RATE;
            case AttributesNameTool.CRITICAL_HIT_DAMAGE -> LHMiracleRoadAttributes.CRITICAL_HIT_DAMAGE;
            case AttributesNameTool.DAMAGE_REDUCTION -> LHMiracleRoadAttributes.DAMAGE_REDUCTION;
            case AttributesNameTool.SOUL_INCREASE -> LHMiracleRoadAttributes.SOUL_INCREASE;
            case AttributesNameTool.DAMAGE_ADDITION -> LHMiracleRoadAttributes.DAMAGE_ADDITION;
            default -> AttributePointsRewardsReloadListener.recordAttribute.get(attributeName);
        };
    }

    /**
     * 判断是否进行格外加成的显示
     * @param attributeName
     * @return
     */
    public static boolean isEspeciallyAttributeView(String attributeName){
        if (attributeName == null) return false;
        return switch (attributeName) {
            case AttributesNameTool.RANGED_DAMAGE,
                    AttributesNameTool.JUMP,
                    AttributesNameTool.HUNGER,
                    AttributesNameTool.HEALING,
                    AttributesNameTool.HEAVY,
                    AttributesNameTool.BURDEN,
                    AttributesNameTool.CRITICAL_HIT_RATE
//                    AttributesNameTool.CRITICAL_HIT_DAMAGE,
//                    AttributesNameTool.DAMAGE_REDUCTION,
//                    AttributesNameTool.SOUL_INCREASE,
//                    AttributesNameTool.DAMAGE_ADDITION
                    -> true;
            default -> false;
        };
    }

    /**
     * 判断该属性名称是否符合属性对象的 descriptionId
     *
     * @param descriptionId
     * @param attributeName
     * @return
     */
    public static boolean isAttributeName(String descriptionId, String attributeName) {
        String[] parts = attributeName.split(":");
        if (parts.length != 2) {
            // 如果不符合key:value的格式，则直接返回false
            return false;
        }
        String key = parts[0];
        String value = parts[1];
        if (descriptionId.equals(value)) {
            return true;
        }

        String format = key + "." + value;
        if (descriptionId.equals(format)) {
            return true;
        }
        return descriptionId.equals("attribute.name." + format) || descriptionId.equals("attribute." + format);
    }

    /**
     * 设置部分属性基础值
     *
     * @param player
     * @param initDifficultyLevel
     */
    public static void setConfigBaseAttribute(ServerPlayer player, int initDifficultyLevel) {
        player.getAttribute(LHMiracleRoadAttributes.BURDEN).setBaseValue(LHMiracleRoadConfig.COMMON.INIT_BURDEN.get());
        player.getAttribute(LHMiracleRoadAttributes.INIT_DIFFICULTY_LEVEL).setBaseValue(initDifficultyLevel);
    }

    /**
     * 将服务端信息同步至客户端信息中
     *
     * @param playerOccupationAttribute
     * @param player
     */
    public static void synchronizationClient(PlayerOccupationAttribute playerOccupationAttribute, ServerPlayer player) {
        AttributeInstance burden = player.getAttribute(LHMiracleRoadAttributes.BURDEN);
        int burdenValue = 0;
        if (burden != null) {
            burdenValue = (int) burden.getValue();
        }
        UUID playerUUID = player.getUUID();
        playerOccupationAttribute.setBurden(burdenValue);
        JsonObject playerOccupationAttributeObject = playerOccupationAttribute.getPlayerOccupationAttribute(playerUUID);
        ClientOccupationMessage message = new ClientOccupationMessage(playerOccupationAttributeObject);
        PlayerAttributeChannel.sendToClient(message, player);
    }

    //同步显示的数据信息
    public static void synchronizationShowAttribute(ServerPlayer player){
        //同步显示的数据信息
        JsonObject showAttributeData = new JsonObject();
        Map<String, JsonObject> showAttributeMap = setShowAttribute(player);
        JsonObject showAttribute = new JsonObject();
        showAttributeMap.forEach(showAttribute::add);
        showAttributeData.addProperty("key","showAttribute");
        showAttributeData.add("data",showAttribute);

        ClientDataMessage attributeTypesMessage = new ClientDataMessage(showAttributeData);
        PlayerAttributeChannel.sendToClient(attributeTypesMessage, player);
    }

    /**
     * 解析并计算字符串公式
     *
     * @param formula
     * @param level
     * @return
     */
    public static int evaluateFormula(String formula, int level) {
        formula = formula.replaceAll("lv", String.valueOf(level));
        return MathCalculatorUtil.getCalculatorInt(formula);
    }

    /**
     * 设置在gui显示的属性信息
     *
     * @param player
     * @return
     */
    public static Map<String, JsonObject> setShowAttribute(ServerPlayer player) {
        Map<String, JsonObject> showAttribute = Maps.newLinkedHashMap();
        for (JsonObject attributeObject : ShowGuiAttributeReloadListener.SHOW_GUI_ATTRIBUTE) {
            String attributeName = LHMiracleRoadTool.isAsString(attributeObject.get("attribute"));
            JsonObject showAttributeObject = new JsonObject();

            Attribute attribute = stringConversionAttribute(attributeName);
            if (attribute == null) continue;
            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance == null) continue;

            showAttributeObject.addProperty("v", attributeInstance.getValue());
            showAttributeObject.addProperty("b", attributeInstance.getBaseValue());
            JsonArray jsonArray = new JsonArray();
            attributeInstance.getModifiers().forEach(modifier -> {
                JsonObject modifierObject = new JsonObject();
                modifierObject.addProperty("a", modifier.getAmount());
                jsonArray.add(modifierObject);
            });
            showAttributeObject.add("m", jsonArray);
            showAttribute.put(attributeName, showAttributeObject);
        }
        return showAttribute;
    }

    /**
     * 概率计算
     *
     * @param probability
     * @return
     */
    public static boolean percentageProbability(int probability) {
        if (probability >= 100) return true;
        else if (probability < 1) return false;

        return new Random().nextInt(100) < probability;
    }

    /**
     * 概率计算
     *
     * @param probability
     * @return
     */
    public static boolean percentageProbability(double probability) {
        if (probability >= 100.0) return true;
        else if (probability <= 0.0) return false;

        return new Random().nextDouble() * 100.0 < probability;
    }

    /**
     * 玩家的奖惩状态更新
     *
     * @param player
     */
    public static void playerPunishmentStateUpdate(ServerPlayer player, PlayerOccupationAttribute playerOccupationAttribute) {
        //更新一下重量的奖惩状态
        AttributeInstance heavyAttributeInstance = player.getAttribute(LHMiracleRoadAttributes.HEAVY);
        AttributeInstance burdenAttributeInstance = player.getAttribute(LHMiracleRoadAttributes.BURDEN);
        double heavy = heavyAttributeInstance.getValue();
        double burden = burdenAttributeInstance.getValue();
        ItemPunishmentTool.setHeavyAttributeModifier(playerOccupationAttribute, player, heavy, burden);

        customEquipmentUpdate(player, playerOccupationAttribute, EquipmentSlot.HEAD, null);
        customEquipmentUpdate(player, playerOccupationAttribute, EquipmentSlot.CHEST, null);
        customEquipmentUpdate(player, playerOccupationAttribute, EquipmentSlot.LEGS, null);
        customEquipmentUpdate(player, playerOccupationAttribute, EquipmentSlot.FEET, null);
        customEquipmentUpdate(player, playerOccupationAttribute, null, InteractionHand.MAIN_HAND);
        customEquipmentUpdate(player, playerOccupationAttribute, null, InteractionHand.OFF_HAND);
    }

    /**
     * 自定义某个位置的装备 并对他进行更新操作
     *
     * @param player
     * @param playerOccupationAttribute
     * @param equipmentSlot
     */
    private static void customEquipmentUpdate(ServerPlayer player, PlayerOccupationAttribute playerOccupationAttribute, EquipmentSlot equipmentSlot, InteractionHand interactionHand) {
        ItemStack stack = null;
        if (equipmentSlot != null) stack = player.getItemBySlot(equipmentSlot);
        else if (interactionHand != null) stack = player.getItemInHand(interactionHand);
        else return;

        if (stack.isEmpty()) return;
        Optional<ItemStackPunishmentAttribute> itemStackPunishmentAttribute = stack
                .getCapability(ItemStackPunishmentAttributeProvider.ITEM_STACK_PUNISHMENT_ATTRIBUTE_PROVIDER)
                .resolve();
        if (itemStackPunishmentAttribute.isEmpty()) return;
        //先清空惩罚信息
        ItemPunishmentTool.cleanItemFromPunishmentAttributeModifier(player, playerOccupationAttribute, itemStackPunishmentAttribute.get());
        //然后重新计算设置一下惩罚信息
        ItemPunishmentTool.setItemToPunishmentAttributeModifier(player, playerOccupationAttribute, itemStackPunishmentAttribute.get());
    }

    /**
     * 通过职业id获取职业信息
     *
     * @param occupationId
     * @return
     */
    public static JsonObject getOccupation(String occupationId) {
        JsonObject occupation = null;
        for (int i = 0; i < OccupationReloadListener.OCCUPATION.size(); i++) {
            JsonObject jsonObject = OccupationReloadListener.OCCUPATION.get(i);
            if (jsonObject.get("id").getAsString().equals(occupationId)) {
                occupation = jsonObject;
            }
        }
        return occupation;
    }

    public static JsonObject getOccupationClient(String occupationId) {
        JsonObject occupation = null;
        for (int i = 0; i < ClientData.OCCUPATION.size(); i++) {
            JsonObject jsonObject = ClientData.OCCUPATION.get(i);
            if (jsonObject.get("id").getAsString().equals(occupationId)) {
                occupation = jsonObject;
            }
        }
        return occupation;
    }

    /**
     * 显示的value格式
     * @return
     */
    public static String getShowValueType(ShowAttributesTypes showValueType,double value,double baseValue,int percentageBase,String attributeName){
        String showValue = "";
//        if (detailedAttribute != null) {
//            showValue = detailedAttribute.get(attributeName);
//            if (showValue != null) return showValue;
//        }
        double base = new BigDecimal(value).setScale(4, RoundingMode.HALF_UP).doubleValue();
        double extra = new BigDecimal(value - baseValue).setScale(4, RoundingMode.HALF_UP).doubleValue();
        showValue = switch (showValueType){
            case BASE -> String.valueOf(base);
            case EXTRA_BASE -> "+" + extra;
            case BASE_PERCENTAGE ->  new BigDecimal(value * percentageBase).setScale(4, RoundingMode.HALF_UP).doubleValue() + "%";
            case EXTRA_PERCENTAGE -> "+" + new BigDecimal((value - baseValue) * percentageBase).setScale(4, RoundingMode.HALF_UP).doubleValue() + "%";
        };
//        detailedAttribute.put(attributeName,showValue);
        return showValue;
    }

    /**
     * 显示的由本模组自定义属性的value格式
     * @return
     */
    public static String getShowLHMiracleRoadValueType(JsonArray modifiers,String attributeName,int percentageBase){
        String showValue = "";
//        if (detailedAttribute != null) {
//            showValue = detailedAttribute.get(attributeName);
//            if (showValue != null) return showValue;
//        }
        for (String key : ClientData.POINTS_REWARDS.keySet()){
            if (key.equals(attributeName)){
                JsonObject pointsReward = ClientData.POINTS_REWARDS.get(key);
                String operation = isAsString(pointsReward.get("operation"));
                double amount = 0.0;
;                for (JsonElement jsonElement: modifiers){
                    JsonObject object = isAsJsonObject(jsonElement);
                    amount += isAsDouble(object.get("a"));
                }
                showValue = switch (operation) {
                    case "addition" -> "+" + new BigDecimal(amount).setScale(4, RoundingMode.HALF_UP).doubleValue();
                    case "multiply_base", "multiply_total" -> "+" + new BigDecimal(amount * percentageBase).setScale(4, RoundingMode.HALF_UP).doubleValue() + "%";
                    default -> "";
                };
//                detailedAttribute.put(attributeName,showValue);
                return showValue;
            }
        }
        return showValue;
    }

    public static boolean isShowPointsButton(int currentLevel,int maxLevel,int attributeMaxLevel){
        if (attributeMaxLevel < 1) return currentLevel < maxLevel;
        else return currentLevel < attributeMaxLevel;
    }

    public static JsonObject getEquipment(Map<String,Map<String,JsonObject>> equipment,String id){
        int lastIndex = id.lastIndexOf(".");
        if (lastIndex == -1) return null;
        String firstPart = id.substring(0, lastIndex);
        String secondPart = id.substring(lastIndex + 1);
        if (equipment.get(firstPart) == null) return null;
        return equipment.get(firstPart).get(secondPart);
    }

    public static void addItemStack(ServerPlayer player,ItemStack itemStack){
        boolean wasAdded = player.getInventory().add(itemStack);
        if (!wasAdded) {
            player.drop(itemStack, false);
        }
    }

    public static void getSoulParticle(ServerLevel serverLevel, ServerPlayer player, int soulCount,int max){
        int particleCount = Math.min(Math.max(soulCount / 200,10),max);
        float speed = .1f + ((float) particleCount / max * .025f);
        serverLevel.sendParticles(player,new SoulParticleOption(player.getId()), true, player.getX(), player.getY() + player.getBbHeight() * 0.5, player.getZ(), particleCount, 0.1, 0.1, 0.1,speed);
    }

    public static void getSoulParticle(ServerLevel serverLevel, ServerPlayer player, int soulCount, int max,int soulCountDivisor, Entity target){
        if (target == null) return;
        int particleCount = Math.min(Math.max(soulCount / soulCountDivisor,10),max);
        float speed = .1f + ((float) particleCount / max * .025f);
        serverLevel.sendParticles(player,new SoulParticleOption(player.getId()), true, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), particleCount, 0.1, 0.1, 0.1,speed);
    }

    public static void getSoulParticle(ServerLevel serverLevel, ServerPlayer player, int soulCount, int max,int min,int soulCountDivisor, Entity target){
        if (target == null) return;
        int particleCount = Math.min(Math.max(soulCount / soulCountDivisor,min),max);
        float speed = .1f + ((float) particleCount / max * .025f);
        serverLevel.sendParticles(player,new SoulParticleOption(player.getId()), true, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), particleCount, 0.1, 0.1, 0.1,speed);
    }
}
