package com.wcn.jdk.example.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CalculatorInvocationHandler implements InvocationHandler {
    private DefaultCalculator defaultCalculator;

    public CalculatorInvocationHandler(DefaultCalculator defaultCalculator) {
        this.defaultCalculator = defaultCalculator;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("执行动态代理逻辑...");
        System.out.println(proxy);
        return method.invoke(defaultCalculator, args);
    }
}
