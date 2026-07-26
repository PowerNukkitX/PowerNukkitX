package org.powernukkitx.entity;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Creature Entities
 *
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityCreature extends EntityLiving implements EntityNameable, EntityAgeable {


    public EntityCreature(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    /**
     * @deprecated Use {@link #trySetNameTag(Player, Item)} instead.
     * <p>
     * Delegates directly to {@code trySetNameTag(player, item)}.
     */
    @Deprecated
    protected boolean applyNameTag(Player player, Item item) {
        return trySetNameTag(player, item);
    }
}
