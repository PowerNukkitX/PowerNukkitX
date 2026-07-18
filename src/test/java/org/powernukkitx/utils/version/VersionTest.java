package org.powernukkitx.utils.version;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class VersionTest {
    @Test
    void parseIntoParts() {
        List<Comparable<?>> parts = new Version("1.2.3").getParts();
        Assertions.assertEquals(List.of(1, 2, 3), parts);
    }

    @Test
    void parseMixesIntegersAndWords() {
        List<Comparable<?>> parts = new Version("1.0-beta").getParts();
        Assertions.assertEquals(List.of(1, 0, "beta"), parts);
    }

    @Test
    void numericComparison() {
        Assertions.assertTrue(new Version("1.2").compareTo(new Version("1.10")) < 0);
        Assertions.assertTrue(new Version("2.0").compareTo(new Version("1.9")) > 0);
        Assertions.assertEquals(0, new Version("1.0").compareTo(new Version("1.0")));
    }

    @Test
    void caseInsensitiveEquality() {
        Assertions.assertEquals(new Version("1.0-RC"), new Version("1.0-rc"));
        Assertions.assertEquals(new Version("1.0-RC").hashCode(), new Version("1.0-rc").hashCode());
    }

    @Test
    void integerBeatsStringAtSamePosition() {
        // when classes differ, the Integer-typed part is considered newer
        Assertions.assertTrue(new Version("1.1").compareTo(new Version("1.beta")) > 0);
        Assertions.assertTrue(new Version("1.beta").compareTo(new Version("1.1")) < 0);
    }

    @Test
    void missingTrailingPartTreatedAsZero() {
        Assertions.assertEquals(0, new Version("1.0.0").compareTo(new Version("1.0")));
    }

    @Test
    void toStringPreservesOriginal() {
        Assertions.assertEquals("1.0-Final", new Version("1.0-Final").toString());
    }

    @Test
    void separatorsOnlyEqualsEmpty() {
        Assertions.assertEquals(new Version("..."), new Version(""));
    }
}
