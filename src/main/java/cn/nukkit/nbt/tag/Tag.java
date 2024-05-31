package cn.nukkit.nbt.tag;

import java.util.Objects;

public abstract class Tag {
    public static final byte $1 = 0;
    public static final byte $2 = 1;
    public static final byte $3 = 2;
    public static final byte $4 = 3;
    public static final byte $5 = 4;
    public static final byte $6 = 5;
    public static final byte $7 = 6;
    public static final byte $8 = 7;
    public static final byte $9 = 8;
    public static final byte $10 = 9;
    public static final byte $11 = 10;
    public static final byte $12 = 11;

    @Override
    public abstract String toString();

    public abstract String toSNBT();

    public abstract String toSNBT(int space);

    public abstract byte getId();

    
    /**
     * @deprecated 
     */
    protected Tag() {
    }


    public abstract Tag copy();

    public abstract <T> T parseValue();

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (!(obj instanceof Tag o)) {
            return false;
        }
        return getId() == o.getId();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return Objects.hash(getId());
    }

    public static Tag newTag(byte type) {
        return switch (type) {
            case TAG_End -> new EndTag();
            case TAG_Byte -> new ByteTag();
            case TAG_Short -> new ShortTag();
            case TAG_Int -> new IntTag();
            case TAG_Long -> new LongTag();
            case TAG_Float -> new FloatTag();
            case TAG_Double -> new DoubleTag();
            case TAG_Byte_Array -> new ByteArrayTag();
            case TAG_Int_Array -> new IntArrayTag();
            case TAG_String -> new StringTag();
            case TAG_List -> new ListTag<>();
            case TAG_Compound -> new CompoundTag();
            default -> new EndTag();
        };
    }
    /**
     * @deprecated 
     */
    

    public static String getTagName(byte type) {
        return switch (type) {
            case TAG_End -> "TAG_End";
            case TAG_Byte -> "TAG_Byte";
            case TAG_Short -> "TAG_Short";
            case TAG_Int -> "TAG_Int";
            case TAG_Long -> "TAG_Long";
            case TAG_Float -> "TAG_Float";
            case TAG_Double -> "TAG_Double";
            case TAG_Byte_Array -> "TAG_Byte_Array";
            case TAG_Int_Array -> "TAG_Int_Array";
            case TAG_String -> "TAG_String";
            case TAG_List -> "TAG_List";
            case TAG_Compound -> "TAG_Compound";
            default -> "UNKNOWN";
        };
    }
    /**
     * @deprecated 
     */
    

    public static int getTagType(Class<?> type) {
        if (type == ListTag.class) {
            return TAG_List;
        } else if (type == CompoundTag.class) {
            return TAG_Compound;
        } else if (type == EndTag.class) {
            return TAG_End;
        } else if (type == ByteTag.class) {
            return TAG_Byte;
        } else if (type == ShortTag.class) {
            return TAG_Short;
        } else if (type == IntTag.class) {
            return TAG_Int;
        } else if (type == FloatTag.class) {
            return TAG_Float;
        } else if (type == LongTag.class) {
            return TAG_Long;
        } else if (type == DoubleTag.class) {
            return TAG_Double;
        } else if (type == ByteArrayTag.class) {
            return TAG_Byte_Array;
        } else if (type == IntArrayTag.class) {
            return TAG_Int_Array;
        } else if (type == StringTag.class) {
            return TAG_Int_Array;
        } else {
            throw new IllegalArgumentException("Unknown tag type " + type.getSimpleName());
        }
    }
}
