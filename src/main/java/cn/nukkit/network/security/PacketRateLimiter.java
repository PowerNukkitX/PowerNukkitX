package cn.nukkit.network.security;

import cn.nukkit.config.category.NetworkSettings;
import com.google.common.util.concurrent.RateLimiter;

/**
 * Per-player inbound packet rate limiters.
 * <p>
 * One instance lives on {@link cn.nukkit.PlayerHandle} for the lifetime of the player's session.
 * All limiters use {@link RateLimiter#tryAcquire()} (non-blocking)
 * <p>
 * A denied packet is dropped immediately rather than stalling the processing thread.
 */
public final class PacketRateLimiter {
    private final RateLimiter command;
    private final RateLimiter chat;
    private final RateLimiter formResponse;
    private final RateLimiter movement;

    public PacketRateLimiter(NetworkSettings settings) {
        this.command = RateLimiter.create(settings.maxCommandsPerSecondPerPlayer());
        this.chat = RateLimiter.create(settings.maxChatPerSecondPerPlayer());
        this.formResponse = RateLimiter.create(settings.maxFormResponsesPerSecondPerPlayer());
        this.movement = RateLimiter.create(settings.maxMovementPacketsPerSecondPerPlayer());
    }

    /** @return true if the command packet should be processed, false if it should be dropped. */
    public boolean tryCommand() {
        return command.tryAcquire();
    }

    /** @return true if the chat packet should be processed, false if it should be dropped. */
    public boolean tryChat() {
        return chat.tryAcquire();
    }

    /** @return true if the form response should be processed, false if it should be dropped. */
    public boolean tryFormResponse() {
        return formResponse.tryAcquire();
    }

    /** @return true if the movement packet should be processed, false if it should be dropped. */
    public boolean tryMovement() {
        return movement.tryAcquire();
    }
}
