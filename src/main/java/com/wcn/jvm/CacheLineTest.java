package com.wcn.jvm;

public class CacheLineTest {

    public long l1, l2, l3, l4, l5, l6, l7;
    private volatile long num;
    public long l11, l12, l13, l14, l15, l16, l17;

    public static void main(String[] args) throws InterruptedException {
        CacheLineTest test = new CacheLineTest();
        long beginTime = System.currentTimeMillis();
        for(long i=1;i<=1000000;i++){
           test.numAdd();
        }
        System.out.println("costTime:"+(System.currentTimeMillis()-beginTime)/1000);
    }

    public void numAdd(){
        num++;
    }
}
