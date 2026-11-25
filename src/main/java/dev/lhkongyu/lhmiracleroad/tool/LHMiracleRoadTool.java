package dev.lhkongyu.lhmiracleroad.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.attributes.LHMiracleRoadAttributes;
import dev.lhkongyu.lhmiracleroad.config.LHMiracleRoadConfig;
import dev.lhkongyu.lhmiracleroad.config.strengthen.WeaponTypeConfig;
import dev.lhkongyu.lhmiracleroad.entity.player.PlayerSoulEntity;
import dev.lhkongyu.lhmiracleroad.generator.SpellDamageTypes;
import dev.lhkongyu.lhmiracleroad.items.gem.AttributeGem;
import dev.lhkongyu.lhmiracleroad.tool.mathcalculator.MathCalculatorUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Math;
import org.joml.Vector3f;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class LHMiracleRoadTool {

    public static final RandomSource random = RandomSource.createThreadSafe();

    public static final Map<UUID, PlayerSoulEntity> SOUL_ENTITY_MAP = new HashMap<>();

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
     * 获取该mod是否存在
     *
     * @param modId
     * @return
     */
    public static boolean isModExist(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static String isAsString(JsonElement jsonElement) {
        return jsonElement != null && !jsonElement.isJsonNull() ? jsonElement.getAsString() : null;
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
        return calculateTotalIncrease(level, value, levelPromoteValue, levelPromote, min,
                LHMiracleRoadConfig.COMMON.LEVEL_BASE.get());
    }

    public static double calculateTotalIncrease(int level, double value, double levelPromoteValue,
                                               int levelPromote, double min, int levelBase) {
        level -= levelBase;
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
            case NameTool.MAX_HEALTH -> Attributes.MAX_HEALTH;
            case NameTool.ATTACK_DAMAGE -> Attributes.ATTACK_DAMAGE;
            case NameTool.RANGED_DAMAGE -> LHMiracleRoadAttributes.RANGED_DAMAGE;
            case NameTool.BURDEN -> LHMiracleRoadAttributes.BURDEN;
            case NameTool.HEAVY -> LHMiracleRoadAttributes.HEAVY;
            case NameTool.ARMOR -> Attributes.ARMOR;
            case NameTool.ARMOR_TOUGHNESS -> Attributes.ARMOR_TOUGHNESS;
            case NameTool.ATTACK_SPEED -> Attributes.ATTACK_SPEED;
            case NameTool.MOVEMENT_SPEED -> Attributes.MOVEMENT_SPEED;
            case NameTool.LUCK -> Attributes.LUCK;
            case NameTool.HEALING -> LHMiracleRoadAttributes.HEALING;
            case NameTool.HUNGER -> LHMiracleRoadAttributes.HUNGER;
            case NameTool.JUMP -> LHMiracleRoadAttributes.JUMP;
            case NameTool.CRITICAL_HIT_RATE -> LHMiracleRoadAttributes.CRITICAL_HIT_RATE;
            case NameTool.CRITICAL_HIT_DAMAGE -> LHMiracleRoadAttributes.CRITICAL_HIT_DAMAGE;
            case NameTool.DAMAGE_REDUCTION -> LHMiracleRoadAttributes.DAMAGE_REDUCTION;
            case NameTool.SOUL_INCREASE -> LHMiracleRoadAttributes.SOUL_INCREASE;
            case NameTool.DAMAGE_ADDITION -> LHMiracleRoadAttributes.DAMAGE_ADDITION;
            case NameTool.MINING_SPEED -> LHMiracleRoadAttributes.MINING_SPEED;
            case NameTool.MAGIC_DAMAGE_ADDITION -> LHMiracleRoadAttributes.MAGIC_DAMAGE_ADDITION;
            case NameTool.MAGIC_ATTRIBUTE_DAMAGE -> LHMiracleRoadAttributes.MAGIC_ATTRIBUTE_DAMAGE;
            case NameTool.FLAME_ATTRIBUTE_DAMAGE -> LHMiracleRoadAttributes.FLAME_ATTRIBUTE_DAMAGE;
            case NameTool.LIGHTNING_ATTRIBUTE_DAMAGE -> LHMiracleRoadAttributes.LIGHTNING_ATTRIBUTE_DAMAGE;
            case NameTool.DARK_ATTRIBUTE_DAMAGE -> LHMiracleRoadAttributes.DARK_ATTRIBUTE_DAMAGE;
            case NameTool.HOLY_ATTRIBUTE_DAMAGE -> LHMiracleRoadAttributes.HOLY_ATTRIBUTE_DAMAGE;
            case NameTool.SOUL_ATTRIBUTE_DAMAGE -> LHMiracleRoadAttributes.SOUL_ATTRIBUTE_DAMAGE;
            case NameTool.ABNORMAL_DAMAGE -> LHMiracleRoadAttributes.ABNORMAL_DAMAGE;
            case NameTool.ABNORMAL_BLEED_DAMAGE -> LHMiracleRoadAttributes.ABNORMAL_BLEED_DAMAGE;
            case NameTool.ABNORMAL_FROST_DAMAGE -> LHMiracleRoadAttributes.ABNORMAL_FROST_DAMAGE;
            case NameTool.ABNORMAL_POISON_DAMAGE -> LHMiracleRoadAttributes.ABNORMAL_POISON_DAMAGE;
            case NameTool.ABNORMAL_BURN_DAMAGE -> LHMiracleRoadAttributes.ABNORMAL_BURN_DAMAGE;
            case NameTool.ABNORMAL_BLEED_BUILDUP -> LHMiracleRoadAttributes.ABNORMAL_BLEED_BUILDUP;
            case NameTool.ABNORMAL_FROST_BUILDUP -> LHMiracleRoadAttributes.ABNORMAL_FROST_BUILDUP;
            case NameTool.ABNORMAL_POISON_BUILDUP -> LHMiracleRoadAttributes.ABNORMAL_POISON_BUILDUP;
            case NameTool.ABNORMAL_BURN_BUILDUP -> LHMiracleRoadAttributes.ABNORMAL_BURN_BUILDUP;
            case NameTool.ABNORMAL_BUILDUP -> LHMiracleRoadAttributes.ABNORMAL_BUILDUP;
            case NameTool.ARMOR_PENETRATION -> LHMiracleRoadAttributes.ARMOR_PENETRATION;
            default -> {
                ResourceLocation id = ResourceLocation.tryParse(attributeName);
                yield id == null ? null : ForgeRegistries.ATTRIBUTES.getValue(id);
            }
        };
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
     * 解析并计算字符串公式
     * @param formula
     * @param exp
     * @param hp
     * @param atk
     * @param arm
     * @param atou
     * @param buff
     * @return
     */
    public static int evaluateFormula(String formula,int exp, int hp,int atk,int arm,int atou,int buff) {
        formula = formula.replaceAll("exp", String.valueOf(exp));
        formula = formula.replaceAll("hp", String.valueOf(hp));
        formula = formula.replaceAll("atk", String.valueOf(atk));
        formula = formula.replaceAll("arm", String.valueOf(arm));
        formula = formula.replaceAll("atou", String.valueOf(atou));
        formula = formula.replaceAll("buff", String.valueOf(buff));
        return MathCalculatorUtil.getCalculatorInt(formula);
    }

    public static int evaluateUpgradeSoulCost(String formula, int level) {
        try {
            return evaluateFormula(formula, level);
        } catch (RuntimeException | StackOverflowError invalid) {
            return evaluateFormula(LHMiracleRoadConfig.COMMON.EMPIRICAL_CALCULATION_FORMULA.getDefault(), level);
        }
    }

    public static int evaluateSoulReward(int exp, int hp, int atk, int arm, int atou, int buff) {
        var formula = LHMiracleRoadConfig.COMMON.EXPERIENCE_ACQUISITION_FORMULA;
        try {
            return evaluateFormula(formula.get(), exp, hp, atk, arm, atou, buff);
        } catch (RuntimeException | StackOverflowError invalid) {
            return evaluateFormula(formula.getDefault(), exp, hp, atk, arm, atou, buff);
        }
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
     * 幸运值收益
     * @param luck
     * @return
     */
    public static double getLuckBonus(double up,double luck) {
        return up * (1 - java.lang.Math.pow(0.5, luck / 10.0));
    }

    /**
     * 返回出生点
     * @param player
     */
    public static void teleportToRespawn(ServerPlayer player) {
        ServerLevel level = player.server.getLevel(player.getRespawnDimension());
        if (level == null) level = player.server.overworld();
        BlockPos pos = player.getRespawnPosition();
        if (pos == null) pos = level.getSharedSpawnPos();
        player.teleportTo(level,
                pos.getX() + 0.5,
                pos.getY() + 0.1,
                pos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );
    }

    public static void addItemStack(ServerPlayer player,ItemStack itemStack){
        boolean wasAdded = player.getInventory().add(itemStack);
        if (!wasAdded) {
            player.drop(itemStack, false);
        }
    }

    public static double getAttributeValue(AttributeInstance attributeInstance){
        if (attributeInstance != null) return attributeInstance.getValue();
        return 0;
    }

    public static void attackMagicHurt(LivingEntity source,LivingEntity target,ResourceKey<DamageType> resourceKey,float damage){
        DamageSource src = LHMiracleRoadTool.getDamageSource(source, resourceKey);
        damage = AttributeGem.attributeAdditionalDamage(damage,src,target,source);
        target.hurt(src, damage);
    }

    public static void magicHurt(LivingEntity source,LivingEntity target,ResourceKey<DamageType> resourceKey,float damage){
        DamageSource src = LHMiracleRoadTool.getDamageSource(source, resourceKey);
        target.hurt(src, damage);
    }

    public static boolean isBelongTeammate(Entity target,Entity owner) {
        if (owner == null || target == owner || owner.isAlliedTo(target)) {
            return false;
        }
        if (!(target instanceof TamableAnimal tamable)) {
            return true;
        }
        LivingEntity tamableOwner = tamable.getOwner();
        return tamableOwner == null || !tamableOwner.equals(owner);
    }

    public static boolean itemIsWeaponsAndEquipmentAll(ItemStack stack) {
        return itemIsWeaponsAll(stack)
                || itemIsArmors(stack)
                || itemIsTool(stack)
                || itemIsShield(stack)
                || itemIsMagicStaff(stack);
    }

    public static boolean itemIsShield(ItemStack stack){
        return stack.getItem() instanceof ShieldItem;
    }

    public static boolean itemIsTool(ItemStack stack){
        return stack.getItem() instanceof PickaxeItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof ShovelItem
                || stack.getItem() instanceof HoeItem;
    }

    public static boolean itemIsArmors(ItemStack stack){
        return stack.getItem() instanceof ArmorItem || stack.getItem() instanceof ElytraItem;
    }

    public static boolean itemIsWeaponsAll(ItemStack stack){
        return itemIsWeapons(stack) || itemIsRangedWeapons(stack);
    }

    public static boolean itemIsWeapons(ItemStack stack){
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof TridentItem
                || stack.is(ItemTags.SWORDS)
                || stack.is(ItemTags.AXES)
                || WeaponTypeConfig.contains(stack, WeaponTypeConfig.Type.MELEE);
    }

    public static boolean itemIsRangedWeapons(ItemStack stack){
        return stack.getItem() instanceof ProjectileWeaponItem
                || WeaponTypeConfig.contains(stack, WeaponTypeConfig.Type.RANGED);
    }

    public static boolean itemIsMagicStaff(ItemStack stack){
        return WeaponTypeConfig.contains(stack, WeaponTypeConfig.Type.MAGIC_STAFF);
    }

//    /**
//     * 获取造成的伤害类型 并且无视护甲
//     * @param entity
//     * @param resourceKey
//     * @return
//     */
//    public static DamageSource getDamageSourceType(Entity entity, ResourceKey<DamageType> resourceKey,TagKey<DamageType> tagKey){
//        Set<TagKey<DamageType>> tagSet = new HashSet<>();
//        tagSet.add(tagKey);
//        return getDamageSource(entity,resourceKey,tagSet);
//    }

    /**
     * 获取攻击类型
     * @param entity
     * @return
     */
    public static DamageSource getDamageSource(Entity entity, ResourceKey<DamageType> key) {
        return entity.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolder(key)
                .<DamageSource>map(holder -> new DamageSource(holder, entity))
                .orElse(entity.level().damageSources().generic());
    }

    public static boolean isMagicDamageTypes(DamageSource damageSource){
        return damageSource.is(DamageTypes.INDIRECT_MAGIC) || damageSource.is(DamageTypes.MAGIC)
                || damageSource.is(DamageTypes.IN_FIRE) || damageSource.is(DamageTypes.ON_FIRE)
                || damageSource.is(DamageTypes.LAVA) || damageSource.is(DamageTypes.FREEZE) || LHMiracleRoadTool.isMagicDamage(damageSource);
    }

    public static boolean isMagicDamage(DamageSource damageSource){
        if (damageSource.is(SpellDamageTypes.FLAME_MAGIC)){
            return true;
        }else if (damageSource.is(SpellDamageTypes.HOLY_MAGIC)){
            return true;
        }else if (damageSource.is(SpellDamageTypes.DARK_MAGIC)){
            return true;
        }else if (damageSource.is(SpellDamageTypes.LIGHTNING_MAGIC)){
            return true;
        }else if (damageSource.is(SpellDamageTypes.SOUL_MAGIC)){
            return true;
        }else return damageSource.is(SpellDamageTypes.MAGIC);
    }

    public static boolean isMagic(DamageSource damageSource){
        return damageSource.is(DamageTypes.INDIRECT_MAGIC) || damageSource.is(DamageTypes.MAGIC)
                || damageSource.is(DamageTypes.IN_FIRE) || damageSource.is(DamageTypes.ON_FIRE)
                || damageSource.is(DamageTypes.LAVA) || damageSource.is(DamageTypes.FREEZE) || damageSource.is(SpellDamageTypes.MAGIC);
    }

}
