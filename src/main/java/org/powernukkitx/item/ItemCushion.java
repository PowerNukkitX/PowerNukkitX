package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityCushion;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.DyeColor;

/**
 * Places a cushion entity, there is no cushion block to put down.
 */
public abstract class ItemCushion extends Item {
    private final DyeColor color;

    protected ItemCushion(String id, DyeColor color) {
        super(id);
        this.color = color;
    }

    /**
     * Creates a cushion item with the requested dye color.
     *
     * @param color the cushion color
     * @return a new item for the corresponding cushion identifier
     * @throws NullPointerException when {@code color} is {@code null}
     */
    public static Item of(DyeColor color) {
        return Item.get(switch (color) {
            case WHITE -> WHITE_CUSHION;
            case ORANGE -> ORANGE_CUSHION;
            case MAGENTA -> MAGENTA_CUSHION;
            case LIGHT_BLUE -> LIGHT_BLUE_CUSHION;
            case YELLOW -> YELLOW_CUSHION;
            case LIME -> LIME_CUSHION;
            case PINK -> PINK_CUSHION;
            case GRAY -> GRAY_CUSHION;
            case LIGHT_GRAY -> LIGHT_GRAY_CUSHION;
            case CYAN -> CYAN_CUSHION;
            case PURPLE -> PURPLE_CUSHION;
            case BLUE -> BLUE_CUSHION;
            case BROWN -> BROWN_CUSHION;
            case GREEN -> GREEN_CUSHION;
            case RED -> RED_CUSHION;
            case BLACK -> BLACK_CUSHION;
        });
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        IChunk chunk = block.getChunk();
        if (chunk == null || !block.canBeReplaced()) {
            return false;
        }

        // a cushion rests on whatever is under it, so it needs something solid to sit on
        if (!block.down().isSolid()) {
            return false;
        }

        SimpleAxisAlignedBB space = new SimpleAxisAlignedBB(block.x, block.y, block.z, block.x + 1, block.y + 1, block.z + 1);
        for (Entity collidingEntity : level.getCollidingEntities(space)) {
            if (collidingEntity instanceof EntityCushion) {
                return false;
            }
        }

        CompoundTag nbt = Entity.getDefaultNBT(block.add(0.5, 0, 0.5), new Vector3(), (float) ((player.yaw + 180f) % 360), 0f)
                .putByte("Color", this.color.getWoolData());

        Entity entity = Entity.createEntity(Entity.CUSHION, chunk, nbt);
        if (entity == null) {
            return false;
        }

        if (!player.isCreative()) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
        }

        entity.spawnToAll();
        return true;
    }
}
