package org.powernukkitx.utils.version;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VersionStringComparatorTest {
    private final VersionStringComparator cmp = VersionStringComparator.getInstance();

    @Test
    void singleton() {
        Assertions.assertSame(VersionStringComparator.getInstance(), VersionStringComparator.getInstance());
    }

    @Test
    void comparesLikeVersion() {
        Assertions.assertTrue(cmp.compare("1.2", "1.10") < 0);
        Assertions.assertTrue(cmp.compare("2.0", "1.9") > 0);
        Assertions.assertEquals(0, cmp.compare("1.0.0", "1.0"));
    }
}
