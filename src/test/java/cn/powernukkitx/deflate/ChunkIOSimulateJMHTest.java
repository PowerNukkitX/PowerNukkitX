package cn.powernukkitx.deflate;

import cn.powernukkitx.libdeflate.CompressionType;
import cn.powernukkitx.libdeflate.LibdeflateCompressor;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.zip.Deflater;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Threads(1)
@Fork(1)
public class ChunkIOSimulateJMHTest {
    Deflater javaCompressor = new Deflater();
    LibdeflateCompressor libdeflateCompressor = new LibdeflateCompressor(6);
    ThreadLocal<LibdeflateCompressor> compressorThreadLocal = ThreadLocal.withInitial(() -> new LibdeflateCompressor(6));
    byte[] data;

    @Setup
    public void setup() {
        javaCompressor.setLevel(1);
        data = new byte[64 * 1024];
        // fill random values
        for (int i = 0; i < data.length / 4; i++) {
            data[i] = (byte) (Math.random() * 256);
        }
        for (int i = data.length / 4; i < data.length / 2; i++) {
            data[i] = (byte) 0;
        }
        for (int i = data.length / 2; i < data.length; i++) {
            data[i] = (byte) (Math.random() * 256);
        }
    }

    @Benchmark
    public void javaDeflate(Blackhole blackhole) {
        javaCompressor.reset();
        javaCompressor.setInput(data);
        javaCompressor.finish();
        byte[] compressed = new byte[64 * 1024];
        int compressedLength = javaCompressor.deflate(compressed);
        blackhole.consume(compressedLength);
        blackhole.consume(compressed);
    }

    @Benchmark
    public void libdeflateDeflate(Blackhole blackhole) {
        byte[] compressed = new byte[64 * 1024];
        int compressedLength = libdeflateCompressor.compress(data, compressed, CompressionType.DEFLATE);
        blackhole.consume(compressedLength);
        blackhole.consume(compressed);
    }

    @Benchmark
    public void libdeflateDeflateNoReuse(Blackhole blackhole) {
        byte[] compressed = new byte[64 * 1024];
        try (var compressor = new LibdeflateCompressor(6)) {
            int compressedLength = compressor.compress(data, compressed, CompressionType.DEFLATE);
            blackhole.consume(compressedLength);
            blackhole.consume(compressed);
        }
    }

    @Benchmark
    public void libdeflateDeflateThreadLocal(Blackhole blackhole) {
        byte[] compressed = new byte[64 * 1024];
        var compressor = compressorThreadLocal.get();
        int compressedLength = compressor.compress(data, compressed, CompressionType.DEFLATE);
        blackhole.consume(compressedLength);
        blackhole.consume(compressed);
    }

    @TearDown
    public void tearDown() {
        javaCompressor.end();
        libdeflateCompressor.close();
        compressorThreadLocal.get().close();
    }
}
