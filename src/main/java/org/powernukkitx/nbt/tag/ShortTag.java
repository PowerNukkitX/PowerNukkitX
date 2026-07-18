package org.powernukkitx.nbt.tag;

public class ShortTag extends NumberTag<Short> {
    public short data;
    public ShortTag() {
    }

    public ShortTag(int data) {
        this.data = (short) data;
    }

    @Override
    public Short getData() {
        return data;
    }

    @Override
    public void setData(Short data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public Short parseValue() {
        return this.data;
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
