package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class IntArrayTag extends Tag {
    public int[] data;
    /**
     * @deprecated 
     */
    

    public IntArrayTag() {
        this(new int[0]);
    }
    /**
     * @deprecated 
     */
    

    public IntArrayTag(int[] data) {
        this.data = data;
    }

    public int[] getData() {
        return data;
    }
    /**
     * @deprecated 
     */
    

    public void setData(int[] data) {
        this.data = data == null ? new int[0] : data;
    }

    @Override
    public int[] parseValue() {
        return this.data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return TAG_Int_Array;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "IntArrayTag " + " [" + data.length + " bytes]";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        return Arrays.toString(data).replace("[", "[I;");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT(int space) {
        return Arrays.toString(data).replace("[", "[I;");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            IntArrayTag $1 = (IntArrayTag) obj;
            return ((data == null && intArrayTag.data == null) || (data != null && Arrays.equals(data, intArrayTag.data)));
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        int $2 = super.hashCode();
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public Tag copy() {
        int[] cp = new int[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new IntArrayTag(cp);
    }
}
