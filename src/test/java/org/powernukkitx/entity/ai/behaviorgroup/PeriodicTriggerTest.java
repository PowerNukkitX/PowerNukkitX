package org.powernukkitx.entity.ai.behaviorgroup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PeriodicTriggerTest {

    @Test
    void isDueOnlyEveryPeriodTicks() {
        var trigger = new PeriodicTrigger<>("behavior", () -> 3);

        Assertions.assertFalse(trigger.isDue());
        Assertions.assertFalse(trigger.isDue());
        Assertions.assertTrue(trigger.isDue());
        Assertions.assertFalse(trigger.isDue());
        Assertions.assertFalse(trigger.isDue());
        Assertions.assertTrue(trigger.isDue());
    }

    @Test
    void periodOneIsDueEveryTick() {
        var trigger = new PeriodicTrigger<>("sensor", () -> 1);

        for (int i = 0; i < 5; i++) {
            Assertions.assertTrue(trigger.isDue());
        }
    }
}
