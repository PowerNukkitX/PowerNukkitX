package cn.nukkit.nbt.tag;

import java.util.Arrays;

public class IntArrayTag extends Tag {
    public int[] data;

    public IntArrayTag() {
        this(new int[0]);
    }

    public IntArrayTag(int[] data) {
        this.data = data;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data == null ? new int[0] : data;
    }

    @Override
    public int[] parseValue() {
        return this.data;
    }

    @Override
    public byte getId() {
        return TAG_Int_Array;
    }

    @Override
    public String toString() {
        return "IntArrayTag " + " [" + data.length + " bytes]";
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
            IntArrayTag intArrayTag = (IntArrayTag) obj;
            return ((data == null && intArrayTag.data == null) || (data != null && Arrays.equals(data, intArrayTag.data)));
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
        int[] cp = new int[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new IntArrayTag(cp);
    }
}
