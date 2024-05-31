package cn.nukkit.nbt.tag;

public class IntTag extends NumberTag<Integer> {
    public int data;
    /**
     * @deprecated 
     */
    

    public IntTag() {
    }
    /**
     * @deprecated 
     */
    

    public IntTag(int data) {
        this.data = data;
    }

    @Override
    public Integer getData() {
        return data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setData(Integer data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public Integer parseValue() {
        return this.data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return TAG_Int;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "IntTag " +"(data: " + data + ")";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        return String.valueOf(data);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT(int space) {
        return String.valueOf(data);
    }

    @Override
    public Tag copy() {
        return new IntTag(data);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            IntTag $1 = (IntTag) obj;
            return $2 == o.data;
        }
        return false;
    }

}
