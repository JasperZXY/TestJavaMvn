package com.zxy.demo.apache;

import org.apache.lucene.util.RamUsageEstimator;

/**
 * 运行结果
 * Object:16 bytes
 * A:32 bytes
 * B:16 bytes
 * C:40 bytes
 * D:24 bytes
 * E:72 bytes
 * long[]:40 bytes
 * int[]:32 bytes
 * Integer[]:80 bytes
 * short[]:24 bytes
 * byte[]:24 bytes
 * Byte:16 bytes
 * Integer:16 bytes
 * Integer_new:16 bytes
 * Long:24 bytes
 * Double:24 bytes
 */
public class ObjectSize {

    public static void main(String[] args) {
        System.out.println("Object:" + RamUsageEstimator.humanSizeOf(new Object()));
        System.out.println("A:" + RamUsageEstimator.humanSizeOf(new A()));
        System.out.println("B:" + RamUsageEstimator.humanSizeOf(new B()));
        System.out.println("C:" + RamUsageEstimator.humanSizeOf(new C()));
        System.out.println("D:" + RamUsageEstimator.humanSizeOf(new D()));
        System.out.println("E:" + RamUsageEstimator.humanSizeOf(new E()));

        long[] longs = {1,2,3};
        System.out.println("long[]:" + RamUsageEstimator.humanSizeOf(longs));
        int[] ints = {1,2,3};
        System.out.println("int[]:" + RamUsageEstimator.humanSizeOf(ints));
        Integer[] integers = {1,2,3};
        System.out.println("Integer[]:" + RamUsageEstimator.humanSizeOf(integers));
        short[] shorts = {1,2,3};
        System.out.println("short[]:" + RamUsageEstimator.humanSizeOf(shorts));
        byte[] bytes = {1,2,3};
        System.out.println("byte[]:" + RamUsageEstimator.humanSizeOf(bytes));

        System.out.println("Byte:" + RamUsageEstimator.humanSizeOf(Byte.valueOf("1")));
        System.out.println("Integer:" + RamUsageEstimator.humanSizeOf(Integer.valueOf(1)));
        System.out.println("Integer_new:" + RamUsageEstimator.humanSizeOf(new Integer(1)));
        System.out.println("Long:" + RamUsageEstimator.humanSizeOf(Long.valueOf(1)));
        System.out.println("Double:" + RamUsageEstimator.humanSizeOf(Double.valueOf(1)));
    }

    private static class A {
        private Integer value = 1;
    }

    private static class B {
        private int value = 1;
    }

    private static class C {
        private Long value = 1L;
    }

    private static class D {
        private long value = 1L;
    }

    private static class E {
        private int[] arr = {0,1,2,3,4,5,6,7,8,9};
    }

}
