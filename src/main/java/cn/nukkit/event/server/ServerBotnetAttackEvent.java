package cn.nukkit.event.server;

import cn.nukkit.network.security.BotnetReport;
import cn.nukkit.event.HandlerList;

import java.net.InetAddress;
import java.util.Set;

/**
 * Fired when the {@link cn.nukkit.network.security.BotnetDetector} detects a
 * coordinated flood attack from multiple sessions simultaneously.
 * <p>
 * The attached {@link BotnetReport} describes which signals fired and what the
 * confidence score is. Plugins can inspect this to decide whether to cancel
 * auto-blocking or adjust the block duration.
 */
public class ServerBotnetAttackEvent extends ServerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BotnetReport report;
    private boolean autoBlock;
    private int blockDurationSeconds;

    public ServerBotnetAttackEvent(BotnetReport report, boolean autoBlock, int blockDurationSeconds) {
        this.report = report;
        this.autoBlock = autoBlock;
        this.blockDurationSeconds = blockDurationSeconds;
    }

    /** Full report with all signal details and the flooding session addresses. */
    public BotnetReport getReport() {
        return report;
    }

    /** Convenience: the unique IPs of all flooding sessions (port stripped). */
    public Set<InetAddress> getSuspiciousAddresses() {
        return report.getFloodingAddresses();
    }

    public boolean isAutoBlock() {
        return autoBlock;
    }

    public void setAutoBlock(boolean autoBlock) {
        this.autoBlock = autoBlock;
    }

    public int getBlockDurationSeconds() {
        return blockDurationSeconds;
    }

    public void setBlockDurationSeconds(int blockDurationSeconds) {
        this.blockDurationSeconds = blockDurationSeconds;
    }
}
