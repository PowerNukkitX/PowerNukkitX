package cn.nukkit.level.util;

import cn.nukkit.utils.collection.ByteArrayWrapper;
import cn.nukkit.utils.collection.FreezableArrayManager;
import com.google.common.base.Preconditions;

import java.util.Arrays;

public class NibbleArray implements Cloneable {
    private final int length;
    private final ByteArrayWrapper byteArrayWrapper;

    public NibbleArray(int length) {
        byteArrayWrapper = FreezableArrayManager.getInstance().createByteArray(length / 2);
        this.length = length / 2;
    }

    public NibbleArray(byte[] array) {
        byteArrayWrapper = FreezableArrayManager.getInstance().wrapByteArray(array);
        this.length = array.length;
    }

    public byte get(int index) {
        Preconditions.checkElementIndex(index, length * 2);
        byte val = byteArrayWrapper.getByte(index / 2);
        if ((index & 1) == 0) {
            return (byte) (val & 0x0f);
        } else {
            return (byte) ((val & 0xf0) >>> 4);
        }
    }

    public void set(int index, byte value) {
        Preconditions.checkArgument(value >= 0 && value < 16, "Nibbles must have a value between 0 and 15.");
        Preconditions.checkElementIndex(index, length * 2);
        value &= 0xf;
        int half = index / 2;
        byte previous = byteArrayWrapper.getByte(half);
        if ((index & 1) == 0) {
            byteArrayWrapper.setByte(half, (byte) (previous & 0xf0 | value));
        } else {
            byteArrayWrapper.setByte(half, (byte) (previous & 0x0f | value << 4));
        }
    }

    public void fill(byte value) {
        Preconditions.checkArgument(value >= 0 && value < 16, "Nibbles must have a value between 0 and 15.");
        value &= 0xf;
        final byte[] rawBytes = getData();
        Arrays.fill(rawBytes, (byte) ((value << 4) | value));
        byteArrayWrapper.setRawBytes(rawBytes);
    }

    public void copyFrom(byte[] bytes) {
        final byte[] rawBytes = getData();
        Preconditions.checkNotNull(bytes, "bytes");
        Preconditions.checkArgument(bytes.length == rawBytes.length, "length of provided byte array is %s but expected %s", bytes.length,
                rawBytes.length);
        System.arraycopy(bytes, 0, rawBytes, 0, rawBytes.length);
        byteArrayWrapper.setRawBytes(rawBytes);
    }

    public void copyFrom(NibbleArray array) {
        Preconditions.checkNotNull(array, "array");
        copyFrom(array.getData());
    }

    public byte[] getData() {
        return byteArrayWrapper.getRawBytes();
    }

    public NibbleArray copy() {
        return new NibbleArray(getData().clone());
    }

    @Override
    public NibbleArray clone() {
        try {
            NibbleArray clone = (NibbleArray) super.clone();
            clone.copyFrom(this);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
