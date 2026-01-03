package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * 实体生物
 *
 * @author MagicDroidX (Nukkit Project)
 */

public abstract class EntityCreature extends EntityLiving implements EntityNameable, EntityAgeable {
    public EntityCreature(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    // Armor stands, when implemented, should also check this.
    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId().equals(Item.NAME_TAG) && !player.isAdventure()) {
            return applyNameTag(player, item);
        }
        return false;
    }

    @Override
    public final boolean playerApplyNameTag(@NotNull Player player, @NotNull Item item) {
        return applyNameTag(player, item);
    }

    // Structured like this so I can override nametags in player and dragon classes
    // without overriding onInteract.
    protected boolean applyNameTag(Player player, Item item) {
        if (item.hasCustomName()) {
            this.setNameTag(item.getCustomName());
            this.setNameTagVisible(true);
            this.despawnable = false;
            this.setPersistent(true);

            if (!player.isCreative()) {
                player.getInventory().removeItem(item);
            }
            // Set entity as persistent.
            return true;
        }
        return false;
    }
}
