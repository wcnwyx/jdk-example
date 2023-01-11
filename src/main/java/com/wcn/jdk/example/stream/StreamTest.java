package com.wcn.jdk.example.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamTest {
    public static void main(String[] args) {
        testCreate();
        testFilter();
        testMap();
        testMapToDouble();
        testFlatMap();
        testDistinct();
        teatSorted();
        testPeek();
        testLimit();
        testSkip();
        testForEach();
        testToArray();
        testReduce();
    }

    public static void testCreate(){
        System.out.println("testCreate begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3,4,5);

        int[] array = new int[]{1,2,3,4,5};
        IntStream intStream = Arrays.stream(array);

        List<Integer> list = new ArrayList();
        list.add(1);
        Stream stream1 = list.stream();
        System.out.println("testCreate end-----------------------");
        System.out.println();
    }

    /**
     * 利用Predicate过滤元素并生成新的流
     */
    public static void testFilter(){
        System.out.println("testFilter begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3,4,5);
        Stream stream1 = stream.filter(integer -> integer%2==0);

        System.out.println("stream:"+stream);
        System.out.println("stream1:"+stream1);
        stream1.forEach(System.out::println);
        System.out.println("testFilter end-----------------------");
        System.out.println();
    }

    /**
     * 使用Function 来映射流中的元素，并返回新的流
     */
    public static void testMap(){
        System.out.println("testMap begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3,4,5);
        stream.map(integer -> integer*2).forEach(System.out::println);
        System.out.println("testMap end-----------------------");
        System.out.println();
    }

    /**
     * 固定的是返回了一个DoubleStream，通过Function将流元素转为Double
     */
    public static void testMapToDouble(){
        System.out.println("testMapToDouble begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3,4,5);
        stream.mapToDouble(integer -> integer*2).forEach(System.out::println);
        System.out.println("testMapToDouble end-----------------------");
        System.out.println();
    }

    /**
     * 将stream中的每个元素，通过Function函数转换为一个Steam，再将所有元素生成的多个stream合并返回
     * 示例中，将原stream中的3个元素("1,2","3,4","5,6")，通过 Arrays.stream(s.split(","))代发生成3个新的stream，
     * 再将这三个新的stream合并到一起返回
     */
    public static void testFlatMap(){
        System.out.println("testFlatMap begin-----------------------");
        Stream<String> stream = Stream.of("1,2","3,4","5,6");
        stream.flatMap(s -> Arrays.stream(s.split(","))).forEach(System.out::println);
        System.out.println("testFlatMap end-----------------------");
        System.out.println();
    }

    /**
     * 将原stream中的元素去重并返回一个新的stream
     */
    public static void testDistinct(){
        System.out.println("testDistinct begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3,1,2);
        stream.distinct().forEach(System.out::println);
        System.out.println("testDistinct end-----------------------");
        System.out.println();
    }


    /**
     * 将原stream中的元素排序并返回到一个新的stream
     */
    public static void teatSorted(){
        System.out.println("testDistinct begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3,1,2);
        stream.sorted().forEach(System.out::println);
        System.out.println("testDistinct end-----------------------");
        System.out.println();
    }

    /**
     * 给流中的每个元素添加一个额外的操作（Consumer）
     */
    public static void testPeek(){
        System.out.println("testPeek begin-----------------------");
        Stream.of("one", "two", "three", "four")
                .peek(e -> System.out.println("peek1 value: "+ e))
                .filter(e -> e.length() > 3)
                .peek(e -> System.out.println("peek2 value: " + e))
                .filter(e -> e.length()>4)
                .peek(e -> System.out.println("peek3 value: " + e))
                .forEach(System.out::println);
        System.out.println("testPeek end-----------------------");
        System.out.println();
    }

    /**
     * 从原始stream中取出limit个元素放到新的流中并返回
     */
    public static void testLimit(){
        System.out.println("testLimit begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3,4,5);
        stream.limit(3).forEach(System.out::println);
        System.out.println("testLimit end-----------------------");
        System.out.println();
    }

    /**
     * 跳过几个元素，将剩余的元素放到新流中，并返回新流
     */
    public static void testSkip(){
        System.out.println("testSkip begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3,4,5);
        stream.skip(3).forEach(System.out::println);
        System.out.println("testSkip end-----------------------");
        System.out.println();
    }

    /**
     * 将stream中的每个元素，执行Consumer操作
     */
    public static void testForEach(){
        System.out.println("testForEach begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3);
        stream.forEach(System.out::println);
        System.out.println("testForEach end-----------------------");
        System.out.println();
    }

    /**
     * 将流中的元素包装到array中返回
     */
    public static void testToArray(){
        System.out.println("testToArray begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3);
        Object[] objects = stream.toArray();
        for(Object obj:objects){
            System.out.println("obj:"+obj);
        }

        stream = Stream.of(1,2,3);
        Integer[] array = stream.toArray(Integer[]::new);
        for(Integer i:array){
            System.out.println("int:"+i);
        }
        System.out.println("testToArray end-----------------------");
        System.out.println();
    }

    /**
     * 规约函数（reduce），使用累加器（BinaryOperator<T> accumulator） 进行规约计算
     */
    public static void testReduce(){
        testReduce1();
        testReduce2();
    }

    /**
     * 规约函数（reduce），使用累加器（BinaryOperator<T> accumulator） 进行规约计算
     */
    public static void testReduce1(){
        System.out.println("testReduce1 begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3);
        //将stream中所有元素求和
        int result = stream.reduce(0, (x,y)->x+y);
        System.out.println("result:"+result);

        //计算stream中元素的个数
        stream = Stream.of(1,2,3);
        int num = stream.reduce(0, (x,y)-> x+1);
        System.out.println("num:"+num);

        //找出stream中最大的元素
        stream = Stream.of(1,2,3,0);
        int max = stream.reduce(0, Math::max);
        System.out.println("max:"+max);

        System.out.println("testReduce1 end-----------------------");
        System.out.println();
    }

    /**
     * 和testReduce1一样的逻辑，是指使用了不带identity参数的reduce函数
     */
    public static void testReduce2(){
        System.out.println("testReduce1 begin-----------------------");
        Stream<Integer> stream = Stream.of(1,2,3);
        //将stream中所有元素求和
        Optional<Integer> optionalInteger = stream.reduce((x, y)->x+y);
        System.out.println("result:"+optionalInteger.get());

        //计算stream中元素的个数
        stream = Stream.of(1,2,3);
        Optional<Integer> optionalInteger1 = stream.reduce((x,y)-> x+1);
        System.out.println("num:"+optionalInteger1.get());

        //找出stream中最大的元素
        stream = Stream.of(1,2,3,0);
        Optional<Integer> optionalInteger2 = stream.reduce(Math::max);
        System.out.println("max:"+optionalInteger2.get());

        System.out.println("testReduce1 end-----------------------");
        System.out.println();
    }
}
