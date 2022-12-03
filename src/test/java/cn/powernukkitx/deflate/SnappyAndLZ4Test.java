package cn.powernukkitx.deflate;

import cn.nukkit.utils.Zlib;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.compression.Snappy;
import net.jpountz.lz4.LZ4Factory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Threads(1)
@Fork(1)
public class SnappyAndLZ4Test {
    byte[][] data;
    static final int DATA_SIZE = 4096;

    LZ4Factory factory = LZ4Factory.fastestInstance();

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
    @Threads(8)
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

    @Benchmark
    @Threads(8)
    public void lz4Java(Blackhole blackhole) {
        var compressor = factory.fastCompressor();
        int maxCompressedLength = compressor.maxCompressedLength(DATA_SIZE);
        byte[] compressed = new byte[maxCompressedLength];
        blackhole.consume(compressor.compress(this.data[ThreadLocalRandom.current().nextInt(this.data.length)], 0, DATA_SIZE, compressed, 0, maxCompressedLength));
    }
}
