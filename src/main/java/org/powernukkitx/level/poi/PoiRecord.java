package org.powernukkitx.level.poi;

import org.powernukkitx.math.BlockVector3;

/**
 * One indexed POI block. Tickets model occupancy without scanning entities:
 * an entity claiming the POI acquires a ticket and releases it when it lets go.
 * Ticket changes notify the owning section so it gets persisted with the chunk.
 */
public final class PoiRecord {

    private final BlockVector3 pos;
    private final PoiType type;
    private final Runnable setDirty;
    private int freeTickets;

    public PoiRecord(BlockVector3 pos, PoiType type, Runnable setDirty) {
        this(pos, type, type.maxTickets(), setDirty);
    }

    public PoiRecord(BlockVector3 pos, PoiType type, int freeTickets, Runnable setDirty) {
        this.pos = pos;
        this.type = type;
        this.setDirty = setDirty;
        this.freeTickets = Math.max(0, Math.min(freeTickets, type.maxTickets()));
    }

    public BlockVector3 getPos() {
        return pos;
    }

    public PoiType getType() {
        return type;
    }

    public synchronized int getFreeTickets() {
        return freeTickets;
    }

    public synchronized boolean acquireTicket() {
        if (freeTickets <= 0) {
            return false;
        }
        freeTickets--;
        setDirty.run();
        return true;
    }

    /**
     * Idempotent claim used to re-validate a memory after a chunk reload: acquires a
     * ticket only when the POI is completely free, and always reports success so a
     * holder whose ticket survived persistence is not evicted.
     */
    public synchronized boolean ensureTicket() {
        if (freeTickets == type.maxTickets()) {
            freeTickets--;
            setDirty.run();
        }
        return true;
    }

    public synchronized boolean releaseTicket() {
        if (freeTickets >= type.maxTickets()) {
            return false;
        }
        freeTickets++;
        setDirty.run();
        return true;
    }

    public synchronized boolean hasSpace() {
        return freeTickets > 0;
    }

    public synchronized boolean isOccupied() {
        return freeTickets != type.maxTickets();
    }
}
