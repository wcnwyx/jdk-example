package com.wcn.jdk.example.cache;

/**
 * 合并写操作
 */
public class WriteCombiningTest {
    private static final int ITERATIONS = Integer.MAX_VALUE;
    private static final int ITEMS = 1<<24;
    private static final int MASK = ITEMS - 1;
    private static final byte[] array1 = new byte[ITEMS];
    private static final byte[] array2 = new byte[ITEMS];
    private static final byte[] array3 = new byte[ITEMS];
    private static final byte[] array4 = new byte[ITEMS];
    private static final byte[] array5 = new byte[ITEMS];
    private static final byte[] array6 = new byte[ITEMS];

    public static void main(String[] args) {
        for(int i=0;i<3;i++){
            System.out.println(i + " SingleLoop costTime = " + runCaseOne());
            System.out.println(i + " SplitLoop costTime = " + runCaseTwo());
        }
    }

    private static long runCaseOne(){
        long beginTime = System.currentTimeMillis();
        int i = ITERATIONS;
        while(--i != 0){
            int slot = i & MASK;
            byte b = (byte)i;
            array1[slot] = b;
            array2[slot] = b;
            array3[slot] = b;
            array4[slot] = b;
            array5[slot] = b;
            array6[slot] = b;
        }
        return System.currentTimeMillis() - beginTime;
    }

    private static long runCaseTwo(){
        long beginTime = System.currentTimeMillis();
        int i = ITERATIONS;
        while(--i != 0){
            int slot = i & MASK;
            byte b = (byte)i;
            array1[slot] = b;
            array2[slot] = b;
            array3[slot] = b;
        }

        i = ITERATIONS;
        while(--i != 0){
            int slot = i & MASK;
            byte b = (byte)i;
            array4[slot] = b;
            array5[slot] = b;
            array6[slot] = b;
        }
        return System.currentTimeMillis() - beginTime;
    }
}
