package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SemVersionTest {
    @Test
    void fromFullList() {
        SemVersion v = SemVersion.from(Arrays.asList(1, 2, 3, 4, 5));
        Assertions.assertEquals(1, v.major());
        Assertions.assertEquals(2, v.minor());
        Assertions.assertEquals(3, v.patch());
        Assertions.assertEquals(4, v.revision());
        Assertions.assertEquals(5, v.build());
    }

    @Test
    void fromEmptyListReturnsZeroed() {
        SemVersion v = SemVersion.from(Collections.emptyList());
        Assertions.assertEquals(new SemVersion(0, 0, 0, 0, 0), v);
    }

    @Test
    void fromWrongSizeThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> SemVersion.from(Arrays.asList(1, 2, 3)));
    }

    @Test
    void fromStringFull() {
        SemVersion v = SemVersion.fromString("1.2.3.4.5");
        Assertions.assertEquals(new SemVersion(1, 2, 3, 4, 5), v);
    }

    @Test
    void fromStringPartialPadsWithZero() {
        SemVersion v = SemVersion.fromString("7.8");
        Assertions.assertEquals(new SemVersion(7, 8, 0, 0, 0), v);
    }

    @Test
    void fromStringWithoutDotReturnsEmpty() {
        SemVersion v = SemVersion.fromString("42");
        Assertions.assertEquals(new SemVersion(0, 0, 0, 0, 0), v);
    }

    @Test
    void toTagRoundTrip() {
        SemVersion v = new SemVersion(1, 2, 3, 4, 5);
        List<Integer> tag = v.toTag();
        Assertions.assertEquals(Arrays.asList(1, 2, 3, 4, 5), tag);
        Assertions.assertEquals(v, SemVersion.from(tag));
    }
}
