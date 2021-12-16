package com.wcn.jdk.example.collection.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class ArrayListTest {
    public static void main(String[] args) {
        List list = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.replaceAll(new UnaryOperator() {
            @Override
            public Object apply(Object o) {
                return (int)o * 2;
            }
        });

        list.forEach((e)->{System.out.println(e);});

        System.out.println("1234567".substring(6,7));
    }
}
