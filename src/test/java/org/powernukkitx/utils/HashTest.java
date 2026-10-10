package org.powernukkitx.utils;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

public class HashTest {
    @Test
    void hashBlockRoundTrip() {
        int[] xs = {0, 1, -1, 100, -100, 0x1FFFFFF, -0x2000000};
        int[] zs = {0, 5, -5, 200, -200, 0x1FFFFFF, -0x2000000};
        int[] ys = {0, 1, -1, 64, 320, -64};
        for (int x : xs) {
            for (int z : zs) {
                for (int y : ys) {
                    long h = Hash.hashBlock(x, y, z);
                    Assertions.assertEquals(x, Hash.hashBlockX(h), "x mismatch");
                    Assertions.assertEquals(y, Hash.hashBlockY(h), "y mismatch");
                    Assertions.assertEquals(z, Hash.hashBlockZ(h), "z mismatch");
                }
            }
        }
    }

    @Test
    void distinctPositionsDistinctHashes() {
        long a = Hash.hashBlock(1, 2, 3);
        long b = Hash.hashBlock(3, 2, 1);
        Assertions.assertNotEquals(a, b);
    }

    @Test
    void levelChunkMetaDataHashUsesNetworkNbt() throws Exception {
        NbtMap metadata = NbtMap.builder()
                .putString("DimensionName", "Overworld")
                .putLong("GenerationSeed", 123456789L)
                .putInt("GeneratorType", 1)
                .build();

        byte[] network;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             var writer = NbtUtils.createNetworkWriter(output)) {
            writer.writeTag(metadata);
            network = output.toByteArray();
        }

        byte[] littleEndian;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             var writer = NbtUtils.createWriterLE(output)) {
            writer.writeTag(metadata);
            littleEndian = output.toByteArray();
        }

        long hash = HashUtils.computeLevelChunkMetaDataHash(metadata);
        Assertions.assertEquals(HashUtils.xxh64(network, 0), hash);
        Assertions.assertNotEquals(HashUtils.xxh64(littleEndian, 0), hash);
    }
}
