package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HumanStringComparatorTest {
    private final HumanStringComparator cmp = HumanStringComparator.getInstance();

    @Test
    void singletonInstance() {
        Assertions.assertSame(HumanStringComparator.getInstance(), HumanStringComparator.getInstance());
    }

    @Test
    void equalStringsAreZero() {
        Assertions.assertEquals(0, cmp.compare("abc", "abc"));
        Assertions.assertEquals(0, cmp.compare("item2", "item2"));
    }

    @Test
    void numericSuffixOrdersNaturally() {
        Assertions.assertTrue(cmp.compare("item2", "item10") < 0);
        Assertions.assertTrue(cmp.compare("item10", "item2") > 0);
    }

    @Test
    void symmetry() {
        int a = cmp.compare("alpha1", "beta2");
        int b = cmp.compare("beta2", "alpha1");
        Assertions.assertEquals(Integer.signum(a), -Integer.signum(b));
    }

    @Test
    void plainLexicographic() {
        Assertions.assertTrue(cmp.compare("apple", "banana") < 0);
        Assertions.assertTrue(cmp.compare("banana", "apple") > 0);
    }
}
