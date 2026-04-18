package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;

/**
 * Creature Entities
 *
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityCreature extends EntityLiving implements EntityNameable, EntityAgeable {
    public EntityCreature(IChunk chunk, NbtMap nbt) {
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
