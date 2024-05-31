package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;

public class EndTag extends Tag {
    /**
     * @deprecated 
     */
    

    public EndTag() {
        super();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return TAG_End;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "EndTag";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        return "";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT(int space) {
        return "";
    }

    @Override
    public Tag copy() {
        return new EndTag();
    }

    @Override
    public Void parseValue() {
        return null;
    }
}
