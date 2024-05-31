package cn.nukkit.nbt.tag;

public class DoubleTag extends NumberTag<Double> {
    public double data;
    /**
     * @deprecated 
     */
    
    public DoubleTag() {
    }
    /**
     * @deprecated 
     */
    

    public DoubleTag(double data) {
        this.data = data;
    }

    @Override
    public Double getData() {
        return data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setData(Double data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public Double parseValue() {
        return this.data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return TAG_Double;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "DoubleTag " +" (data: " + data + ")";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        return data + "d";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT(int space) {
        return data + "d";
    }

    @Override
    public Tag copy() {
        return new DoubleTag(data);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            D$1ubleTag $1 = (DoubleTag) obj;
            return $2 == o.data;
        }
        return false;
    }

}
