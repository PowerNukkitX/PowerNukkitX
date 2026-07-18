package org.powernukkitx.entity.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PlayerFlagTest {

    @Test
    void roundTrip() {
        Assertions.assertEquals(2, PlayerFlag.values().length);
        for (PlayerFlag f : PlayerFlag.values()) {
            Assertions.assertSame(f, PlayerFlag.valueOf(f.name()));
        }
    }

    @Test
    void values() {
        Assertions.assertEquals(1, PlayerFlag.SLEEP.getValue());
        Assertions.assertEquals(2, PlayerFlag.DEAD.getValue());
    }
}
