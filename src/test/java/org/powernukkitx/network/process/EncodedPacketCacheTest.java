package org.powernukkitx.network.process;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetTimePacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powernukkitx.network.NetworkConstants;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class EncodedPacketCacheTest {
    private static final BedrockCodec CODEC = NetworkConstants.CODEC;

    private static byte[] encode(BedrockPacket packet) {
        ByteBuf buffer = Unpooled.buffer();
        try {
            CODEC.tryEncode(CODEC.createHelper(), buffer, packet);
            return ByteBufUtil.getBytes(buffer);
        } finally {
            buffer.release();
        }
    }

    private static SetTimePacket time(int value) {
        SetTimePacket packet = new SetTimePacket();
        packet.setTime(value);
        return packet;
    }

    @Test
    void cachedFormEncodesToTheSameBytes() {
        EncodedPacketCache cache = new EncodedPacketCache(CODEC);
        SetTimePacket packet = time(12345);

        BedrockPacket cached = cache.get(packet, null, () -> packet, helper -> {});

        UnknownPacket unknown = Assertions.assertInstanceOf(UnknownPacket.class, cached);
        Assertions.assertEquals(CODEC.getPacketDefinition(SetTimePacket.class).getId(), unknown.getPacketId());
        Assertions.assertArrayEquals(encode(packet), encode(unknown));
        Assertions.assertArrayEquals(encode(packet), encode(unknown), "encoding leaves the cached bytes intact");
        unknown.release();
    }

    @Test
    void encodesOncePerKey() {
        EncodedPacketCache cache = new EncodedPacketCache(CODEC);
        AtomicInteger created = new AtomicInteger();
        Object first = new Object();
        Object second = new Object();

        cache.get(first, null, () -> {
            created.incrementAndGet();
            return time(1);
        }, _ -> {});
        BedrockPacket again = cache.get(first, null, () -> {
            created.incrementAndGet();
            return time(1);
        }, _ -> {});
        Assertions.assertEquals(1, created.get());
        Assertions.assertArrayEquals(encode(time(1)), encode(again));

        BedrockPacket changed = cache.get(second, null, () -> {
            created.incrementAndGet();
            return time(2);
        }, _ -> {});
        Assertions.assertEquals(2, created.get());
        Assertions.assertArrayEquals(encode(time(2)), encode(changed));
    }

    @Test
    void encodesAgainWhenTheDependencyChanges() {
        EncodedPacketCache cache = new EncodedPacketCache(CODEC);
        AtomicInteger created = new AtomicInteger();
        Object key = new Object();
        Object palette = new Object();

        cache.get(key, palette, () -> {
            created.incrementAndGet();
            return time(3);
        }, _ -> {});
        cache.get(key, palette, () -> {
            created.incrementAndGet();
            return time(3);
        }, _ -> {});
        cache.get(key, new Object(), () -> {
            created.incrementAndGet();
            return time(3);
        }, _ -> {});

        Assertions.assertEquals(2, created.get());
    }

    @Test
    void identityFingerprintFollowsWhichInstancesAreHeldInWhatOrder() {
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        long base = EncodedPacketCache.identityFingerprint(List.of(a, b), List.of(c));

        Assertions.assertEquals(base, EncodedPacketCache.identityFingerprint(List.of(a, b), List.of(c)));
        Assertions.assertNotEquals(base, EncodedPacketCache.identityFingerprint(List.of(b, a), List.of(c)));
        Assertions.assertNotEquals(base, EncodedPacketCache.identityFingerprint(List.of(a, b, c), List.of()));
        Assertions.assertNotEquals(base, EncodedPacketCache.identityFingerprint(List.of(a, new Object()), List.of(c)));
        Assertions.assertNotEquals(base, EncodedPacketCache.identityFingerprint(List.of(a), List.of(c)));
    }

    @Test
    void encodesAgainWhenTheStampChanges() {
        EncodedPacketCache cache = new EncodedPacketCache(CODEC);
        AtomicInteger created = new AtomicInteger();
        Object key = new Object();

        cache.get(key, 1L, null, () -> {
            created.incrementAndGet();
            return time(4);
        }, helper -> {});
        cache.get(key, 1L, null, () -> {
            created.incrementAndGet();
            return time(4);
        }, helper -> {});
        cache.get(key, 2L, null, () -> {
            created.incrementAndGet();
            return time(4);
        }, helper -> {});

        Assertions.assertEquals(2, created.get());
    }

    @Test
    void releasingAHandedOutPacketKeepsTheCache() {
        EncodedPacketCache cache = new EncodedPacketCache(CODEC);
        Object key = new Object();
        for (int i = 0; i < 5; i++) {
            UnknownPacket packet = (UnknownPacket) cache.get(key, null, () -> time(7), helper -> {});
            packet.release();
        }
        Assertions.assertArrayEquals(encode(time(7)), encode(cache.get(key, null, () -> time(7), helper -> {})));
    }
}
