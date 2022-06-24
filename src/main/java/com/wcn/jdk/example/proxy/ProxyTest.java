package com.wcn.jdk.example.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyTest {
    public static void main(String[] args) {
        testStaticProxy();
        System.out.println("--------------------------");
        testDynamicProxy();
    }

    private static void testStaticProxy(){
        DefaultCalculator defaultCalculator = new DefaultCalculator();
        StaticProxy staticProxy = new StaticProxy(defaultCalculator);
        staticProxy.add(1,2);
    }

    private static void testDynamicProxy(){
        DefaultCalculator defaultCalculator = new DefaultCalculator();
        InvocationHandler invocationHandler = new CalculatorInvocationHandler(defaultCalculator);

        Calculator calculator = (Calculator) Proxy.newProxyInstance(DefaultCalculator.class.getClassLoader(), DefaultCalculator.class.getInterfaces(), invocationHandler);
        calculator.add(1, 2);
    }
}
