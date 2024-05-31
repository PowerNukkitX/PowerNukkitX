package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;

public class ShortTag extends NumberTag<Integer> {
    public short data;
    /**
     * @deprecated 
     */
    
    public ShortTag() {
    }
    /**
     * @deprecated 
     */
    

    public ShortTag(int data) {
        this.data = (short) data;
    }

    @Override
    public Integer getData() {
        return (int)  data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setData(Integer data) {
        this.data = (short) (data == null ? 0 : data);
    }

    @Override
    public Integer parseValue() {
        return (int) this.data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return TAG_Short;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "ShortTag " + "(data: " + data + ")";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        return data + "s";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT(int space) {
        return data + "s";
    }

    @Override
    public Tag copy() {
        return new ShortTag(data);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            Sh$1rtTag $1 = (ShortTag) obj;
            return $2 == o.data;
        }
        return false;
    }

}
