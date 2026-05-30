package cn.nukkit.nbt.tag;

import cn.nukkit.utils.Binary;

import java.util.Arrays;

public class ByteArrayTag extends Tag {
    public byte[] data;
    public ByteArrayTag() {
    }

    public ByteArrayTag(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public byte getId() {
        return TAG_Byte_Array;
    }

    @Override
    public String toString() {
        return "ByteArrayTag " + " (data: 0x" + Binary.bytesToHexString(data, true) + " [" + data.length + " bytes])";
    }

    @Override
    public String toSNBT() {
        StringBuilder builder = new StringBuilder("[B;");
        for (int i = 0; i < this.data.length - 1; i++) {
            builder.append(data[i]).append('b').append(',');
        }
        builder.append(data[data.length - 1]).append("b]");
        return builder.toString();
    }

    @Override
    public String toSNBT(int space) {
        StringBuilder builder = new StringBuilder("[B; ");
        for (int i = 0; i < this.data.length - 1; i++) {
            builder.append(data[i]).append("b, ");
        }
        builder.append(data[data.length - 1]).append("b]");
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ByteArrayTag byteArrayTag = (ByteArrayTag) obj;
            return ((data == null && byteArrayTag.data == null) || (data != null && Arrays.equals(data, byteArrayTag.data)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public Tag copy() {
        byte[] cp = new byte[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new ByteArrayTag(cp);
    }

    @Override
    public byte[] parseValue() {
        return this.data;
    }
}
