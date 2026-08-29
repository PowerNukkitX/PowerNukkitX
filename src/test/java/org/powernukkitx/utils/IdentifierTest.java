package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IdentifierTest {
    @Test
    void defaultNamespaceWhenMissing() {
        Identifier id = new Identifier("stone");
        Assertions.assertEquals("minecraft", id.getNamespace());
        Assertions.assertEquals("stone", id.getPath());
        Assertions.assertEquals("minecraft:stone", id.toString());
    }

    @Test
    void explicitNamespace() {
        Identifier id = new Identifier("custom:thing");
        Assertions.assertEquals("custom", id.getNamespace());
        Assertions.assertEquals("thing", id.getPath());
    }

    @Test
    void twoArgConstructor() {
        Identifier id = new Identifier("ns", "path/sub");
        Assertions.assertEquals("ns", id.getNamespace());
        Assertions.assertEquals("path/sub", id.getPath());
    }

    @Test
    void invalidCharactersThrow() {
        Assertions.assertThrows(InvalidIdentifierException.class, () -> new Identifier("Bad:path"));
        Assertions.assertThrows(InvalidIdentifierException.class, () -> new Identifier("ns", "Path"));
    }

    @Test
    void tryParseReturnsNullOnInvalid() {
        Assertions.assertNull(Identifier.tryParse("BAD:x"));
        Assertions.assertNotNull(Identifier.tryParse("minecraft:stone"));
    }

    @Test
    void ofReturnsNullOnInvalid() {
        Assertions.assertNull(Identifier.of("ns", "BAD"));
        Assertions.assertNotNull(Identifier.of("ns", "ok"));
    }

    @Test
    void isValid() {
        Assertions.assertTrue(Identifier.isValid("minecraft:stone"));
        Assertions.assertTrue(Identifier.isValid("stone"));
        Assertions.assertFalse(Identifier.isValid("Bad:x"));
    }

    @Test
    void assertValid() {
        Assertions.assertDoesNotThrow(() -> Identifier.assertValid("ns:path"));
        Assertions.assertThrows(InvalidIdentifierException.class, () -> Identifier.assertValid("NS:path"));
    }

    @Test
    void charValidity() {
        Assertions.assertTrue(Identifier.isCharValid('a'));
        Assertions.assertTrue(Identifier.isCharValid('9'));
        Assertions.assertTrue(Identifier.isCharValid(':'));
        Assertions.assertFalse(Identifier.isCharValid('A'));
        Assertions.assertTrue(Identifier.isPathCharacterValid('/'));
        Assertions.assertFalse(Identifier.isPathCharacterValid(':'));
    }

    @Test
    void splitOnCustomDelimiter() {
        Identifier id = Identifier.splitOn("ns/path", '/');
        Assertions.assertEquals("ns", id.getNamespace());
        Assertions.assertEquals("path", id.getPath());
    }

    @Test
    void equalsAndHashCode() {
        Identifier a = new Identifier("ns:path");
        Identifier b = new Identifier("ns", "path");
        Identifier c = new Identifier("ns:other");
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
        Assertions.assertNotEquals(a, c);
    }
}
