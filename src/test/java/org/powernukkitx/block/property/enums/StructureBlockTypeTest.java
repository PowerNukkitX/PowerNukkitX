package org.powernukkitx.block.property.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StructureBlockTypeTest {

    @Test
    void roundTrip() {
        Assertions.assertEquals(6, StructureBlockType.values().length);
        for (StructureBlockType t : StructureBlockType.values()) {
            Assertions.assertSame(t, StructureBlockType.valueOf(t.name()));
        }
    }

    @Test
    void from() {
        for (StructureBlockType t : StructureBlockType.values()) {
            Assertions.assertSame(t, StructureBlockType.from(t.ordinal()));
        }
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> StructureBlockType.from(99));
    }
}
