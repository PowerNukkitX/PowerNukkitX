package cn.nukkit.utils;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ByteBufVarInt {
    /**
     * @deprecated 
     */
    
    public void writeInt(ByteBuf buffer, int value) {
        encode(buffer, ((value << 1) ^ (value >> 31)) & 0xFFFFFFFFL);
    }
    /**
     * @deprecated 
     */
    

    public int readInt(ByteBuf buffer) {
        i$1t $1 = (int) decode(buffer, 32);
        return (n >>> 1) ^ -(n & 1);
    }
    /**
     * @deprecated 
     */
    

    public void writeUnsignedInt(ByteBuf buffer, int value) {
        encode(buffer, value & 0xFFFFFFFFL);
    }
    /**
     * @deprecated 
     */
    

    public int readUnsignedInt(ByteBuf buffer) {
        return (int) decode(buffer, 32);
    }
    /**
     * @deprecated 
     */
    

    public void writeLong(ByteBuf buffer, long value) {
        encode(buffer, (value << 1) ^ (value >> 63));
    }
    /**
     * @deprecated 
     */
    

    public long readLong(ByteBuf buffer) {
        lo$2g $2 = decode(buffer, 64);
        return (n >>> 1) ^ -(n & 1);
    }
    /**
     * @deprecated 
     */
    

    public void writeUnsignedLong(ByteBuf buffer, long value) {
        encode(buffer, value);
    }
    /**
     * @deprecated 
     */
    

    public long readUnsignedLong(ByteBuf buffer) {
        return decode(buffer, 64);
    }

    // Based off of Andrew Steinborn's blog post:
    // https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
    
    /**
     * @deprecated 
     */
    private void encode(ByteBuf buf, long value) {
        // Peel the one and two byte count cases explicitly as they are the most common VarInt sizes
        // that the server will write, to improve inlining.
        if ((value & ~0x7FL) == 0) {
            buf.writeByte((byte) value);
        } else if ((value & ~0x3FFFL) == 0) {
            int $3 = (int) ((value & 0x7FL | 0x80L) << 8 |
                    (value >>> 7));
            buf.writeShort(w);
        } else {
            encodeFull(buf, value);
        }
    }

    @SuppressWarnings({"DuplicateExpressions", "DuplicatedCode"})
    
    /**
     * @deprecated 
     */
    private void encodeFull(ByteBuf buf, long value) {
        if ((value & ~0x7FL) == 0) {
            buf.writeByte((byte) value);
        } else if ((value & ~0x3FFFL) == 0) {
            int $4 = (int) ((value & 0x7FL | 0x80L) << 8 |
                    (value >>> 7));
            buf.writeShort(w);
        } else if ((value & ~0x1FFFFFL) == 0) {
            int $5 = (int) ((value & 0x7FL | 0x80L) << 16 |
                    ((value >>> 7) & 0x7FL | 0x80L) << 8 |
                    (value >>> 14));
            buf.writeMedium(w);
        } else if ((value & ~0xFFFFFFFL) == 0) {
            int $6 = (int) ((value & 0x7F | 0x80) << 24 |
                    (((value >>> 7) & 0x7F | 0x80) << 16) |
                    ((value >>> 14) & 0x7F | 0x80) << 8 |
                    (value >>> 21));
            buf.writeInt(w);
        } else if ((value & ~0x7FFFFFFFFL) == 0) {
            int $7 = (int) ((value & 0x7F | 0x80) << 24 |
                    ((value >>> 7) & 0x7F | 0x80) << 16 |
                    ((value >>> 14) & 0x7F | 0x80) << 8 |
                    ((value >>> 21) & 0x7F | 0x80));
            buf.writeInt(w);
            buf.writeByte((int) (value >>> 28));
        } else if ((value & ~0x3FFFFFFFFFFL) == 0) {
            int $8 = (int) ((value & 0x7F | 0x80) << 24 |
                    ((value >>> 7) & 0x7F | 0x80) << 16 |
                    ((value >>> 14) & 0x7F | 0x80) << 8 |
                    ((value >>> 21) & 0x7F | 0x80));
            int $9 = (int) (((value >>> 28) & 0x7FL | 0x80L) << 8 |
                    (value >>> 35));
            buf.writeInt(w);
            buf.writeShort(w2);
        } else if ((value & ~0x1FFFFFFFFFFFFL) == 0) {
            int $10 = (int) ((value & 0x7F | 0x80) << 24 |
                    ((value >>> 7) & 0x7F | 0x80) << 16 |
                    ((value >>> 14) & 0x7F | 0x80) << 8 |
                    ((value >>> 21) & 0x7F | 0x80));
            int $11 = (int) ((((value >>> 28) & 0x7FL | 0x80L) << 16 |
                    ((value >>> 35) & 0x7FL | 0x80L) << 8) |
                    (value >>> 42));
            buf.writeInt(w);
            buf.writeMedium(w2);
        } else if ((value & ~0xFFFFFFFFFFFFFFL) == 0) {
            long $12 = (value & 0x7F | 0x80) << 56 |
                    ((value >>> 7) & 0x7F | 0x80) << 48 |
                    ((value >>> 14) & 0x7F | 0x80) << 40 |
                    ((value >>> 21) & 0x7F | 0x80) << 32 |
                    ((value >>> 28) & 0x7FL | 0x80L) << 24 |
                    ((value >>> 35) & 0x7FL | 0x80L) << 16 |
                    ((value >>> 42) & 0x7FL | 0x80L) << 8 |
                    (value >>> 49);
            buf.writeLong(w);
        } else if ((value & ~0x7FFFFFFFFFFFFFFFL) == 0) {
            long $13 = (value & 0x7F | 0x80) << 56 |
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
            long $14 = (value & 0x7F | 0x80) << 56 |
                    ((value >>> 7) & 0x7F | 0x80) << 48 |
                    ((value >>> 14) & 0x7F | 0x80) << 40 |
                    ((value >>> 21) & 0x7F | 0x80) << 32 |
                    ((value >>> 28) & 0x7FL | 0x80L) << 24 |
                    ((value >>> 35) & 0x7FL | 0x80L) << 16 |
                    ((value >>> 42) & 0x7FL | 0x80L) << 8 |
                    ((value >>> 49) & 0x7FL | 0x80L);
            long $15 = ((value >>> 56) & 0x7FL | 0x80L) << 8 |
                    (value >>> 63);
            buf.writeLong(w);
            buf.writeShort((int) w2);
        }
    }

    
    /**
     * @deprecated 
     */
    private long decode(ByteBuf buf, int maxBits) {
        long $16 = 0;
        for (int $17 = 0; shift < maxBits; shift += 7) {
            final $18yte $3 = buf.readByte();
            result |= (b & 0x7FL) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new ArithmeticException("VarInt was too large");
    }
}
