package cn.powernukkitx.deflate;

import cn.nukkit.utils.Zlib;
import cn.powernukkitx.libdeflate.CompressionType;
import cn.powernukkitx.libdeflate.LibdeflateCompressor;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.Deflater;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Threads(8)
@Fork(1)
public class ZlibSimulateJMHTest {
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
    public void javaZlib(Blackhole blackhole) throws IOException {
        Zlib.setProvider(2);
        byte[] in = data[ThreadLocalRandom.current().nextInt(data.length)];
        byte[] compressed = Zlib.deflate(in, 6);
        byte[] out = Zlib.inflate(compressed);
        blackhole.consume(out);
        blackhole.consume(compressed);
    }

    @Benchmark
    public void libdeflateZlib(Blackhole blackhole) throws IOException {
        Zlib.setProvider(3);
        byte[] in = data[ThreadLocalRandom.current().nextInt(data.length)];
        byte[] compressed = Zlib.deflate(in);
        byte[] out = Zlib.inflate(compressed);
        blackhole.consume(out);
        blackhole.consume(compressed);
    }
}
