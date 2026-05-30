package cn.nukkit.nbt.tag;

public class ShortTag extends NumberTag<Integer> {
    public short data;
    public ShortTag() {
    }

    public ShortTag(int data) {
        this.data = (short) data;
    }

    @Override
    public Integer getData() {
        return (int)  data;
    }

    @Override
    public void setData(Integer data) {
        this.data = (short) (data == null ? 0 : data);
    }

    @Override
    public Integer parseValue() {
        return (int) this.data;
    }

    @Override
    public byte getId() {
        return TAG_Short;
    }

    @Override
    public String toString() {
        return "ShortTag " + "(data: " + data + ")";
    }

    @Override
    public String toSNBT() {
        return data + "s";
    }

    @Override
    public String toSNBT(int space) {
        return data + "s";
    }

    @Override
    public Tag copy() {
        return new ShortTag(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ShortTag o = (ShortTag) obj;
            return data == o.data;
        }
        return false;
    }

}
