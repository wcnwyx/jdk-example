package com.wcn.jdk.example.proxy;

public class DefaultCalculator implements Calculator{
    @Override
    public int add(int a, int b) {
        System.out.println("原始对象执行...");
        return a+b;
    }
}
