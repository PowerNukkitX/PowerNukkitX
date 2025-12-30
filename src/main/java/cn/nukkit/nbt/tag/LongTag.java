package cn.nukkit.nbt.tag;

public class LongTag extends NumberTag<Long> {
    public long data;
    public LongTag() {
    }

    public LongTag(long data) {
        this.data = data;
    }

    @Override
    public Long getData() {
        return data;
    }

    @Override
    public void setData(Long data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public Long parseValue() {
        return this.data;
    }

    @Override
    public byte getId() {
        return TAG_Long;
    }

    @Override
    public String toString() {
        return "LongTag " +  " (data:" + data + ")";
    }

    @Override
    public String toSNBT() {
        return data + "L";
    }

    @Override
    public String toSNBT(int space) {
        return data + "L";
    }

    @Override
    public Tag copy() {
        return new LongTag(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            LongTag o = (LongTag) obj;
            return data == o.data;
        }
        return false;
    }

}
