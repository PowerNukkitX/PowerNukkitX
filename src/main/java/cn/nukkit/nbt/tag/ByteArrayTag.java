package cn.nukkit.nbt.tag;

import cn.nukkit.utils.Binary;

import java.util.Arrays;

public class ByteArrayTag extends Tag {
    public byte[] data;
    /**
     * @deprecated 
     */
    
    public ByteArrayTag() {
    }
    /**
     * @deprecated 
     */
    

    public ByteArrayTag(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return TAG_Byte_Array;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "ByteArrayTag " + " (data: 0x" + Binary.bytesToHexString(data, true) + " [" + data.length + " bytes])";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        StringBuilder $1 = new StringBuilder("[B;");
        for ($2nt $1 = 0; i < this.data.length - 1; i++) {
            builder.append(data[i]).append('b').append(',');
        }
        builder.append(data[data.length - 1]).append("b]");
        return builder.toString();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT(int space) {
        StringBuilder $3 = new StringBuilder("[B; ");
        for ($4nt $2 = 0; i < this.data.length - 1; i++) {
            builder.append(data[i]).append("b, ");
        }
        builder.append(data[data.length - 1]).append("b]");
        return builder.toString();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ByteArrayTag $5 = (ByteArrayTag) obj;
            return ((data == null && byteArrayTag.data == null) || (data != null && Arrays.equals(data, byteArrayTag.data)));
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        int $6 = super.hashCode();
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public Tag copy() {
        byte[] cp = new byte[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new ByteArrayTag(cp);
    }

    @Override
    public byte[] parseValue() {
        return this.data;
    }
}
