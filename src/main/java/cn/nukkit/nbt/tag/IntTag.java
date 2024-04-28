package cn.nukkit.nbt.tag;

public class IntTag extends NumberTag<Integer> {
    public int data;

    public IntTag() {
    }

    public IntTag(int data) {
        this.data = data;
    }

    @Override
    public Integer getData() {
        return data;
    }

    @Override
    public void setData(Integer data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public Integer parseValue() {
        return this.data;
    }

    @Override
    public byte getId() {
        return TAG_Int;
    }

    @Override
    public String toString() {
        return "IntTag " +"(data: " + data + ")";
    }

    @Override
    public String toSNBT() {
        return String.valueOf(data);
    }

    @Override
    public String toSNBT(int space) {
        return String.valueOf(data);
    }

    @Override
    public Tag copy() {
        return new IntTag(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            IntTag o = (IntTag) obj;
            return data == o.data;
        }
        return false;
    }

}
