package dev.lhkongyu.lhmiracleroad.tool.mathcalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义数学公式枚举
 **/
enum SelfMathFormulaEnum {

    abs("abs", 1, 3, "abs(x)", "返回数的绝对值"),
    acos("acos", 1, 4,"acos(x)", "返回数的反余弦值"),
    asin("asin", 1, 4,"asin(x)", "返回数的反正弦值"),
    atan("atan", 1, 4,"atan(x)", "以介于 -PI/2 与 PI/2 弧度之间的数值来返回 x 的反正切值"),
    ceil("ceil", 1, 4,"ceil(x)", "对数进行上舍入"),
    cos("cos", 1, 3,"cos(x)", "返回数的余弦"),
    exp("exp", 1, 3,"exp(x)", "返回 e 的指数"),
    floor("floor", 1, 5,"floor(x)", "对数进行下舍入"),
    log("log", 1, 3,"log(x)", "返回数的自然对数（底为e）"),
    max("max", 2, 3,"max(x,y)", "返回 x 和 y 中的最高值"),
    min("min", 2, 3,"min(x,y)", "返回 x 和 y 中的最低值"),
    pow("pow", 2, 3,"pow(x,y)", "返回 x 的 y 次幂"),
    round("round", 1, 5,"round(x)", "把数四舍五入为最接近的整数"),
    sin("sin", 1, 3,"sin(x)", "返回数的正弦"),
    sqrt("sqrt", 1, 4,"sqrt(x)", "返回数的平方根"),
    tan("tan", 1, 3,"tan(x)", "返回角的正切");


    /**
     * 公式名称
     **/
    private String formulaName;

    /**
     * 公式参数数量
     **/
    private Integer formulaArgCount;

    /**
     * 公式名称长度
     **/
    private Integer formulaNameLength;

    /**
     * 公式表达式
     **/
    private String formulaExpresion;

    /**
     * 公式描述
     **/
    private String description;

    /**
     * @param formulaName
     * @param formulaArgCount
     * @return com.jxv.common.utils.MathCalculatorUtil.SelfMathFormulaEnum
     * @Author fangzhenxun
     * @Description 根据自定义公式名称，和参数数量返回匹配的枚举实体
     * @Date 2020/12/31 10:14
     **/
    public static SelfMathFormulaEnum getSelfMathFormulaEnum(String formulaName, Integer formulaArgCount) {
        for (SelfMathFormulaEnum selfMathFormulaEnum : SelfMathFormulaEnum.values()) {
            if (selfMathFormulaEnum.getFormulaName().equals(formulaName) && selfMathFormulaEnum.getFormulaArgCount().equals(formulaArgCount)) {
                return selfMathFormulaEnum;
            }
        }
        return null;
    }

    /**
     * @Author fangzhenxun
     * @Description  根据名称获取函数名
     * @Date 2020/12/31 17:10
     * @param formulaName
     * @return com.jxv.common.utils.MathCalculatorUtil.SelfMathFormulaEnum
     **/
    public static SelfMathFormulaEnum getSelfMathFormulaEnum(String formulaName) {
        for (SelfMathFormulaEnum selfMathFormulaEnum : SelfMathFormulaEnum.values()) {
            if (selfMathFormulaEnum.getFormulaName().equals(formulaName)) {
                return selfMathFormulaEnum;
            }
        }
        return null;
    }

    /**
     * @param
     * @return java.util.List<java.lang.String>
     * @Author fangzhenxun
     * @Description 获取自定义公式的简单名称集合
     * @Date 2020/12/31 14:44
     **/
    public static List<String> getSelfMathFormulaNames() {
        List<String> formulaNames = new ArrayList<>();
        for (SelfMathFormulaEnum selfMathFormulaEnum : SelfMathFormulaEnum.values()) {
            formulaNames.add(selfMathFormulaEnum.getFormulaName());
        }
        return formulaNames;
    }

    /**
     * @Author fangzhenxun
     * @Description  获取所有的自定义函数枚举
     * @Date 2021/1/6 10:27
     * @param
     * @return java.util.List<com.jxv.common.utils.MathCalculatorUtil.SelfMathFormulaEnum>
     **/
    public static List<SelfMathFormulaEnum> getSelfMathFormulas() {
        List<SelfMathFormulaEnum> formulaNames = new ArrayList<>();
        for (SelfMathFormulaEnum selfMathFormulaEnum : SelfMathFormulaEnum.values()) {
            formulaNames.add(selfMathFormulaEnum);
        }
        return formulaNames;
    }


    SelfMathFormulaEnum(String formulaName, Integer formulaArgCount, Integer formulaNameLength, String formulaExpresion, String description) {
        this.formulaName = formulaName;
        this.formulaArgCount = formulaArgCount;
        this.formulaNameLength = formulaNameLength;
        this.formulaExpresion = formulaExpresion;
        this.description = description;
    }

    public Integer getFormulaNameLength() {
        return formulaNameLength;
    }

    public void setFormulaNameLength(Integer formulaNameLength) {
        this.formulaNameLength = formulaNameLength;
    }

    public String getFormulaName() {
        return formulaName;
    }

    public void setFormulaName(String formulaName) {
        this.formulaName = formulaName;
    }

    public Integer getFormulaArgCount() {
        return formulaArgCount;
    }

    public void setFormulaArgCount(Integer formulaArgCount) {
        this.formulaArgCount = formulaArgCount;
    }

    public String getFormulaExpresion() {
        return formulaExpresion;
    }

    public void setFormulaExpresion(String formulaExpresion) {
        this.formulaExpresion = formulaExpresion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
