package org.powernukkitx.entity.ai.executor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests mob attack difficulty scaling.
 *
 * @author Curse
 */
class MeleeAttackExecutorTest {

    @Test
    void scalesCanonicalMobDamageForPlayerDifficulty() {
        Assertions.assertEquals(0f, MeleeAttackExecutor.scaleDamageForDifficulty(12f, 0), 0f);
        Assertions.assertEquals(7f, MeleeAttackExecutor.scaleDamageForDifficulty(12f, 1), 0f);
        Assertions.assertEquals(12f, MeleeAttackExecutor.scaleDamageForDifficulty(12f, 2), 0f);
        Assertions.assertEquals(18f, MeleeAttackExecutor.scaleDamageForDifficulty(12f, 3), 0f);
    }

    @Test
    void easyDifficultyDoesNotIncreaseLowDamage() {
        Assertions.assertEquals(1f, MeleeAttackExecutor.scaleDamageForDifficulty(1f, 1), 0f);
        Assertions.assertEquals(2f, MeleeAttackExecutor.scaleDamageForDifficulty(2f, 1), 0f);
    }

    @Test
    void vexBaseAndSwordProduceCanonicalDifficultyDamage() {
        float damage = 3f + 6f;

        Assertions.assertEquals(5.5f, MeleeAttackExecutor.scaleDamageForDifficulty(damage, 1), 0f);
        Assertions.assertEquals(9f, MeleeAttackExecutor.scaleDamageForDifficulty(damage, 2), 0f);
        Assertions.assertEquals(13.5f, MeleeAttackExecutor.scaleDamageForDifficulty(damage, 3), 0f);
    }
}
