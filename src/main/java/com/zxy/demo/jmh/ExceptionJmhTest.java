package com.zxy.demo.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

/**
 * 在我其中一台机器上运行的结果
 *
 * Benchmark                           Mode  Cnt      Score      Error   Units
 * ExceptionJmhTest.exceptionMessage  thrpt   20      0.209 ±    0.004  ops/ns
 * ExceptionJmhTest.exceptionNone     thrpt   20      0.395 ±    0.004  ops/ns
 * ExceptionJmhTest.exceptionTrace    thrpt   20      0.037 ±    0.001  ops/ns
 * ExceptionJmhTest.fileNotFound      thrpt   20      0.001 ±    0.001  ops/ns
 * ExceptionJmhTest.fileNotFoundS     thrpt   20     ≈ 10⁻⁴             ops/ns
 * ExceptionJmhTest.myException1      thrpt   20      0.001 ±    0.001  ops/ns
 * ExceptionJmhTest.myException2      thrpt   20      0.042 ±    0.001  ops/ns
 * ExceptionJmhTest.newObj            thrpt   20      0.242 ±    0.004  ops/ns
 * ExceptionJmhTest.npe               thrpt   20      0.001 ±    0.001  ops/ns
 * ExceptionJmhTest.simple            thrpt   20      0.343 ±    0.005  ops/ns
 * ExceptionJmhTest.exceptionMessage   avgt   20      4.755 ±    0.038   ns/op
 * ExceptionJmhTest.exceptionNone      avgt   20      2.540 ±    0.037   ns/op
 * ExceptionJmhTest.exceptionTrace     avgt   20     28.009 ±    2.348   ns/op
 * ExceptionJmhTest.fileNotFound       avgt   20   1112.427 ±  130.442   ns/op
 * ExceptionJmhTest.fileNotFoundS      avgt   20  22752.737 ± 1533.800   ns/op
 * ExceptionJmhTest.myException1       avgt   20   1061.222 ±   30.239   ns/op
 * ExceptionJmhTest.myException2       avgt   20     24.128 ±    0.453   ns/op
 * ExceptionJmhTest.newObj             avgt   20      4.360 ±    0.210   ns/op
 * ExceptionJmhTest.npe                avgt   20   1189.087 ±   87.677   ns/op
 * ExceptionJmhTest.simple             avgt   20      2.881 ±    0.027   ns/op
 *
 * 结论:
 * 能不用异常的不要用异常；
 * 抛异常的情况下，减少getStackTrace()的调用；
 * 自定义业务异常，可以考虑重写fillInStackTrace()
 *
 * 初始化一个NPE比系统抛出一个NPE，性能还差，可能是JVM底层对一些比较常见的异常做了优化
 */
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations=5)
public class ExceptionJmhTest {

    private static final Object NUL = null;
    private static final Object OBJ = new Object();

    private static Object exceptionMsg = null;

    @Benchmark
    public int simple() {
        return OBJ.hashCode();
    }

    @Benchmark
    public int exceptionMessage() {
        try {
            return NUL.hashCode();
        } catch (NullPointerException e) {
            exceptionMsg = e.getMessage();
        }
        return 0;
    }

    @Benchmark
    public int exceptionNone() {
        try {
            return NUL.hashCode();
        } catch (NullPointerException e) {

        }
        return 0;
    }

    @Benchmark
    public int exceptionTrace() {
        try {
            return NUL.hashCode();
        } catch (NullPointerException e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            exceptionMsg = stackTrace;
        }
        return 0;
    }

    @Benchmark
    public String myException1() {
        try {
            throw new MyExcection1("xxx");
        } catch (MyExcection1 e) {
            exceptionMsg = e.getMessage();
        }
        return null;
    }

    @Benchmark
    public String myException2() {
        try {
            throw new MyExcection2("xxx");
        } catch (MyExcection2 e) {
            exceptionMsg = e.getMessage();
        }
        return null;
    }

    @Benchmark
    public String npe() {
        try {
            throw new NullPointerException("xxx");
        } catch (NullPointerException e) {
            exceptionMsg = e.getMessage();
        }
        return null;
    }

    @Benchmark
    public String fileNotFound() {
        try {
            throw new FileNotFoundException("file_not_found");
        } catch (FileNotFoundException e) {
            exceptionMsg = e.getMessage();
        }
        return null;
    }

    @Benchmark
    public String fileNotFoundS() {
        try {
            new FileInputStream("file_not_found").toString();
        } catch (FileNotFoundException e) {
            exceptionMsg = e.getMessage();
        }
        return null;
    }

    @Benchmark
    public Object newObj() {
        return new Object();
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ExceptionJmhTest.class.getSimpleName())
                .build();
        new Runner(opt).run();

        System.out.println("exceptionMsg:" + exceptionMsg);
    }

    public static class MyExcection1 extends Exception {
        public MyExcection1() {
        }

        public MyExcection1(String message) {
            super(message);
        }

        public MyExcection1(String message, Throwable cause) {
            super(message, cause);
        }

        public MyExcection1(Throwable cause) {
            super(cause);
        }
    }

    public static class MyExcection2 extends Exception {
        public MyExcection2() {
        }

        public MyExcection2(String message) {
            super(message);
        }

        public MyExcection2(String message, Throwable cause) {
            super(message, cause);
        }

        public MyExcection2(Throwable cause) {
            super(cause);
        }
        // 强制不构造 Stack Trace 信息
        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}
