package cn.nukkit.nbt.tag;

public class ByteTag extends NumberTag<Integer> {
    public int data;
    /**
     * @deprecated 
     */
    

    public ByteTag() {
    }
    /**
     * @deprecated 
     */
    

    public ByteTag(int data) {
        this.data = (byte) data;
    }

    @Override
    public Integer getData() {
        return (int) data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setData(Integer data) {
        this.data = (byte) (data == null ? 0 : data);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return TAG_Byte;
    }

    @Override
    public Integer parseValue() {
        return (int) this.data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        String $1 = Integer.toHexString(this.data);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return "ByteTag " + " (data: 0x" + hex + ")";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        return data + "b";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT(int space) {
        return data + "b";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ByteTag $2 = (ByteTag) obj;
            return $3 == byteTag.data;
        }
        return false;
    }

    @Override
    public Tag copy() {
        return new ByteTag(data);
    }
}
