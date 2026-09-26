package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityCushion;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.DyeColor;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;

/**
 * Places a cushion entity, there is no cushion block to put down.
 */
public abstract class ItemCushion extends Item {
    private static final double CUSHION_WIDTH = 0.999;
    private static final double CUSHION_HEIGHT = 0.249;

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

        // a cushion is only placed on the top face, at the height that was clicked
        if (face != BlockFace.UP) {
            return false;
        }

        IChunk chunk = target.getChunk();
        if (chunk == null) {
            return false;
        }

        Vector3 click = new Vector3(target.x + fx, target.y + fy, target.z + fz);
        SimpleAxisAlignedBB space = new SimpleAxisAlignedBB(
            target.x, click.y, target.z,
            target.x + CUSHION_WIDTH, click.y + CUSHION_HEIGHT, target.z + CUSHION_WIDTH
        );
        for (Entity nearby : level.getCollidingEntities(space.grow(1, 1, 1))) {
            if (nearby instanceof EntityCushion) {
                AxisAlignedBB other = nearby.getBoundingBox();
                if (other.intersectsWith(space) || isInside(other, click)) {
                    return false;
                }
            }
        }

        CompoundTag nbt = Entity.getDefaultNBT(new Vector3(target.x + 0.5, click.y, target.z + 0.5), new Vector3(), (float) getPlacedYaw(player.yaw), 0f)
                .putInt("Variant", this.color.getDyeData());

        Entity entity = Entity.createEntity(Entity.CUSHION, chunk, nbt);
        if (entity == null) {
            return false;
        }

        if (this.hasCustomName()) {
            entity.setNameTag(this.getCustomName());
        }

        if (!player.isCreative()) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
        }

        entity.spawnToAll();
        level.addLevelSoundEvent(entity, SoundEvent.SPAWN, -1, Entity.CUSHION, false, false);
        return true;
    }

    /**
     * Whether the clicked point lies within the box of another cushion.
     */
    private static boolean isInside(AxisAlignedBB box, Vector3 click) {
        return click.x > box.getMinX() && click.x < box.getMaxX()
            && click.z > box.getMinZ() && click.z < box.getMaxZ()
            && click.y - 0.001 < box.getMaxY() && click.y + 0.001 > box.getMinY();
    }

    /**
     * A placed cushion faces the player, rounded to 90 degrees.
     */
    private static double getPlacedYaw(double playerYaw) {
        double wrapped = playerYaw % 360;
        if (wrapped < 0) {
            wrapped += 360;
        }

        double yaw = Math.floor((wrapped - 135) / 90) * 90;
        if (yaw >= 180) {
            yaw -= 360;
        }
        return yaw;
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
