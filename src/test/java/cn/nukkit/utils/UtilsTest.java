package cn.nukkit.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {
    @Test
    void Utils_computeRequiredBits() {
        Assertions.assertEquals(2, Utils.computeRequiredBits(0, 3));
        Assertions.assertEquals(3, Utils.computeRequiredBits(0, 4));
        Assertions.assertEquals(3, Utils.computeRequiredBits(1, 8));
        Assertions.assertEquals(4, Utils.computeRequiredBits(1, 9));
    }
}
