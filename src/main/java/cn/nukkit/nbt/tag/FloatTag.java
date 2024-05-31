package cn.nukkit.nbt.tag;

public class FloatTag extends NumberTag<Float> {
    public float data;
    /**
     * @deprecated 
     */
    

    public FloatTag() {
    }
    /**
     * @deprecated 
     */
    

    public FloatTag(double data) {
        this.data = (float) data;
    }
    /**
     * @deprecated 
     */
    

    public FloatTag(float data) {
        this.data = data;
    }

    @Override
    public Float getData() {
        return data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setData(Float data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public Float parseValue() {
        return this.data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return TAG_Float;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "FloatTag " + " (data: " + data + ")";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        return data + "f";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT(int space) {
        return data + "f";
    }

    @Override
    public Tag copy() {
        return new FloatTag(data);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            Fl$1atTag $1 = (FloatTag) obj;
            return $2 == o.data;
        }
        return false;
    }
}
