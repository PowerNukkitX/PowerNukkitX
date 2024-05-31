package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;

public class LongTag extends NumberTag<Long> {
    public long data;
    /**
     * @deprecated 
     */
    
    public LongTag() {
    }
    /**
     * @deprecated 
     */
    

    public LongTag(long data) {
        this.data = data;
    }

    @Override
    public Long getData() {
        return data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setData(Long data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public Long parseValue() {
        return this.data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return TAG_Long;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "LongTag " +  " (data:" + data + ")";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        return data + "L";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT(int space) {
        return data + "L";
    }

    @Override
    public Tag copy() {
        return new LongTag(data);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            L$1ngTag $1 = (LongTag) obj;
            return $2 == o.data;
        }
        return false;
    }

}
