package cn.nukkit.nbt.stream;

import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.LittleEndianByteBufInputStream;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class LittleEndianByteBufInputStreamNBTInputStream implements DataInput, AutoCloseable {
    private LittleEndianByteBufInputStream stream;

    public LittleEndianByteBufInputStreamNBTInputStream(LittleEndianByteBufInputStream stream) {
        this.stream = stream;
    }

    @Override
    public String readUTF() throws IOException {
        return stream.readUTF();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        stream.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        stream.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return stream.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return stream.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return stream.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return stream.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return stream.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return stream.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return stream.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return stream.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return stream.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return stream.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return stream.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return stream.readLine();
    }

    public Object readTag() throws IOException {
        return this.readTag(16);
    }

    public Object readTag(int maxDepth) throws IOException {
        if (stream.available() <= 0) {
            throw new IllegalStateException("Trying to read from a closed reader!");
        } else {
            int typeId = this.readUnsignedByte();
            this.readUTF();
            return this.deserialize(typeId, maxDepth);
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
