package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a scheduled block update entry with priority and delay.
 * Used for managing block updates in the world tick system.
 *
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockUpdateEntry implements Comparable<BlockUpdateEntry> {
    private static final AtomicLong entryID = new AtomicLong(0);

    public int priority;
    public long delay;


    public boolean checkBlockWhenUpdate = true;

    public final Vector3 pos;
    public final Block block;

    public final long id;

    public BlockUpdateEntry(Vector3 pos, Block block) {
        this.pos = pos;
        this.block = block;
        this.id = entryID.getAndIncrement();
    }

    public BlockUpdateEntry(Vector3 pos, Block block, long delay, int priority) {
        this.id = entryID.getAndIncrement();
        this.pos = pos;
        this.priority = priority;
        this.delay = delay;
        this.block = block;
    }

    public BlockUpdateEntry(Vector3 pos, Block block, long delay, int priority, boolean checkBlockWhenUpdate) {
        this.id = entryID.getAndIncrement();
        this.pos = pos;
        this.priority = priority;
        this.delay = delay;
        this.block = block;
        this.checkBlockWhenUpdate = checkBlockWhenUpdate;
    }

    @Override
    public int compareTo(BlockUpdateEntry entry) {
        return this.delay < entry.delay ? -1 : (this.delay > entry.delay ? 1 : (this.priority != entry.priority ? this.priority - entry.priority : Long.compare(this.id, entry.id)));
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BlockUpdateEntry entry)) {
            if (object instanceof Block) {
                return ((Block) object).layer == block.layer && pos.equals(object);
            }
            if (object instanceof Vector3) {
                return block.layer == 0 && pos.equals(object);
            }
            return false;
        } else {
            return block.layer == entry.block.layer && this.pos.equals(entry.pos) && Block.equals(this.block, entry.block, false);
        }
    }

    @Override
    public int hashCode() {
        return this.pos.hashCode();
    }
}
