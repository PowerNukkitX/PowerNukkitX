package cn.powernukkitx.utils;

import cn.nukkit.utils.StringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SplitTest {
    final String data = "qqq:www:eee:zzz";
    final String data2 = "qqq:www:eee:zzz:qqq:www:eee:zzz:qqq:www:eee:zzz:qqq:www:eee:zzz:qqq:www:eee:zzz:qqq:www:eee:zzz:qqq:www:eee:zzz:qqq:www:eee:zzz:qqq:www:eee:zzz:qqq:www:eee:zzz:qqq:www:eee:zzz:qqq:www:eee:zzz";

    @Benchmark
    public void javaSplit() {
        data2.split(":");
    }

    @Benchmark
    public void pnxFastSplit() {
        StringUtils.fastSplit(":", data2);
    }

    public static void main(String[] args) throws RunnerException {
        Options option = new OptionsBuilder()
                .include(SplitTest.class.getSimpleName())
                .build();
        new Runner(option).run();
    }
}
