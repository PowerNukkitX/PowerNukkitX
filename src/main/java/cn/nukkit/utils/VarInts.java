package cn.nukkit.utils;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

import java.math.BigInteger;

/**
 * Took from CloudburstMC Protocol.
 * https://github.com/CloudburstMC/Protocol/blob/3.0/common/src/main/java/org/cloudburstmc/protocol/common/util/VarInts.java
 */

@UtilityClass
public class VarInts {

    private static final BigInteger BIG_INTEGER_7F = BigInteger.valueOf(0x7f);
    private static final BigInteger BIG_INTEGER_80 = BigInteger.valueOf(0x80);

    public static void writeInt(ByteBuf buffer, int value) {
        encode(buffer, ((value << 1) ^ (value >> 31)) & 0xFFFFFFFFL);
    }

    public static int readInt(ByteBuf buffer) {
        int n = (int) decode(buffer, 32);
        return (n >>> 1) ^ -(n & 1);
    }

    public static void writeUnsignedInt(ByteBuf buffer, int value) {
        encode(buffer, value & 0xFFFFFFFFL);
    }

    public static int readUnsignedInt(ByteBuf buffer) {
        return (int) decode(buffer, 32);
    }

    public static void writeLong(ByteBuf buffer, long value) {
        encode(buffer, (value << 1) ^ (value >> 63));
    }

    public static long readLong(ByteBuf buffer) {
        long n = decode(buffer, 64);
        return (n >>> 1) ^ -(n & 1);
    }

    public static void writeUnsignedLong(ByteBuf buffer, long value) {
        encode(buffer, value);
    }

    public static long readUnsignedLong(ByteBuf buffer) {
        return decode(buffer, 64);
    }

    // Based off of Andrew Steinborn's blog post:
    // https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
    private static void encode(ByteBuf buf, long value) {
        // Peel the one and two byte count cases explicitly as they are the most common VarInt sizes
        // that the server will write, to improve inlining.
        if ((value & ~0x7FL) == 0) {
            buf.writeByte((byte) value);
        } else if ((value & ~0x3FFFL) == 0) {
            int w = (int) ((value & 0x7FL | 0x80L) << 8 |
                    (value >>> 7));
            buf.writeShort(w);
        } else {
            encodeFull(buf, value);
        }
    }

