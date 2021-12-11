package com.wcn.jdk.example.collection.map;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class LinkedHashMapTest {
    public static void main(String[] args) {
//        LinkedHashMap map = new LinkedHashMap();
//        map.put(1, 1);
//        map.put(2, 2);

//        int tableSize = 16;
//        for(Float key = 90f;key<100f;key++){
//            int hashTableIndex = (key.hashCode() & 0x7FFFFFFF) % tableSize;
//            int h;
//            int hashMapIndex = ((h = key.hashCode()) ^ (h >>> 16))& (tableSize-1);
//            System.out.println("key:"+key+" hash:"+key.hashCode()+" hashTableIndex:"+hashTableIndex+" hashMapIndex:"+hashMapIndex);
//        }

        System.out.println("aaaa");
        ConcurrentHashMap map = new ConcurrentHashMap();
        map.put("1", "1");

    }
}
