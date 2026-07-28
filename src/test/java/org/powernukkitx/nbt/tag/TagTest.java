package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TagTest {
    @Test
    void newTagByType() {
        Assertions.assertInstanceOf(EndTag.class, Tag.newTag(Tag.TAG_End));
        Assertions.assertInstanceOf(ByteTag.class, Tag.newTag(Tag.TAG_Byte));
        Assertions.assertInstanceOf(ShortTag.class, Tag.newTag(Tag.TAG_Short));
        Assertions.assertInstanceOf(IntTag.class, Tag.newTag(Tag.TAG_Int));
        Assertions.assertInstanceOf(LongTag.class, Tag.newTag(Tag.TAG_Long));
        Assertions.assertInstanceOf(FloatTag.class, Tag.newTag(Tag.TAG_Float));
        Assertions.assertInstanceOf(DoubleTag.class, Tag.newTag(Tag.TAG_Double));
        Assertions.assertInstanceOf(ByteArrayTag.class, Tag.newTag(Tag.TAG_Byte_Array));
        Assertions.assertInstanceOf(IntArrayTag.class, Tag.newTag(Tag.TAG_Int_Array));
        Assertions.assertInstanceOf(LongArrayTag.class, Tag.newTag(Tag.TAG_Long_Array));
        Assertions.assertInstanceOf(StringTag.class, Tag.newTag(Tag.TAG_String));
        Assertions.assertInstanceOf(ListTag.class, Tag.newTag(Tag.TAG_List));
        Assertions.assertInstanceOf(CompoundTag.class, Tag.newTag(Tag.TAG_Compound));
        Assertions.assertInstanceOf(EndTag.class, Tag.newTag((byte) 99));
    }

    @Test
    void getTagName() {
        Assertions.assertEquals("TAG_End", Tag.getTagName(Tag.TAG_End));
        Assertions.assertEquals("TAG_Int", Tag.getTagName(Tag.TAG_Int));
        Assertions.assertEquals("TAG_Compound", Tag.getTagName(Tag.TAG_Compound));
        Assertions.assertEquals("UNKNOWN", Tag.getTagName((byte) 99));
    }

    @Test
    void getTagType() {
        Assertions.assertEquals(Tag.TAG_List, Tag.getTagType(ListTag.class));
        Assertions.assertEquals(Tag.TAG_Compound, Tag.getTagType(CompoundTag.class));
        Assertions.assertEquals(Tag.TAG_End, Tag.getTagType(EndTag.class));
        Assertions.assertEquals(Tag.TAG_Byte, Tag.getTagType(ByteTag.class));
        Assertions.assertEquals(Tag.TAG_Short, Tag.getTagType(ShortTag.class));
        Assertions.assertEquals(Tag.TAG_Int, Tag.getTagType(IntTag.class));
        Assertions.assertEquals(Tag.TAG_Float, Tag.getTagType(FloatTag.class));
        Assertions.assertEquals(Tag.TAG_Long, Tag.getTagType(LongTag.class));
        Assertions.assertEquals(Tag.TAG_Double, Tag.getTagType(DoubleTag.class));
        Assertions.assertEquals(Tag.TAG_Byte_Array, Tag.getTagType(ByteArrayTag.class));
        Assertions.assertEquals(Tag.TAG_Int_Array, Tag.getTagType(IntArrayTag.class));
        Assertions.assertEquals(Tag.TAG_Long_Array, Tag.getTagType(LongArrayTag.class));
        Assertions.assertEquals(Tag.TAG_String, Tag.getTagType(StringTag.class));
    }

    @Test
    void getTagTypeUnknownThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Tag.getTagType(String.class));
    }

    @Test
    void toNetworkDefaultReturnsParseValue() {
        IntTag tag = new IntTag(7);
        Integer network = tag.toNetwork();
        Assertions.assertEquals(7, network);
    }

    @Test
    void baseEqualsBySameId() {
        Assertions.assertEquals(new EndTag(), new EndTag());
        Assertions.assertNotEquals(new IntTag(1), "notatag");
    }
}
