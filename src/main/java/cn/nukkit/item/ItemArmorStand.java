package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.math.CompassRoseDirection.Precision.PRIMARY_INTER_CARDINAL;


public class ItemArmorStand extends Item {
    /**
     * @deprecated 
     */
    
    public ItemArmorStand() {
        super(ARMOR_STAND);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        IChunk $1 = block.getChunk();
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

        CompassRoseDirection $2 = CompassRoseDirection.getClosestFromYaw(player.yaw, PRIMARY_INTER_CARDINAL).getOppositeFace();
        CompoundTag $3 = Entity.getDefaultNBT(block.add(0.5, 0, 0.5), new Vector3(), direction.getYaw(), 0f);
        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        if (!removeForPlacement(block) || !removeForPlacement(block.up())) {
            return false;
        }

        Entity $4 = Entity.createEntity(Entity.ARMOR_STAND, chunk, nbt);
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
     * @param block The block which is in the same space as the armor stand.
     * @return {@code true} if the armor stand entity can be placed
     */
    
    /**
     * @deprecated 
     */
    protected boolean removeForPlacement(Block block) {
        return switch (block.getId()) {
            case BlockID.AIR -> true;
            case BlockID.SNOW_LAYER -> block.canBeReplaced();
            default -> block.getLevel().setBlock(block, Block.get(BlockID.AIR));
        };
    }
}
