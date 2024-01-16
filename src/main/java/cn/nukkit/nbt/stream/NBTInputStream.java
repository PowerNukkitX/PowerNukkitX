package cn.nukkit.nbt.stream;

import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.VarInt;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@SuppressWarnings("unchecked")
public class NBTInputStream implements DataInput, AutoCloseable {
    private final DataInputStream stream;
    private final ByteOrder endianness;
    private final boolean network;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public NBTInputStream(InputStream stream) {
        this(stream, ByteOrder.BIG_ENDIAN);
    }

    public NBTInputStream(InputStream stream, ByteOrder endianness) {
        this(stream, endianness, false);
    }

    public NBTInputStream(InputStream stream, ByteOrder endianness, boolean network) {
        this.stream = stream instanceof DataInputStream ? (DataInputStream) stream : new DataInputStream(stream);
        this.endianness = endianness;
        this.network = network;
    }

    public ByteOrder getEndianness() {
        return endianness;
    }

    public boolean isNetwork() {
        return network;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.stream.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.stream.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return this.stream.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.stream.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.stream.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.stream.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        short s = this.stream.readShort();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            s = Short.reverseBytes(s);
        }
        return s;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int s = this.stream.readUnsignedShort();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            s = Integer.reverseBytes(s) >> 16;
        }
        return s;
    }

    @Override
    public char readChar() throws IOException {
        char c = this.stream.readChar();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            c = Character.reverseBytes(c);
        }
        return c;
    }

    @Override
    public int readInt() throws IOException {
        if (network) {
            return VarInt.readVarInt(this.stream);
        }
        int i = this.stream.readInt();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i);
        }
        return i;
    }

    @Override
    public long readLong() throws IOException {
        if (network) {
            return VarInt.readVarLong(this.stream);
        }
        long l = this.stream.readLong();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l);
        }
        return l;
    }

    @Override
    public float readFloat() throws IOException {
        int i = this.stream.readInt();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i);
        }
        return Float.intBitsToFloat(i);
    }

    @Override
    public double readDouble() throws IOException {
        long l = this.stream.readLong();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l);
        }
        return Double.longBitsToDouble(l);
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        return this.stream.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        int length = network ? (int) VarInt.readUnsignedVarInt(stream) : this.readUnsignedShort();
        byte[] bytes = new byte[length];
        this.stream.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public int available() throws IOException {
        return this.stream.available();
    }

    @Override
    public void close() throws IOException {
        closed.set(true);
        this.stream.close();
    }

    public Object readTag() throws IOException {
        return this.readTag(16);
    }

    public Object readTag(int maxDepth) throws IOException {
        if (this.closed.get()) {
            throw new IllegalStateException("Trying to read from a closed reader!");
        } else {
            int typeId = this.readUnsignedByte();
            this.readUTF();
            return this.deserialize(typeId, maxDepth);
        }
    }

    public <T extends Tag> T readValue(int type) throws IOException {
        return this.readValue(type, 16);
    }

    public <T extends Tag> T readValue(int type, int maxDepth) throws IOException {
        if (this.closed.get()) {
            throw new IllegalStateException("Trying to read from a closed reader!");
        } else {
            return (T) this.deserialize(type, maxDepth);
        }
    }

    private Tag deserialize(int type, int maxDepth) throws IOException {
        if (maxDepth < 0) {
            throw new IllegalArgumentException("NBT compound is too deeply nested");
        } else {
            int arraySize;
            switch (type) {
                case Tag.TAG_End:
                    return null;
                case Tag.TAG_Byte:
                    return new ByteTag(readByte());
                case Tag.TAG_Short:
                    return new ShortTag(readShort());
                case Tag.TAG_Int:
                    return new IntTag(readInt());
                case Tag.TAG_Long:
                    return new LongTag(readLong());
                case Tag.TAG_Float:
                    return new FloatTag(readFloat());
                case Tag.TAG_Double:
                    return new DoubleTag(readDouble());
                case Tag.TAG_Byte_Array:
                    arraySize = this.readInt();
                    byte[] bytes = new byte[arraySize];
                    this.readFully(bytes);
                    return new ByteArrayTag(bytes);
                case Tag.TAG_String:
                    return new StringTag(this.readUTF());
                case Tag.TAG_Compound:
                    LinkedHashMap<String, Tag> map = new LinkedHashMap<>();
                    int nbtType;
                    while ((nbtType = this.readUnsignedByte()) != Tag.TAG_End) {
                        String name = this.readUTF();
                        map.put(name, deserialize(nbtType, maxDepth - 1));
                    }
                    return new CompoundTag(map);
                case Tag.TAG_List:
                    int typeId = this.readUnsignedByte();
                    int listLength = this.readInt();
                    List<Tag> list = new ArrayList<>(listLength);

                    for (int i = 0; i < listLength; ++i) {
                        list.add(this.deserialize(typeId, maxDepth - 1));
                    }
                    return new ListTag<>(typeId, list);
                case Tag.TAG_Int_Array:
                    arraySize = this.readInt();
                    int[] ints = new int[arraySize];

                    for (int i = 0; i < arraySize; ++i) {
                        ints[i] = this.readInt();
                    }
                    return new IntArrayTag(ints);
                default:
                    throw new IllegalArgumentException("Unknown type " + type);
            }
        }
    }
}
