package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityArmorStand;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.CompassRoseDirection;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;

import static org.powernukkitx.math.CompassRoseDirection.Precision.PRIMARY_INTER_CARDINAL;


public class ItemArmorStand extends Item {
    public ItemArmorStand() {
        super(ARMOR_STAND);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        IChunk chunk = block.getChunk();
        if (chunk == null) {
            return false;
        }

        if (!block.canBeReplaced() || !block.up().canBeReplaced()) {
            return false;
        }

        for (Entity collidingEntity : level.getCollidingEntities(new SimpleAxisAlignedBB(block.x, block.y, block.z, block.x + 1, block.y + 1, block.z + 1))) {
            if (collidingEntity instanceof EntityArmorStand) {
                return false;
            }
        }

        CompassRoseDirection direction = CompassRoseDirection.getClosestFromYaw(player.yaw, PRIMARY_INTER_CARDINAL).getOppositeFace();
        CompoundTag nbt = Entity.getDefaultNBT(block.add(0.5, 0, 0.5), new Vector3(), direction.getYaw(), 0f);
        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        if (!removeForPlacement(block) || !removeForPlacement(block.up())) {
            return false;
        }

        Entity entity = Entity.createEntity(Entity.ARMOR_STAND, chunk, nbt);
        if (entity == null) {
            return false;
        }

        if (!player.isCreative()) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
        }

        entity.spawnToAll();
        player.getLevel().addSound(entity, Sound.MOB_ARMOR_STAND_PLACE);
        return true;
    }

    /**
     * 1
     *
     * @param block The block which is in the same space as the armor stand.
     * @return {@code true} if the armor stand entity can be placed
     */
    protected boolean removeForPlacement(Block block) {
        return switch (block.getId()) {
            case BlockID.AIR -> true;
            case BlockID.SNOW_LAYER -> block.canBeReplaced();
            default -> block.getLevel().setBlock(block, Block.get(BlockID.AIR));
        };
    }
}
