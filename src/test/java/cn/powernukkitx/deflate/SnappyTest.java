package cn.powernukkitx.deflate;

import cn.nukkit.utils.Zlib;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.compression.Snappy;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Threads(1)
@Fork(1)
public class SnappyTest {
    byte[][] data;
    static final int DATA_SIZE = 256;

    @Setup
    public void setup() {
        data = new byte[16384][DATA_SIZE];
        // fill random values
        for (int i = 0; i < data.length; i++) {
            data[i] = new byte[DATA_SIZE];
            for (int j = 0; j < DATA_SIZE; j++) {
                data[i][j] = (byte) ((Math.random() - 0.5) * 128);
            }
        }
    }

    @Benchmark
    public void snappyJava() {
        var data = this.data[ThreadLocalRandom.current().nextInt(this.data.length)];
        var snappy = new Snappy();
        var inBuf = ByteBufAllocator.DEFAULT.buffer(data.length);
        inBuf.writeBytes(data);
        var outBuf = ByteBufAllocator.DEFAULT.buffer(data.length);
        snappy.encode(inBuf, outBuf, data.length);
        inBuf.release();
        outBuf.release();
    }

    @Benchmark
    public void gzipJava(Blackhole blackhole) throws IOException {
        Zlib.setProvider(2);
        var data = this.data[ThreadLocalRandom.current().nextInt(this.data.length)];
        blackhole.consume(Zlib.deflate(data, 6));
    }
}
