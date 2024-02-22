package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

public class PlayerRespawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Position position;//Respawn Position
    @Deprecated
    private Position spawnBlock;
    @Deprecated
    private Position originalSpawnPosition;
    @Deprecated
    private boolean spawnBlockAvailable;
    @Deprecated
    private boolean keepRespawnBlockPosition;
    @Deprecated
    private boolean keepRespawnPosition;
    @Deprecated
    private boolean sendInvalidRespawnBlockMessage = true;
    @Deprecated
    private boolean consumeCharge = true;

    public PlayerRespawnEvent(Player player, Position position) {
        this.player = player;
        this.position = position;
    }

    public Position getRespawnPosition() {
        return position;
    }

    public void setRespawnPosition(Position position) {
        this.position = position;
    }

    @Deprecated
    public Position getRespawnBlockPosition() {
        return spawnBlock;
    }

    @Deprecated
    public void setRespawnBlockPosition(Position spawnBlock) {
        this.spawnBlock = spawnBlock;
    }

    @Deprecated
    public boolean isRespawnBlockAvailable() {
        return spawnBlockAvailable;
    }

    /**
     * Plugins not suggest use
     *
     * @param spawnBlockAvailable the spawn block available
     */
    @Deprecated
    public void setRespawnBlockAvailable(boolean spawnBlockAvailable) {
        this.spawnBlockAvailable = spawnBlockAvailable;
    }

    @Deprecated
    public Position getOriginalRespawnPosition() {
        return originalSpawnPosition;
    }

    @Deprecated
    public void setOriginalRespawnPosition(Position originalSpawnPosition) {
        this.originalSpawnPosition = originalSpawnPosition;
    }

    @Deprecated
    public boolean isKeepRespawnBlockPosition() {
        return keepRespawnBlockPosition;
    }

    @Deprecated
    public void setKeepRespawnBlockPosition(boolean keepRespawnBlockPosition) {
        this.keepRespawnBlockPosition = keepRespawnBlockPosition;
    }

    @Deprecated
    public boolean isKeepRespawnPosition() {
        return keepRespawnPosition;
    }

    @Deprecated
    public void setKeepRespawnPosition(boolean keepRespawnPosition) {
        this.keepRespawnPosition = keepRespawnPosition;
    }

    @Deprecated
    public boolean isSendInvalidRespawnBlockMessage() {
        return sendInvalidRespawnBlockMessage;
    }

    @Deprecated
    public void setSendInvalidRespawnBlockMessage(boolean sendInvalidRespawnBlockMessage) {
        this.sendInvalidRespawnBlockMessage = sendInvalidRespawnBlockMessage;
    }

    @Deprecated
    public boolean isConsumeCharge() {
        return consumeCharge;
    }

    @Deprecated
    public void setConsumeCharge(boolean consumeCharge) {
        this.consumeCharge = consumeCharge;
    }
}
