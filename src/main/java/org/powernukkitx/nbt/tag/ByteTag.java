package org.powernukkitx.nbt.tag;

public class ByteTag extends NumberTag<Byte> {
    public byte data;

    public ByteTag() {
    }

    public ByteTag(byte data) {
        this.data = (byte) data;
    }

    @Override
    public Byte getData() {
        return (byte) data;
    }

    @Override
    public void setData(Byte data) {
        this.data = (byte) (data == null ? 0 : data);
    }

    @Override
    public byte getId() {
        return TAG_Byte;
    }

    @Override
    public Byte parseValue() {
        return (byte) this.data;
    }

    @Override
    public String toString() {
        String hex = Integer.toHexString(this.data);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return "ByteTag " + " (data: 0x" + hex + ")";
    }

    @Override
    public String toSNBT() {
        return data + "b";
    }

    @Override
    public String toSNBT(int space) {
        return data + "b";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ByteTag byteTag = (ByteTag) obj;
            return data == byteTag.data;
        }
        return false;
    }

    @Override
    public Tag copy() {
        return new ByteTag(data);
    }
}
