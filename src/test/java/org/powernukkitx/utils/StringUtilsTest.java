package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class StringUtilsTest {
    @Test
    void beforeLast() {
        Assertions.assertEquals("a", StringUtils.beforeLast("a:b", ":"));
        Assertions.assertEquals("nodelim", StringUtils.beforeLast("nodelim", ":"));
    }

    @Test
    void afterFirst() {
        Assertions.assertEquals("b:c", StringUtils.afterFirst("a:b:c", ":"));
        Assertions.assertEquals("nodelim", StringUtils.afterFirst("nodelim", ":"));
    }

    @Test
    void capitalize() {
        Assertions.assertEquals("", StringUtils.capitalize(""));
        Assertions.assertEquals("A", StringUtils.capitalize("a"));
        Assertions.assertEquals("Hello", StringUtils.capitalize("hello"));
        Assertions.assertEquals("HELLO", StringUtils.capitalize("hELLO"));
    }

    @Test
    void fastSplit() {
        Assertions.assertEquals(List.of("a", "b", "c"), StringUtils.fastSplit(",", "a,b,c"));
        Assertions.assertEquals(List.of("single"), StringUtils.fastSplit(",", "single"));
    }

    @Test
    void fastSplitWithLimit() {
        List<String> parts = StringUtils.fastSplit(",", "a,b,c,d", 2);
        Assertions.assertEquals(Arrays.asList("a", "b,c,d"), parts);
    }

    @Test
    void joinNotNullSkipsNulls() {
        Assertions.assertEquals("a-b", StringUtils.joinNotNull("-", "a", null, "b"));
        Assertions.assertEquals("", StringUtils.joinNotNull("-", (String) null));
    }
}