    @SuppressWarnings({"DuplicateExpressions", "DuplicatedCode"})
    private static void encodeFull(ByteBuf buf, long value) {
        if ((value & ~0x7FL) == 0) {
            buf.writeByte((byte) value);
        } else if ((value & ~0x3FFFL) == 0) {
            int w = (int) ((value & 0x7FL | 0x80L) << 8 |
                    (value >>> 7));
            buf.writeShort(w);
        } else if ((value & ~0x1FFFFFL) == 0) {
            int w = (int) ((value & 0x7FL | 0x80L) << 16 |
                    ((value >>> 7) & 0x7FL | 0x80L) << 8 |
                    (value >>> 14));
            buf.writeMedium(w);
        } else if ((value & ~0xFFFFFFFL) == 0) {
            int w = (int) ((value & 0x7F | 0x80) << 24 |
                    (((value >>> 7) & 0x7F | 0x80) << 16) |
                    ((value >>> 14) & 0x7F | 0x80) << 8 |
                    (value >>> 21));
            buf.writeInt(w);
        } else if ((value & ~0x7FFFFFFFFL) == 0) {
            int w = (int) ((value & 0x7F | 0x80) << 24 |
                    ((value >>> 7) & 0x7F | 0x80) << 16 |
                    ((value >>> 14) & 0x7F | 0x80) << 8 |
                    ((value >>> 21) & 0x7F | 0x80));
            buf.writeInt(w);
            buf.writeByte((int) (value >>> 28));
        } else if ((value & ~0x3FFFFFFFFFFL) == 0) {
            int w = (int) ((value & 0x7F | 0x80) << 24 |
                    ((value >>> 7) & 0x7F | 0x80) << 16 |
                    ((value >>> 14) & 0x7F | 0x80) << 8 |
                    ((value >>> 21) & 0x7F | 0x80));
            int w2 = (int) (((value >>> 28) & 0x7FL | 0x80L) << 8 |
                    (value >>> 35));
            buf.writeInt(w);
            buf.writeShort(w2);
        } else if ((value & ~0x1FFFFFFFFFFFFL) == 0) {
            int w = (int) ((value & 0x7F | 0x80) << 24 |
                    ((value >>> 7) & 0x7F | 0x80) << 16 |
                    ((value >>> 14) & 0x7F | 0x80) << 8 |
                    ((value >>> 21) & 0x7F | 0x80));
            int w2 = (int) ((((value >>> 28) & 0x7FL | 0x80L) << 16 |
                    ((value >>> 35) & 0x7FL | 0x80L) << 8) |
                    (value >>> 42));
            buf.writeInt(w);
            buf.writeMedium(w2);
        } else if ((value & ~0xFFFFFFFFFFFFFFL) == 0) {
            long w = (value & 0x7F | 0x80) << 56 |
                    ((value >>> 7) & 0x7F | 0x80) << 48 |
                    ((value >>> 14) & 0x7F | 0x80) << 40 |
                    ((value >>> 21) & 0x7F | 0x80) << 32 |
                    ((value >>> 28) & 0x7FL | 0x80L) << 24 |
                    ((value >>> 35) & 0x7FL | 0x80L) << 16 |
                    ((value >>> 42) & 0x7FL | 0x80L) << 8 |
                    (value >>> 49);
            buf.writeLong(w);
        } else if ((value & ~0x7FFFFFFFFFFFFFFFL) == 0) {
            long w = (value & 0x7F | 0x80) << 56 |
                    ((value >>> 7) & 0x7F | 0x80) << 48 |
                    ((value >>> 14) & 0x7F | 0x80) << 40 |
                    ((value >>> 21) & 0x7F | 0x80) << 32 |
                    ((value >>> 28) & 0x7FL | 0x80L) << 24 |
                    ((value >>> 35) & 0x7FL | 0x80L) << 16 |
                    ((value >>> 42) & 0x7FL | 0x80L) << 8 |
                    ((value >>> 49) & 0x7FL | 0x80L);
            buf.writeLong(w);
            buf.writeByte((byte) (value >>> 56));
        } else {
            long w = (value & 0x7F | 0x80) << 56 |
                    ((value >>> 7) & 0x7F | 0x80) << 48 |
                    ((value >>> 14) & 0x7F | 0x80) << 40 |
                    ((value >>> 21) & 0x7F | 0x80) << 32 |
                    ((value >>> 28) & 0x7FL | 0x80L) << 24 |
                    ((value >>> 35) & 0x7FL | 0x80L) << 16 |
                    ((value >>> 42) & 0x7FL | 0x80L) << 8 |
                    ((value >>> 49) & 0x7FL | 0x80L);
            long w2 = ((value >>> 56) & 0x7FL | 0x80L) << 8 |
                    (value >>> 63);
            buf.writeLong(w);
            buf.writeShort((int) w2);
        }
    }

    private static long decode(ByteBuf buf, int maxBits) {
        long result = 0;
        for (int shift = 0; shift < maxBits; shift += 7) {
            final byte b = buf.readByte();
            result |= (b & 0x7FL) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new ArithmeticException("VarInt was too large");
    }

    public static void writeUnsignedBigVarInt(ByteBuf buffer, BigInteger value) {
        while (true) {
            BigInteger bits = value.and(BIG_INTEGER_7F);
            value = value.shiftRight(7);
            if (value.compareTo(BigInteger.ZERO) == 0) {
                buffer.writeByte(bits.intValue());
                return;
            }
            buffer.writeByte(bits.or(BIG_INTEGER_80).intValue());
        }
    }

    public static BigInteger readUnsignedBigVarInt(ByteBuf buffer, int maxBits) {
        BigInteger value = BigInteger.ZERO;
        int shift = 0;
        while (true) {
            if (shift >= maxBits) {
                throw new ArithmeticException("VarInt was too large");
            }

            byte b = buffer.readByte();
            value = value.or(BigInteger.valueOf(b & 0x7F).shiftLeft(shift));
            if ((b & 0x80) == 0) {
                return value;
            }
            shift += 7;
        }
    }
}