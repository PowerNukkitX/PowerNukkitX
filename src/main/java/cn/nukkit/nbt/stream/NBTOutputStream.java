package cn.nukkit.nbt.stream;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.VarInt;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class NBTOutputStream implements DataOutput, AutoCloseable {
    private final DataOutputStream stream;
    private final ByteOrder endianness;
    private final boolean network;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public NBTOutputStream(OutputStream stream) {
        this(stream, ByteOrder.BIG_ENDIAN);
    }

    public NBTOutputStream(OutputStream stream, ByteOrder endianness) {
        this(stream, endianness, false);
    }

    public NBTOutputStream(OutputStream stream, ByteOrder endianness, boolean network) {
        this.stream = stream instanceof DataOutputStream ? (DataOutputStream) stream : new DataOutputStream(stream);
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
    public void write(byte[] bytes) throws IOException {
        this.stream.write(bytes);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.stream.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.stream.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.stream.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v) >> 16;
        }
        this.stream.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Character.reverseBytes((char) v);
        }
        this.stream.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        if (network) {
            VarInt.writeVarInt(this.stream, v);
        } else {
            if (endianness == ByteOrder.LITTLE_ENDIAN) {
                v = Integer.reverseBytes(v);
            }
            this.stream.writeInt(v);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        if (network) {
            VarInt.writeVarLong(this.stream, v);
        } else {
            if (endianness == ByteOrder.LITTLE_ENDIAN) {
                v = Long.reverseBytes(v);
            }
            this.stream.writeLong(v);
        }
    }

    @Override
    public void writeFloat(float v) throws IOException {
        int i = Float.floatToIntBits(v);
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i);
        }
        this.stream.writeInt(i);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        long l = Double.doubleToLongBits(v);
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l);
        }
        this.stream.writeLong(l);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.stream.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        this.stream.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (network) {
            VarInt.writeUnsignedVarInt(stream, bytes.length);
        } else {
            this.writeShort(bytes.length);
        }
        this.stream.write(bytes);
    }

    @Override
    public void close() throws IOException {
        this.closed.set(true);
        this.stream.close();
    }

    public void writeTag(Tag tag) throws IOException {
        this.writeTag(tag, 16);
    }

    public void writeTag(Tag tag, int maxDepth) throws IOException {
        Objects.requireNonNull(tag, "tag");
        if (this.closed.get()) {
            throw new IllegalStateException("closed");
        } else {
            int type = tag.getId();
            this.writeByte(type);
            this.writeUTF("");
            this.serialize(tag, type, maxDepth);
        }
    }

    public void writeValue(Tag tag) throws IOException {
        this.writeValue(tag, 16);
    }

    public void writeValue(Tag tag, int maxDepth) throws IOException {
        Objects.requireNonNull(tag, "tag");
        if (this.closed.get()) {
            throw new IllegalStateException("closed");
        } else {
            this.serialize(tag, tag.getId(), maxDepth);
        }
    }

    private void serialize(Tag tag, int type, int maxDepth) throws IOException {
        if (maxDepth < 0) {
            throw new IllegalArgumentException("Reached depth limit");
        } else {
            switch (type) {
                case Tag.TAG_Byte -> this.writeByte(tag.parseValue());
                case Tag.TAG_Short -> this.writeShort(tag.parseValue());
                case Tag.TAG_Int -> this.writeInt(tag.parseValue());
                case Tag.TAG_Long -> this.writeLong(tag.parseValue());
                case Tag.TAG_Float -> this.writeFloat(tag.parseValue());
                case Tag.TAG_Double -> this.writeDouble(tag.parseValue());
                case Tag.TAG_Byte_Array -> {
                    byte[] byteArray = tag.parseValue();
                    this.writeInt(byteArray.length);
                    this.write(byteArray);
                }
                case Tag.TAG_Int_Array -> {
                    int[] intArray = tag.parseValue();
                    this.writeInt(intArray.length);
                    for(int i : intArray) {
                        this.writeInt(i);
                    }
                }
                case Tag.TAG_String -> {
                    String string = tag.parseValue();
                    this.writeUTF(string);
                }
                case Tag.TAG_Compound -> {
                    CompoundTag map = (CompoundTag) tag;
                    for (var e : map.getTags().entrySet()) {
                        this.writeByte(e.getValue().getId());
                        this.writeUTF(e.getKey());
                        this.serialize(e.getValue(), e.getValue().getId(), maxDepth - 1);
                    }
                    this.writeByte(0);// End tag
                }
                case Tag.TAG_List -> {
                    ListTag<? extends Tag> list = (ListTag<? extends Tag>) tag;
                    this.writeByte(list.type);
                    this.writeInt(list.size());
                    for (var t : list.getAll()) {
                        this.serialize(t, list.type, maxDepth - 1);
                    }
                }
                default -> {
                    throw new IllegalArgumentException("Cannot write Tag of Class " + tag.getClass().getSimpleName() + " type: " + type);
                }
            }
        }
    }
}
