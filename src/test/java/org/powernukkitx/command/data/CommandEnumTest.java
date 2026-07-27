package org.powernukkitx.command.data;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandEnumTest {

    @Test
    void varargsConstructorStoresValues() {
        CommandEnum e = new CommandEnum("Colors", "red", "green", "blue");
        assertEquals("Colors", e.getName());
        assertEquals(List.of("red", "green", "blue"), e.getValues());
        assertFalse(e.isSoft());
    }

    @Test
    void listConstructorDefaultsToNonSoft() {
        CommandEnum e = new CommandEnum("X", Arrays.asList("a", "b"));
        assertFalse(e.isSoft());
        assertEquals(List.of("a", "b"), e.getValues());
    }

    @Test
    void softFlagIsRespected() {
        CommandEnum e = new CommandEnum("X", Arrays.asList("a"), true);
        assertTrue(e.isSoft());
    }

    @Test
    void supplierConstructorIsSoftAndLazy() {
        CommandEnum e = new CommandEnum("Dyn", () -> List.of("x", "y"));
        assertTrue(e.isSoft());
        assertEquals(List.of("x", "y"), e.getValues());
    }

    @Test
    void hashCodeIsBasedOnName() {
        CommandEnum e = new CommandEnum("Same", "a");
        assertEquals("Same".hashCode(), e.hashCode());
    }

    @Test
    void toNetworkCarriesNameAndSoftFlag() {
        CommandEnum e = new CommandEnum("Colors", Arrays.asList("red", "green"), true);
        var data = e.toNetwork();
        assertEquals("Colors", data.getName());
        assertTrue(data.isSoft());
        assertTrue(data.getValues().containsKey("red"));
        assertTrue(data.getValues().containsKey("green"));
    }
}
