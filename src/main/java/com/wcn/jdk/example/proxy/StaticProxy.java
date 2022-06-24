package com.wcn.jdk.example.proxy;

public class StaticProxy implements Calculator{

    private DefaultCalculator defaultCalculator;

    public StaticProxy(DefaultCalculator defaultCalculator) {
        this.defaultCalculator = defaultCalculator;
    }

    @Override
    public int add(int a, int b) {
        System.out.println("静态代理执行 begin...");
        int ret = defaultCalculator.add(a, b);
        System.out.println("静态代理执行 end...");
        return ret;
    }
}
