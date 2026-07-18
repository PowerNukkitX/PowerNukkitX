package org.powernukkitx.nbt.tag;

import java.util.Arrays;

public class LongArrayTag extends Tag {
    public long[] data;

    public LongArrayTag() {
        this(new long[0]);
    }

    public LongArrayTag(long[] data) {
        this.data = data;
    }

    public long[] getData() {
        return data;
    }

    public void setData(long[] data) {
        this.data = data == null ? new long[0] : data;
    }

    @Override
    public long[] parseValue() {
        return this.data;
    }

    @Override
    public byte getId() {
        return TAG_Long_Array;
    }

    @Override
    public String toString() {
        return "LongArrayTag " + " [" + data.length + " bytes]";
    }

    @Override
    public String toSNBT() {
        return Arrays.toString(data).replace("[", "[I;");
    }

    @Override
    public String toSNBT(int space) {
        return Arrays.toString(data).replace("[", "[I;");
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            LongArrayTag longArrayTag = (LongArrayTag) obj;
            return ((data == null && longArrayTag.data == null) || (data != null && Arrays.equals(data, longArrayTag.data)));
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
        long[] cp = new long[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new LongArrayTag(cp);
    }
}
