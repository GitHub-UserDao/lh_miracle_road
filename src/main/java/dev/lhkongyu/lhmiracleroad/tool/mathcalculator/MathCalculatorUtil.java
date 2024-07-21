package dev.lhkongyu.lhmiracleroad.tool.mathcalculator;
import dev.lhkongyu.lhmiracleroad.tool.LHMiracleRoadTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class MathCalculatorUtil {

    private static final Integer MAX_VALUE = Integer.MAX_VALUE;

    private static final Logger log = LoggerFactory.getLogger(MathCalculatorUtil.class);

    //所支持的运算操作符集合， 两元运算符
    private static final Set<Character> operateSet = new HashSet<>();

    static {
        //加
        operateSet.add('+');
        //减
        operateSet.add('-');
        //乘
        operateSet.add('*');
        //除
        operateSet.add('/');
        //求余
        operateSet.add('%');
    }

    /**
     * 通过当前左括号(的索引 ， 找到与之匹配对应的右括号) 的索引
     * @param s 待匹配的字符串
     * @param fromIndex 开始索引
     * @param leftDest 左括号
     * @return
     */
    private static int matchBracketIndex(String s, int fromIndex, char leftDest) {
        if (StringUtils.isEmpty(s)) {
            return -1;
        }
        //取出匹配目标的第一个索引
        int index0 = s.indexOf(leftDest, fromIndex);
        if (index0 != -1) {
            //1、申明一个stack
            Stack<Character> stack = new Stack<>();
            //遍历s String本质上是char[]
            for (int i = index0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '{' || c == '[' || c == '(') {
                    //如果是{ [ (  压入栈中
                    stack.push(c);
                } else if (c == '}' || c == ']' || c == ')') {
                    //  }  ]  )   进行比对,
                    if (stack.isEmpty()) {
                        return -1;
                    }
                    char topChar = stack.pop();
                    if ((topChar == '[' && c == ']') || (topChar == '(' && c == ')') || (topChar == '{') && c == '}') {
                        if (stack.isEmpty()) {
                            return i;
                        } else {
                            continue;
                        }
                    }
                } else {
                    continue;
                }
            }

        }
        return -1;
    }

    /**
     * 两个数相加
     * @param v1
     * @param v2
     * @return
     */
    private static String add(String v1, String v2) {
        BigDecimal v1Bd = new BigDecimal(v1);
        BigDecimal v2Bd = new BigDecimal(v2);
        return v1Bd.add(v2Bd).toString();
    }

    /**
     *两个数相减
     * @param v1
     * @param v2
     * @return
     */
    private static String sub(String v1, String v2) {
        BigDecimal v1Bd = new BigDecimal(v1);
        BigDecimal v2Bd = new BigDecimal(v2);
        return v1Bd.subtract(v2Bd).toString();
    }

    /**
     * 两个数向乘
     * @param v1
     * @param v2
     * @return
     */
    private static String mul(String v1, String v2) {
        BigDecimal v1Bd = new BigDecimal(v1);
        BigDecimal v2Bd = new BigDecimal(v2);
        return v1Bd.multiply(v2Bd).toString();
    }

    /**
     * 两个数相除
     * @param v1
     * @param v2
     * @return
     */
    private static String div(String v1, String v2) {
        BigDecimal v1Bd = new BigDecimal(v1);
        BigDecimal v2Bd = new BigDecimal(v2);
        return v1Bd.divide(v2Bd, 2, RoundingMode.HALF_UP).toString();
    }

    /**
     * v1%v2 取余
     * @param v1
     * @param v2
     * @return
     */
    private static String mod(String v1, String v2) {
        BigDecimal v1Bd = new BigDecimal(v1);
        BigDecimal v2Bd = new BigDecimal(v2);
        return v1Bd.remainder(v2Bd).toString();
    }

    /**
     * 公式字符串， 如："1+1*2+(10-(2*(5-3)*(2-1))-4)+10/(5-0) + log(10) + log(10,12)"
     * 注意log(10) ==> ln(10) ==> log(e,10) 表示以自然数e为底的对数
     * 使用递归计算，判断表达式是否有括号，有括号，则先计算括号内的表达式，无则直接运算结果。
     * @param mathFormula
     * @return
     */
    public static String calculator(String mathFormula) {
        if (StringUtils.isEmpty(mathFormula)) {
            throw new RuntimeException("非法计算公式！");
        }
        //替换空格
        mathFormula = mathFormula.replaceAll(" ", "");
        int bracket = mathFormula.indexOf("[");
        int brace = mathFormula.indexOf("{");
        if (bracket != -1 || brace != -1) {
            //将字符串中的"{}"、"[]"替换成"()"
            log.info("计算公式：{}", mathFormula);
            mathFormula = mathFormula.replaceAll("[\\[\\{]", "(").replaceAll("[\\]\\}]", ")");
            log.info("标准数学计算公式 '{,[':" + mathFormula);
        }
        //如果有自定义的数学公式，则先计算自定义的公式
        //如果有括号，则先计算括号内的（去括号）
        //没有括号直接计算
        String result0 = calculatorSelfMathFormula(mathFormula);
        //结果保留八位小数
        return new BigDecimal(result0).setScale(8,BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 去除自定义公式
     * @param mathFormula
     * @return
     */
    private static String calculatorSelfMathFormula(String mathFormula) {
        if (StringUtils.isEmpty(mathFormula)) {
            throw new RuntimeException("非法参数错误！");
        }
        mathFormula = mathFormula.replaceAll(" ", "");
        //去除自定义公式
        List<SelfMathFormulaEnum> selfMathFormulaEnums = SelfMathFormulaEnum.getSelfMathFormulas();
        boolean flag = false;
        for (SelfMathFormulaEnum mathFormulaEnum : selfMathFormulaEnums) {
            if (mathFormula.contains(mathFormulaEnum.getFormulaName())) {
                flag = true;
                break;
            }
        }
        //包含自定义公式
        if (flag) {
            for (int i = 0; i < selfMathFormulaEnums.size();) {
                boolean repeat = false;
                SelfMathFormulaEnum mathFormulaEnum = selfMathFormulaEnums.get(i);
                //如果该公式表达式包含自定义数学公式
                if (mathFormula.contains(mathFormulaEnum.getFormulaName())) {
                    //如果匹配到，则获取第一个自定义的数学公式首字母所在的索引（该索引是格式化后的索引）
                    int index0 = mathFormula.indexOf(mathFormulaEnum.getFormulaName());
                    //取出该公式括号中内容字符,不包括左右字符
                    String left = mathFormula.substring(0, index0);
                    int index1 = matchBracketIndex(mathFormula, index0, '(');
                    String right = mathFormula.substring(index1 +1);
                    String bracketsContent = mathFormula.substring(index0 + mathFormulaEnum.getFormulaNameLength() + 1, index1);
                    //计算括号中的值，如果该字符串又包含其他自定义公式，则递归继续计算
                    //left + result0 + right
                    mathFormula =  left + selfMathCalculation(mathFormulaEnum.getFormulaName(), calculatorSelfMathFormula(bracketsContent)) + right;
                    repeat = true;
                }
                if (repeat) {
                    i = i;
                }else {
                    i++;
                }
            }
        }
        //直接进行计算
        return standardCalculation(mathFormula);
    }

    /**
     * 自定义公式计算
     * @param mathFormulaName
     * @param digitStr
     * @return
     */
    private static String selfMathCalculation(String mathFormulaName, String digitStr) {
        double result;
        if (StringUtils.isEmpty(digitStr)) {
            throw new RuntimeException("非法计算公式参数！");
        }
        String[] args = digitStr.split(",", -1);

        SelfMathFormulaEnum selfMathFormulaEnum = SelfMathFormulaEnum.getSelfMathFormulaEnum(mathFormulaName);
        if (selfMathFormulaEnum == null) {
            throw new RuntimeException("非法数学公式名称");
        }
        switch (selfMathFormulaEnum) {
            case abs:
                result = Math.abs(Double.parseDouble(args[0]));
                break;
            case acos:
                result = Math.acos(Double.parseDouble(args[0]));
                break;
            case asin:
                result =  Math.asin(Double.parseDouble(args[0]));
                break;
            case atan:
                result = Math.atan(Double.parseDouble(args[0]));
                break;
            case ceil:
                result = Math.ceil(Double.parseDouble(args[0]));
                break;
            case cos:
                result = Math.cos(Double.parseDouble(args[0]));
                break;
            case exp:
                result = Math.exp(Double.parseDouble(args[0]));
                break;
            case floor:
                result = Math.floor(Double.parseDouble(args[0]));
                break;
            case log:
                result = Math.log(Double.parseDouble(args[0]));
                break;
            case max:
                result = Math.max(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
                break;
            case min:
                result = Math.min(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
                break;
            case pow:
                result = Math.pow(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
                break;
            case round:
                result = Math.round(Double.parseDouble(args[0]));
                break;
            case sin:
                result = Math.sin(Double.parseDouble(args[0]));
                break;
            case sqrt:
                result = Math.sqrt(Double.parseDouble(args[0]));
                break;
            case tan:
                result = Math.tan(Double.parseDouble(args[0]));
                break;
            default:
                throw new RuntimeException("找不到匹配的计算公式！");

        }
        return String.valueOf(result);
    }


    /**
     * 标准计算，不包含自定义函数, 但包含括号与其他符号表达式
     * @param str
     * @return
     */
    private static String standardCalculation(String str) {
        if (StringUtils.isEmpty(str)) {
            log.error("非法计算公式！");
            throw new RuntimeException("非法计算公式！");
        }
        String[] args = str.split(",", -1);
        if (args != null && args.length > 0) {
            List<String> argResult = new ArrayList<>();
            for (String arg : args) {
                //每一个arg 都是一个算式（带上括号的）
                //判断是公式表达是是否存在小括号（优先级）
                int hasBrackets = arg.lastIndexOf('(');
                if (hasBrackets == -1) {
                    //没有小括号，直接计算
                    argResult.add(cac(arg));
                }else {
                    int cr = arg.indexOf(')', hasBrackets);
                    String left = arg.substring(0, hasBrackets);
                    String right = arg.substring(cr + 1);
                    //如果存在"("提取括号中的表达式
                    String middle = arg.substring(hasBrackets + 1, cr);
                    argResult.add(standardCalculation(left + cac(middle) + right));
                }
            }
            return StringUtils.join(argResult, ",");
        }
        throw new RuntimeException("非法算式参数！");
    }

    /**
     * DESC:计算表达式，判断是否存在乘除运算，存在则先执行乘除运算，然后执行加减运算，返回运算结果；
     * 不存在，直接运行加减运算，返回运算结果。
     * @param str
     * @return
     */
    private static String cac(String str) {
        //字符串中不存在*，/, %
        int mulIndex = str.indexOf('*');
        int divIndex = str.indexOf('/');
        int modIndex = str.indexOf('%');
        //只有加法和减法
        if (mulIndex == -1 && divIndex == -1 && modIndex == -1) {
            return AASOperation(str);
        }
        String result0 = "0";

        //定义先处理的符号索引位置
        int index0 = getMin(-1,mulIndex, divIndex, modIndex);
        try {
            String left = str.substring(0, index0);
            String v1 = lastNumber(left);
            left = left.substring(0, left.length() - v1.length());
            String right = str.substring(index0 + 1);
            String v2 = firstNumber(right);
            right = right.substring(v2.length());

            if (index0 == mulIndex) {
                result0 = mul(v1, v2);
            } else if(index0 == divIndex) {
                result0 = div(v1, v2);
            } else if(index0 == modIndex) {
                result0 = mod(v1, v2);
            }
            String s = left + result0 + right;
            return cac(left + result0 + right);
        }catch (Exception e) {
            log.error("数学计算公式错误"+ e.getMessage());
            throw new RuntimeException("数学计算公式错误！");
        }
    }


    /**
     * 求给定可变参数中不等于noNum 的最小值
     * @param noNum
     * @param a
     * @return
     */
    private static int getMin(int noNum, int... a){
        if (a == null || a.length == 0) {
            throw new RuntimeException("非法参数！");
        }
        int min = a[0];
        for (int i = 1; i < a.length; i++) {
            if (min ==noNum || (min > a[i] && a[i] != noNum)) {
                min = a[i];
            }
        }
        if (min == noNum) {
            throw new RuntimeException("非法可变参数！");
        }
        return min;
    }


    /**
     * 获得表达式的最后连续合法数字
     * @param str
     * @return
     */
    private static String lastNumber(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--) {
            char c = str.charAt(i);
            //包含首字母为-
            if (Character.isDigit(c) || (i != 0 && c == '.') || ((i == 0 || operateSet.contains(str.charAt(i -1))) && c == '-')) {
                sb.append(c);
            }else {
                break;
            }
        }
        return sb.reverse().toString();
    }


    /**
     * 获得表达式的最后连续合法数字
     * @param str
     * @return
     */
    private static String firstNumber(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            //包含首字母为-
            if (Character.isDigit(c) || (i != 0 && c == '.') || (i == 0 && c == '-')) {
                sb.append(c);
            }else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * 只用加减操作
     * @param mathStr 只有加减操作的数学运算字符串： 如2.98-5-6+9-0.2-8  或  -2.0-9-5+9  或 -9-2
     * @param mathStr
     * @return
     */
    private static String AASOperation(String mathStr) {
        if (StringUtils.isEmpty(mathStr)) {
            throw new RuntimeException("非法计算参数");
        }
        //这里字符串加上一个运算法号，只要是合法的都可以，只是为了走一步运算
        char[] options = (mathStr + "+").replaceAll(" ", "").toCharArray();
        String result0 = "0";
        StringBuilder sb = new StringBuilder();
        char sign = '+';
        for (int i = 0; i < options.length; i++) {
            if (isHexadecimalDigit(options[i]) || options[i] == '.') {
                sb.append(options[i]);
            } else {
                if ((i == 0 && options[i] == '-') || (i>1 && operateSet.contains(options[i-1]))) {
                    sb.append(options[i]);
                }else {
                    if (sb.length() > 0){
                        //先默认为 + 把第一个数值加上
                        if (sign == '+') {
                            result0 = add(result0, sb.toString());
                        } else {
                            result0 = sub(result0, sb.toString());
                        }
                        sb.setLength(0);
                        sign = options[i];
                    } else {
                        throw new RuntimeException("非法数学公式错误！");
                    }
                }

            }
        }

        return result0;
    }

    public static boolean isHexadecimalDigit(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f');
    }

    public static int getCalculatorInt(String mathFormula){
        double value = Double.parseDouble(calculator(mathFormula));
        if (value > MAX_VALUE) return MAX_VALUE;
        return (int) value;
    }

    public static void main(String args[]){
        String k2 ="min(pow(3.02 * lv,2) + 98.4 * lv, 19999999)";
        System.err.println(LHMiracleRoadTool.evaluateFormula(k2,15));
    }
}
