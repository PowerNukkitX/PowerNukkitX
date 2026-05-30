package cn.nukkit.nbt.tag;

public class DoubleTag extends NumberTag<Double> {
    public double data;
    public DoubleTag() {
    }

    public DoubleTag(double data) {
        this.data = data;
    }

    @Override
    public Double getData() {
        return data;
    }

    @Override
    public void setData(Double data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public Double parseValue() {
        return this.data;
    }

    @Override
    public byte getId() {
        return TAG_Double;
    }

    @Override
    public String toString() {
        return "DoubleTag " +" (data: " + data + ")";
    }

    @Override
    public String toSNBT() {
        return data + "d";
    }

    @Override
    public String toSNBT(int space) {
        return data + "d";
    }

    @Override
    public Tag copy() {
        return new DoubleTag(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            DoubleTag o = (DoubleTag) obj;
            return data == o.data;
        }
        return false;
    }

}
