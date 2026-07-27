package org.powernukkitx.nbt.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EndTagTest {
    @Test
    void id() {
        Assertions.assertEquals(Tag.TAG_End, new EndTag().getId());
    }

    @Test
    void parseValueNull() {
        Assertions.assertNull(new EndTag().parseValue());
    }

    @Test
    void snbtEmpty() {
        Assertions.assertEquals("", new EndTag().toSNBT());
        Assertions.assertEquals("", new EndTag().toSNBT(2));
    }

    @Test
    void toStringValue() {
        Assertions.assertEquals("EndTag", new EndTag().toString());
    }

    @Test
    void copyAndEquals() {
        EndTag a = new EndTag();
        Tag copy = a.copy();
        Assertions.assertInstanceOf(EndTag.class, copy);
        Assertions.assertEquals(a, copy);
        Assertions.assertNotSame(a, copy);
    }
}
