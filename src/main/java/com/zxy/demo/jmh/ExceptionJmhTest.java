package com.zxy.demo.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 在我其中一台机器上运行的结果
 *
 * Benchmark                             Mode  Cnt     Score    Error   Units
 * ExceptionJmhTest.exceptionMessage    thrpt   20     0.205 ±  0.007  ops/ns
 * ExceptionJmhTest.exceptionNone       thrpt   20     0.201 ±  0.003  ops/ns
 * ExceptionJmhTest.exceptionTrace      thrpt   20     0.037 ±  0.001  ops/ns
 * ExceptionJmhTest.myException         thrpt   20     0.042 ±  0.001  ops/ns
 * ExceptionJmhTest.outBoundsException  thrpt   20     0.001 ±  0.001  ops/ns
 * ExceptionJmhTest.simple              thrpt   20     0.377 ±  0.004  ops/ns
 * ExceptionJmhTest.exceptionMessage     avgt   20     4.812 ±  0.107   ns/op
 * ExceptionJmhTest.exceptionNone        avgt   20     4.798 ±  0.096   ns/op
 * ExceptionJmhTest.exceptionTrace       avgt   20    27.512 ±  0.216   ns/op
 * ExceptionJmhTest.myException          avgt   20    23.809 ±  0.337   ns/op
 * ExceptionJmhTest.outBoundsException   avgt   20  1154.585 ± 31.224   ns/op
 * ExceptionJmhTest.simple               avgt   20     2.648 ±  0.032   ns/op
 *
 *
 * 数据分析：
 * 抛异常的情况下，是正常不抛异常的0.55倍吞吐率（约为1.8的倒数）
 * 抛异常并且获取异常堆栈信息，是正常不抛异常的0.096倍吞吐率（约为10.39的倒数）
 * 这段代码的逻辑比较简单，上面的倍数可能不是很能说明问题，
 * 比较一下异常情况下‘不获取异常堆栈’是‘获取异常堆栈’的5.7倍吞吐率
 *
 * 所以，抛异常的情况下，减少getStackTrace()的调用；自定义业务异常，可以考虑重写fillInStackTrace()
 *
 * 同样的异常，自己new出来的性能比系统抛出来的差这么多，还需要再研究一下
 */
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations=5)
public class ExceptionJmhTest {

    private static final int[] NUMS = {1, 3, 5};

    private static Object tmp = null;

    @Benchmark
    public int simple() {
        return NUMS[1];
    }

    @Benchmark
    public int exceptionMessage() {
        try {
            return NUMS[10];
        } catch (ArrayIndexOutOfBoundsException e) {
            tmp = e.getMessage();
        }
        return -1;
    }

    @Benchmark
    public int exceptionNone() {
        try {
            return NUMS[10];
        } catch (ArrayIndexOutOfBoundsException e) {
            tmp = e.getMessage();
        }
        return -1;
    }

    @Benchmark
    public int exceptionTrace() {
        try {
            return NUMS[10];
        } catch (ArrayIndexOutOfBoundsException e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            tmp = stackTrace;
        }
        return -1;
    }

    @Benchmark
    public int myException() {
        try {
            throw new MyExcection("xxx");
        } catch (MyExcection e) {
            tmp = e.getMessage();
        }
        return -1;
    }

    @Benchmark
    public int outBoundsException() {
        try {
            throw new ArrayIndexOutOfBoundsException("xxx");
        } catch (ArrayIndexOutOfBoundsException e) {
            tmp = e.getMessage();
        }
        return -1;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ExceptionJmhTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

    public static class MyExcection extends Exception {
        public MyExcection() {
        }

        public MyExcection(String message) {
            super(message);
        }

        public MyExcection(String message, Throwable cause) {
            super(message, cause);
        }

        public MyExcection(Throwable cause) {
            super(cause);
        }

        // 强制不构造 Stack Trace 信息
        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}
