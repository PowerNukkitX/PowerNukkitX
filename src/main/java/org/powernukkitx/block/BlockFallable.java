package org.powernukkitx.block;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityFallingBlock;
import org.powernukkitx.event.block.BlockFallEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.math.Vector3;


/**
 * @author rcsuperman (Nukkit Project)
 */
public abstract class BlockFallable extends BlockSolid {

    public BlockFallable(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.getGameplaySettings().enableBlockGravity()) {
            return 0;
        }
        Block down = this.down();
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if ((down.isAir() || down instanceof BlockFire || down instanceof BlockLiquid ||
                    (down instanceof BlockBubbleColumn && down.getLevelBlockAtLayer(1) instanceof BlockLiquid))) {
                BlockFallEvent event = new BlockFallEvent(this);
                this.level.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return type;
                }
                drop(new CompoundTag());
                return type;
            }
        }
        return 0;
    }

    public void drop(CompoundTag customNbt) {
        this.level.setBlock(this, Block.get(Block.AIR), true, true);
        EntityFallingBlock fall = createFallingEntity( customNbt);

        if (fall != null) {
            fall.spawnToAll();
        }
    }

    protected EntityFallingBlock createFallingEntity(CompoundTag customNbt) {
        CompoundTag nbt =
                Entity.getDefaultNBT(new Vector3(this.x + 0.5, this.y, this.z + 0.5)) 
                .putCompound("Block", CompoundTag.fromNetwork(this.blockstate.getBlockStateTag()).copy());

        for (var e : customNbt.getEntrySet()) {
            nbt.put(e.getKey(), e.getValue().copy());
        }

        return (EntityFallingBlock) Entity.createEntity(Entity.FALLING_BLOCK, this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
    }

    public Item toFallingItem() {
        return this.toItem();
    }
}