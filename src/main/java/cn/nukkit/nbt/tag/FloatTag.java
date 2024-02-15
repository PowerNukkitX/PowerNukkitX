package cn.nukkit.nbt.tag;

public class FloatTag extends NumberTag<Float> {
    public float data;

    public FloatTag() {
    }

    public FloatTag(double data) {
        this.data = (float) data;
    }

    public FloatTag(float data) {
        this.data = data;
    }

    @Override
    public Float getData() {
        return data;
    }

    @Override
    public void setData(Float data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public Float parseValue() {
        return this.data;
    }

    @Override
    public byte getId() {
        return TAG_Float;
    }

    @Override
    public String toString() {
        return "FloatTag " + " (data: " + data + ")";
    }

    @Override
    public String toSNBT() {
        return data + "f";
    }

    @Override
    public String toSNBT(int space) {
        return data + "f";
    }

    @Override
    public Tag copy() {
        return new FloatTag(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            FloatTag o = (FloatTag) obj;
            return data == o.data;
        }
        return false;
    }
}
