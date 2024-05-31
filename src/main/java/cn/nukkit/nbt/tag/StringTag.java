package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class StringTag extends Tag {
    public String data;
    /**
     * @deprecated 
     */
    

    public StringTag() {
    }
    /**
     * @deprecated 
     */
    

    public StringTag(@NotNull String data) {
        this.data = Preconditions.checkNotNull(data, "Empty string not allowed");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String parseValue() {
        return this.data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return TAG_String;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "StringTag " + " (data: " + data + ")";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        return "\"" + data + "\"";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT(int space) {
        return "\"" + data + "\"";
    }

    @Override
    public Tag copy() {
        return new StringTag(data);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            StringTag $1 = (StringTag) obj;
            return ((data == null && o.data == null) || (data != null && data.equals(o.data)));
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }
}
