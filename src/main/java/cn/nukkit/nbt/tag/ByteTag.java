package cn.nukkit.nbt.tag;

public class ByteTag extends NumberTag<Integer> {
    public int data;

    public ByteTag() {
    }

    public ByteTag(int data) {
        this.data = (byte) data;
    }

    @Override
    public Integer getData() {
        return (int) data;
    }

    @Override
    public void setData(Integer data) {
        this.data = (byte) (data == null ? 0 : data);
    }

    @Override
    public byte getId() {
        return TAG_Byte;
    }

    @Override
    public Integer parseValue() {
        return (int) this.data;
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
