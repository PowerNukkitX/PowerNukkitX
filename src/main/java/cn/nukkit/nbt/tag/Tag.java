package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;

public abstract class Tag {
    public static final byte TAG_End = 0;
    public static final byte TAG_Byte = 1;
    public static final byte TAG_Short = 2;
    public static final byte TAG_Int = 3;
    public static final byte TAG_Long = 4;
    public static final byte TAG_Float = 5;
    public static final byte TAG_Double = 6;
    public static final byte TAG_Byte_Array = 7;
    public static final byte TAG_String = 8;
    public static final byte TAG_List = 9;
    public static final byte TAG_Compound = 10;
    public static final byte TAG_Int_Array = 11;

    private String name;

    abstract void write(NBTOutputStream dos) throws IOException;

    abstract void load(NBTInputStream dis) throws IOException;

    @Override
    public abstract String toString();

    public abstract String toSNBT();

    public abstract String toSNBT(int space);

    public abstract byte getId();

    protected Tag(String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag o = (Tag) obj;
        return getId() == o.getId() && !(name == null && o.name != null || name != null && o.name == null) && !(name != null && !name.equals(o.name));
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId(), name);
    }
    
    public void print(PrintStream out) {
        print("", out);
    }

    public void print(String prefix, PrintStream out) {
        String name = getName();

        out.print(prefix);
        out.print(getTagName(getId()));
        if (name.length() > 0) {
            out.print("(\"" + name + "\")");
        }
        out.print(": ");
        if (getId() != TAG_Compound && getId() != TAG_List) {
            out.println(parseValue());
        }
    }

    public Tag setName(String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
        return this;
    }

    public @NotNull String getName() {
        if (name == null) return "";
        return name;
    }

    /**
     * Only used for LevelEventGenericPacket
     * Which do not read tag id and name (It only can be a compound tag)
     */
    public static CompoundTag readCompoundValue(NBTInputStream dis) throws IOException {
        var compoundTag = new CompoundTag();
        compoundTag.load(dis);
        return compoundTag;
    }

    public static Tag readNamedTag(NBTInputStream dis) throws IOException {
        byte type = dis.readByte();
        if (type == 0) return new EndTag();

        String name = dis.readUTF();

        Tag tag = newTag(type, name);

        tag.load(dis);
        return tag;
    }

    /**
     * Only used for LevelEventGenericPacket
     * Which do not write tag id and name
     */
    public static void writeValue(Tag tag, NBTOutputStream dos) throws IOException {
        if (tag.getId() == Tag.TAG_End) return;

        tag.write(dos);
    }

    public static void writeNamedTag(Tag tag, NBTOutputStream dos) throws IOException {
        writeNamedTag(tag, tag.getName(), dos);
    }

    public static void writeNamedTag(Tag tag, String name, NBTOutputStream dos) throws IOException {
        dos.writeByte(tag.getId());
        if (tag.getId() == Tag.TAG_End) return;
        dos.writeUTF(name);

        tag.write(dos);
    }

    public static Tag newTag(byte type, String name) {
        return switch (type) {
            case TAG_End -> new EndTag();
            case TAG_Byte -> new ByteTag(name);
            case TAG_Short -> new ShortTag(name);
            case TAG_Int -> new IntTag(name);
            case TAG_Long -> new LongTag(name);
            case TAG_Float -> new FloatTag(name);
            case TAG_Double -> new DoubleTag(name);
            case TAG_Byte_Array -> new ByteArrayTag(name);
            case TAG_Int_Array -> new IntArrayTag(name);
            case TAG_String -> new StringTag(name);
            case TAG_List -> new ListTag<>(name);
            case TAG_Compound -> new CompoundTag(name);
            default -> new EndTag();
        };
    }

    public static String getTagName(byte type) {
        switch (type) {
            case TAG_End:
                return "TAG_End";
            case TAG_Byte:
                return "TAG_Byte";
            case TAG_Short:
                return "TAG_Short";
            case TAG_Int:
                return "TAG_Int";
            case TAG_Long:
                return "TAG_Long";
            case TAG_Float:
                return "TAG_Float";
            case TAG_Double:
                return "TAG_Double";
            case TAG_Byte_Array:
                return "TAG_Byte_Array";
            case TAG_Int_Array:
                return "TAG_Int_Array";
            case TAG_String:
                return "TAG_String";
            case TAG_List:
                return "TAG_List";
            case TAG_Compound:
                return "TAG_Compound";
        }
        return "UNKNOWN";
    }

    public abstract Tag copy();

    public abstract Object parseValue();
}
