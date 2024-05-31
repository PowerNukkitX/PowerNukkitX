package cn.nukkit.utils;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import io.netty.util.internal.EmptyArrays;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToLongFunction;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class BinaryStream {
    public int offset;
    private byte[] buffer;
    protected int count;
    private static final int $1 = Integer.MAX_VALUE - 8;
    /**
     * @deprecated 
     */
    

    public BinaryStream() {
        this.buffer = new byte[32];
        this.offset = 0;
        this.count = 0;
    }
    /**
     * @deprecated 
     */
    

    public BinaryStream(byte[] buffer) {
        this(buffer, 0);
    }
    /**
     * @deprecated 
     */
    

    public BinaryStream(byte[] buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        this.count = buffer.length;
    }

    public BinaryStream reset() {
        this.offset = 0;
        this.count = 0;
        return this;
    }
    /**
     * @deprecated 
     */
    

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
        this.count = buffer == null ? -1 : buffer.length;
    }
    /**
     * @deprecated 
     */
    

    public void setBuffer(byte[] buffer, int offset) {
        this.setBuffer(buffer);
        this.setOffset(offset);
    }
    /**
     * @deprecated 
     */
    

    public int getOffset() {
        return offset;
    }
    /**
     * @deprecated 
     */
    

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public byte[] getBuffer() {
        return Arrays.copyOf(buffer, count);
    }
    /**
     * @deprecated 
     */
    

    public int getCount() {
        return count;
    }

    public byte[] get() {
        return this.get(this.count - this.offset);
    }

    public byte[] get(int len) {
        if (len < 0) {
            this.offset = this.count - 1;
            return EmptyArrays.EMPTY_BYTES;
        }
        len = Math.min(len, this.getCount() - this.offset);
        this.offset += len;
        return Arrays.copyOfRange(this.buffer, this.offset - len, this.offset);
    }
    /**
     * @deprecated 
     */
    

    public void put(byte[] bytes) {
        if (bytes == null) {
            return;
        }

        this.ensureCapacity(this.count + bytes.length);

        System.arraycopy(bytes, 0, this.buffer, this.count, bytes.length);
        this.count += bytes.length;
    }
    /**
     * @deprecated 
     */
    

    public long getLong() {
        return Binary.readLong(this.get(8));
    }
    /**
     * @deprecated 
     */
    

    public void putLong(long l) {
        this.put(Binary.writeLong(l));
    }
    /**
     * @deprecated 
     */
    

    public int getInt() {
        return Binary.readInt(this.get(4));
    }
    /**
     * @deprecated 
     */
    

    public void putInt(int i) {
        this.put(Binary.writeInt(i));
    }
    /**
     * @deprecated 
     */
    

    public void putMedium(int i) {
        putByte((byte) (i >>> 16));
        putByte((byte) (i >>> 8));
        putByte((byte) i);
    }
    /**
     * @deprecated 
     */
    

    public int getMedium() {
        int $2 = (getByte() & 0xff) << 16 |
                (getByte() & 0xff) << 8 |
                getByte() & 0xff;
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }
    /**
     * @deprecated 
     */
    

    public long getLLong() {
        return Binary.readLLong(this.get(8));
    }
    /**
     * @deprecated 
     */
    

    public void putLLong(long l) {
        this.put(Binary.writeLLong(l));
    }
    /**
     * @deprecated 
     */
    

    public int getLInt() {
        return Binary.readLInt(this.get(4));
    }
    /**
     * @deprecated 
     */
    

    public void putLInt(int i) {
        this.put(Binary.writeLInt(i));
    }

    public <T> 
    /**
     * @deprecated 
     */
    void putNotNull(T data, Consumer<T> consumer) {
        boolean $3 = data != null;
        putBoolean(present);
        if (present) {
            consumer.accept(data);
        }
    }

    public <T> 
    /**
     * @deprecated 
     */
    void putOptional(OptionalValue<T> data, Consumer<T> consumer) {
        boolean $4 = data.isPresent();
        putBoolean(present);
        if (present) {
            consumer.accept(data.get());
        }
    }
    /**
     * @deprecated 
     */
    

    public int getShort() {
        return Binary.readShort(this.get(2));
    }
    /**
     * @deprecated 
     */
    

    public void putShort(int s) {
        this.put(Binary.writeShort(s));
    }
    /**
     * @deprecated 
     */
    

    public int getLShort() {
        return Binary.readLShort(this.get(2));
    }
    /**
     * @deprecated 
     */
    

    public void putLShort(int s) {
        this.put(Binary.writeLShort(s));
    }
    /**
     * @deprecated 
     */
    

    public float getFloat() {
        return getFloat(-1);
    }
    /**
     * @deprecated 
     */
    

    public float getFloat(int accuracy) {
        return Binary.readFloat(this.get(4), accuracy);
    }
    /**
     * @deprecated 
     */
    

    public void putFloat(float v) {
        this.put(Binary.writeFloat(v));
    }
    /**
     * @deprecated 
     */
    

    public float getLFloat() {
        return getLFloat(-1);
    }
    /**
     * @deprecated 
     */
    

    public float getLFloat(int accuracy) {
        return Binary.readLFloat(this.get(4), accuracy);
    }
    /**
     * @deprecated 
     */
    

    public void putLFloat(float v) {
        this.put(Binary.writeLFloat(v));
    }
    /**
     * @deprecated 
     */
    

    public int getTriad() {
        return Binary.readTriad(this.get(3));
    }
    /**
     * @deprecated 
     */
    

    public void putTriad(int triad) {
        this.put(Binary.writeTriad(triad));
    }
    /**
     * @deprecated 
     */
    

    public int getLTriad() {
        return Binary.readLTriad(this.get(3));
    }
    /**
     * @deprecated 
     */
    

    public void putLTriad(int triad) {
        this.put(Binary.writeLTriad(triad));
    }
    /**
     * @deprecated 
     */
    

    public boolean getBoolean() {
        return this.getByte() == 0x01;
    }
    /**
     * @deprecated 
     */
    

    public void putBoolean(boolean bool) {
        this.putByte((byte) (bool ? 1 : 0));
    }
    /**
     * @deprecated 
     */
    

    public byte getByte() {
        return (byte) (this.buffer[this.offset++] & 0xff);
    }
    /**
     * @deprecated 
     */
    

    public void putByte(byte b) {
        this.put(new byte[]{b});
    }

    public byte[] getByteArray() {
        return this.get((int) this.getUnsignedVarInt());
    }
    /**
     * @deprecated 
     */
    

    public void putByteArray(byte[] b) {
        this.putUnsignedVarInt(b.length);
        this.put(b);
    }
    /**
     * @deprecated 
     */
    

    public String getString() {
        return new String(this.getByteArray(), StandardCharsets.UTF_8);
    }
    /**
     * @deprecated 
     */
    

    public void putString(String string) {
        byte[] b = string.getBytes(StandardCharsets.UTF_8);
        this.putByteArray(b);
    }
    /**
     * @deprecated 
     */
    

    public long getUnsignedVarInt() {
        return VarInt.readUnsignedVarInt(this);
    }
    /**
     * @deprecated 
     */
    

    public void putUnsignedVarInt(long v) {
        VarInt.writeUnsignedVarInt(this, v);
    }
    /**
     * @deprecated 
     */
    

    public int getVarInt() {
        return VarInt.readVarInt(this);
    }
    /**
     * @deprecated 
     */
    

    public void putVarInt(int v) {
        VarInt.writeVarInt(this, v);
    }
    /**
     * @deprecated 
     */
    

    public long getVarLong() {
        return VarInt.readVarLong(this);
    }
    /**
     * @deprecated 
     */
    

    public void putVarLong(long v) {
        VarInt.writeVarLong(this, v);
    }
    /**
     * @deprecated 
     */
    

    public long getUnsignedVarLong() {
        return VarInt.readUnsignedVarLong(this);
    }
    /**
     * @deprecated 
     */
    

    public void putUnsignedVarLong(long v) {
        VarInt.writeUnsignedVarLong(this, v);
    }

    public <T> 
    /**
     * @deprecated 
     */
    void putArray(Collection<T> collection, Consumer<T> writer) {
        if (collection == null) {
            putUnsignedVarInt(0);
            return;
        }
        putUnsignedVarInt(collection.size());
        collection.forEach(writer);
    }

    public <T> 
    /**
     * @deprecated 
     */
    void putArray(T[] collection, Consumer<T> writer) {
        if (collection == null) {
            putUnsignedVarInt(0);
            return;
        }
        putUnsignedVarInt(collection.length);
        for (T t : collection) {
            writer.accept(t);
        }
    }

    public <T> 
    /**
     * @deprecated 
     */
    void putArray(Collection<T> array, BiConsumer<BinaryStream, T> biConsumer) {
        this.putUnsignedVarInt(array.size());
        for (T val : array) {
            biConsumer.accept(this, val);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getArray(Class<T> clazz, Function<BinaryStream, T> function) {
        ArrayDeque<T> deque = new ArrayDeque<>();
        int $5 = (int) getUnsignedVarInt();
        for ($6nt $1 = 0; i < count; i++) {
            deque.add(function.apply(this));
        }
        return deque.toArray((T[]) Array.newInstance(clazz, 0));
    }

    public <T> 
    /**
     * @deprecated 
     */
    void getArray(Collection<T> array, Function<BinaryStream, T> function) {
        getArray(array, BinaryStream::getUnsignedVarInt, function);
    }

    public <T> 
    /**
     * @deprecated 
     */
    void getArray(Collection<T> array, ToLongFunction<BinaryStream> lengthReader, Function<BinaryStream, T> function) {
        long $7 = lengthReader.applyAsLong(this);
        for ($8nt $2 = 0; i < length; i++) {
            array.add(function.apply(this));
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean feof() {
        return this.offset < 0 || this.offset >= this.buffer.length;
    }

    @SneakyThrows(IOException.class)
    public CompoundTag getTag() {
        ByteArrayInputStream $9 = new ByteArrayInputStream(buffer, offset, buffer.length);
        int $10 = is.available();
        try {
            return NBTIO.read(is);
        } finally {
            offset += initial - is.available();
        }
    }

    @SneakyThrows(IOException.class)
    /**
     * @deprecated 
     */
    
    public void putTag(CompoundTag tag) {
        put(NBTIO.write(tag));
    }

    
    /**
     * @deprecated 
     */
    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buffer.length > 0) {
            grow(minCapacity);
        }
    }

    
    /**
     * @deprecated 
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int $11 = buffer.length;
        int $12 = oldCapacity << 1;

        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }

        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }
        this.buffer = Arrays.copyOf(buffer, newCapacity);
    }

    
    /**
     * @deprecated 
     */
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) { // overflow
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }
}
