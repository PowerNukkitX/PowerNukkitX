package cn.nukkit.nbt.tag;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StringTag extends Tag {
    public String data;

    public StringTag() {
    }

    public StringTag(@NotNull String data) {
        this.data = Preconditions.checkNotNull(data, "Empty string not allowed");
    }

    @Override
    public String parseValue() {
        return this.data;
    }

    @Override
    public byte getId() {
        return TAG_String;
    }

    @Override
    public String toString() {
        return "StringTag " + " (data: " + data + ")";
    }

    @Override
    public String toSNBT() {
        return "\"" + data + "\"";
    }

    @Override
    public String toSNBT(int space) {
        return "\"" + data + "\"";
    }

    @Override
    public Tag copy() {
        return new StringTag(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            StringTag o = (StringTag) obj;
            return ((data == null && o.data == null) || (data != null && data.equals(o.data)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }
}
