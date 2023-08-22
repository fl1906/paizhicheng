package com.ruoyi.test;


//import cn.hutool.extra.expression.engine.jexl.JexlEngine;
import cn.hutool.extra.expression.ExpressionUtil;
//import cn.hutool.extra.expression.engine.jexl.JexlEngine;
import com.googlecode.aviator.AviatorEvaluator;
import com.ismail.mxreflection.annotations.Arg;

import com.ismail.mxreflection.annotations.Expression;

import com.ismail.mxreflection.core.Calculator;

import com.ismail.mxreflection.factory.MXFactory;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class Example1Test {

    class Example1 {

        @Arg("f1")

        String field1;

        @Arg("f2")

        int field2;

        String abc = "abc";

        @Expression(value = "f1 * sin(f2) * log2(f1 + f2) + der(cos(f1), f1) * pi + int(tan(f2), f2, 0, e)")
        double field3;

    }

    @Test

    public void example1Test() {

        Example1 example1 = new Example1();

        example1.field1 = "2.2";

        example1.field2 = 5;

        Calculator<Example1> calculator = MXFactory.createCalculator(Example1.class);

        calculator.calculate(example1);

        System.out.println("Field 3 result: " + example1.field3);

    }



    @Test
    public void test01() {
        JexlEngine jexl = new JexlBuilder().create();
        // 定义表达式
        String expression = "java.lang.Math.abs(30)";

// 执行表达式
        Object result = jexl.createExpression(expression).evaluate(null);

// 输出结果
        System.out.println(result);
    }

    @Test
    public void test02()
    {


        System.out.println(AviatorEvaluator.execute("println('hello, AviatorScript')"));
        System.out.println("-----------------------------------------------------------------");
        System.out.println("算术表达式【1+1】： " + AviatorEvaluator.execute("1+1"));
        System.out.println("逻辑表达式【1==1】： " + AviatorEvaluator.execute("1==1"));
        System.out.println("三元表达式【1==1 ? '对' : '错'】： " + AviatorEvaluator.execute("1==1 ? '对' : '错'"));

        System.out.println("正则表达式： " +  AviatorEvaluator.execute("'killme@2008gmail.com'=~/([\\w0-8]+@\\w+[\\.\\w+]+)/ ? $1:'unknow'"));
        System.out.println("-----------------------------------------------------------------");

        System.out.println("函数调用【6的3次方】： " + AviatorEvaluator.execute("math.pow(6,3)"));
        System.out.println("求字符串长度： " +       AviatorEvaluator.execute("string.length('hello')"));
        System.out.println("判断字符串是否包含字符串： " +     AviatorEvaluator.execute("string.contains('hello','h')"));
        System.out.println("是否以子串开头： " +     AviatorEvaluator.execute("math.pow(-3,2)"));
        System.out.println("求n次方： " +        AviatorEvaluator.execute("math.sqrt(14.0)"));
        System.out.println("正弦函数： " +      AviatorEvaluator.execute("math.sin(100) + 1"));

        System.out.println("-----------------------------------------------------------------");


        Map env = new HashMap(16);
        env.put("yourname","风离");
        String result3 = (String)AviatorEvaluator.execute(" 'hello ' + yourname ", env);
        System.out.println("变量和字符串相加:"+ result3);

        System.out.println("-----------------------------------------------------------------");

    }
}
