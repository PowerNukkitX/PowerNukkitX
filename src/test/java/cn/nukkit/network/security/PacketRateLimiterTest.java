package cn.nukkit.network.security;

import cn.nukkit.config.category.NetworkSettings;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PacketRateLimiter}.
 * <p>
 * Guava {@link com.google.common.util.concurrent.RateLimiter} uses a real wall clock
 * internally, so these tests use very high limits (many thousands per second) to ensure
 * the first {@code tryAcquire()} always succeeds, and very low limits (1/s) to ensure
 * that rapid consecutive calls reliably fail the second attempt.
 */
class PacketRateLimiterTest {

    private static NetworkSettings settingsWith(int commands, int chat, int forms, int movement) {
        NetworkSettings s = Mockito.mock(NetworkSettings.class);
        Mockito.when(s.maxCommandsPerSecondPerPlayer()).thenReturn(commands);
        Mockito.when(s.maxChatPerSecondPerPlayer()).thenReturn(chat);
        Mockito.when(s.maxFormResponsesPerSecondPerPlayer()).thenReturn(forms);
        Mockito.when(s.maxMovementPacketsPerSecondPerPlayer()).thenReturn(movement);
        return s;
    }

    @Test
    void firstCommandAllowed() {
        PacketRateLimiter limiter = new PacketRateLimiter(settingsWith(10, 10, 10, 10));
        assertTrue(limiter.tryCommand());
    }

    @Test
    void firstChatAllowed() {
        PacketRateLimiter limiter = new PacketRateLimiter(settingsWith(10, 10, 10, 10));
        assertTrue(limiter.tryChat());
    }

    @Test
    void firstFormResponseAllowed() {
        PacketRateLimiter limiter = new PacketRateLimiter(settingsWith(10, 10, 10, 10));
        assertTrue(limiter.tryFormResponse());
    }

    @Test
    void firstMovementAllowed() {
        PacketRateLimiter limiter = new PacketRateLimiter(settingsWith(10, 10, 10, 10));
        assertTrue(limiter.tryMovement());
    }

    @Test
    void commandExhaustedAtRateOne() {
        PacketRateLimiter limiter = new PacketRateLimiter(settingsWith(1, 1000, 1000, 1000));
        assertTrue(limiter.tryCommand(), "First call must succeed");
        assertFalse(limiter.tryCommand(), "Second immediate call must fail at rate=1/s");
    }

    @Test
    void chatExhaustedAtRateOne() {
        PacketRateLimiter limiter = new PacketRateLimiter(settingsWith(1000, 1, 1000, 1000));
        assertTrue(limiter.tryChat(), "First call must succeed");
        assertFalse(limiter.tryChat(), "Second immediate call must fail at rate=1/s");
    }

    @Test
    void formResponseExhaustedAtRateOne() {
        PacketRateLimiter limiter = new PacketRateLimiter(settingsWith(1000, 1000, 1, 1000));
        assertTrue(limiter.tryFormResponse(), "First call must succeed");
        assertFalse(limiter.tryFormResponse(), "Second immediate call must fail at rate=1/s");
    }

    @Test
    void movementExhaustedAtRateOne() {
        PacketRateLimiter limiter = new PacketRateLimiter(settingsWith(1000, 1000, 1000, 1));
        assertTrue(limiter.tryMovement(), "First call must succeed");
        assertFalse(limiter.tryMovement(), "Second immediate call must fail at rate=1/s");
    }

    @Test
    void limitersAreIndependent() {
        PacketRateLimiter limiter = new PacketRateLimiter(settingsWith(1, 1000, 1000, 1000));

        assertTrue(limiter.tryCommand());
        assertFalse(limiter.tryCommand(), "Commands exhausted");

        assertTrue(limiter.tryChat(),         "Chat must still pass after commands exhausted");
        assertTrue(limiter.tryFormResponse(), "FormResponse must still pass");
        assertTrue(limiter.tryMovement(),     "Movement must still pass");
    }
}
