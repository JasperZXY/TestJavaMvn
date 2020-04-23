package com.zxy.demo.jmh;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


/**
 * Created by zhongxianyao.
 *
 * JMH，即Java Microbenchmark Harness，是专门用于代码微基准测试的工具套件。
 * 何谓Micro Benchmark呢？简单的来说就是基于方法层面的基准测试，精度可以达到微秒级。
 * 当你定位到热点方法，希望进一步优化方法性能的时候，就可以使用JMH对优化的结果进行量化的分析。
 * 和其他竞品相比——如果有的话，JMH最有特色的地方就是，它是由Oracle内部实现JIT
 * 的那拨人开发的，对于JIT以及JVM所谓的“profile guided optimization”对基准测试准确性的影响可谓心知肚明
 *
 * https://blog.csdn.net/lxbjkben/article/details/79410740
 * http://openjdk.java.net/projects/code-tools/jmh/
 * http://hg.openjdk.java.net/code-tools/jmh
 * https://immutables.github.io/apt.html
 *
 * BenchmarkMode，使用模式，默认是Mode.Throughput，表示吞吐量,
 * 其他参数还有AverageTime，表示每次执行时间，SampleTime表示采样时间，
 * SingleShotTime表示只运行一次，用于测试冷启动消耗时间，All表示统计前面的所有指标
 * Warmup 配置预热次数，默认是每次运行1秒，运行10次
 * Measurement 配置执行次数，本例是一次运行5秒，总共运行3次。在性能对比时候，
 * 采用默认1秒即可，如果我们用jvisualvm做性能监控，我们可以指定一个较长时间运行。
 * Threads 配置同时起多少个线程执行，默认值是
 * Runtime.getRuntime().availableProcessors()
 * Fork，代表启动多个单独的进程分别测试每个方法，我们这里指定为每个方法启动一个进程。
 * OutputTimeUnit 统计结果的时间单元，这个例子TimeUnit.NANOSECONDS，我们在运行后会看到输出结果是统计每秒的吞吐量
 */
@BenchmarkMode({Mode.AverageTime, Mode.Throughput}) // 测试方法平均执行时间跟吞吐量
@OutputTimeUnit(TimeUnit.NANOSECONDS) // 输出结果的时间粒度为纳秒
@State(Scope.Thread) // 每个测试线程一个实例
public class JmhTest {

    @Benchmark
    public String stringConcat() {
        String a = "a";
        String b = "b";
        String c = "c";
        String s = a + b + c;
        return s;
    }

    public static void main(String[] args) throws RunnerException {
        // 使用一个单独进程执行测试，执行5遍warmup，然后执行5遍测试
        Options opt = new OptionsBuilder()
                .include(JmhTest.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }
}
