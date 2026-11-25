package org.powernukkitx.block.property.type;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Covers the string-valued enum property that behaviour pack block states are built from.
 */
class StringEnumPropertyTypeTest {

    private static final StringEnumPropertyType DIRECTION =
            StringEnumPropertyType.of("pnxtest:direction", List.of("south", "west", "north", "east"), "south");

    @Test
    void indexesValuesInDeclarationOrder() {
        assertEquals(0, DIRECTION.createValue("south").getIndex());
        assertEquals(2, DIRECTION.createValue("north").getIndex());
        assertEquals("east", DIRECTION.createValue("east").getSerializedValue());
        assertEquals(BlockPropertyType.Type.ENUM, DIRECTION.getType());
        assertEquals("south", DIRECTION.getDefaultValue());
    }

    @Test
    void reusesValueInstances() {
        assertSame(DIRECTION.createValue("west"), DIRECTION.createValue("west"));
    }

    @Test
    void lenientLookupIgnoresCase() {
        assertSame(DIRECTION.createValue("north"), DIRECTION.tryCreateValue("NORTH"));
    }

    @Test
    void rejectsUnknownValues() {
        assertThrows(IllegalArgumentException.class, () -> DIRECTION.createValue("up"));
        assertThrows(IllegalArgumentException.class, () -> DIRECTION.tryCreateValue(3));
    }

    @Test
    void rejectsInvalidDeclarations() {
        assertThrows(IllegalArgumentException.class, () -> StringEnumPropertyType.of("pnxtest:empty", List.of(), "a"));
        assertThrows(IllegalArgumentException.class, () -> StringEnumPropertyType.of("pnxtest:bad_default", List.of("a", "b"), "c"));
    }
}
