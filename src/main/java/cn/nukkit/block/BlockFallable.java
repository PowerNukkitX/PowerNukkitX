package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.event.block.BlockFallEvent;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.*;


/**
 * @author rcsuperman (Nukkit Project)
 */
public abstract class BlockFallable extends BlockSolid {

    public BlockFallable(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int onUpdate(int type) {
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
            }
            return type;
        }
        return 0;
    }

    public void drop(CompoundTag customNbt) {
        this.level.setBlock(this, Block.get(Block.AIR), true, true);
        EntityFallingBlock fall = createFallingEntity(customNbt);

        fall.spawnToAll();
    }

    protected EntityFallingBlock createFallingEntity(CompoundTag customNbt) {
        CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(this.x + 0.5))
                        .add(new DoubleTag(this.y))
                        .add(new DoubleTag(this.z + 0.5)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(0))
                        .add(new FloatTag(0)))
                .putCompound("Block", this.blockstate.getBlockStateTag().copy());

        for (var e : customNbt.getEntrySet()) {
            nbt.put(e.getKey(), e.getValue().copy());
        }

        EntityFallingBlock fall = (EntityFallingBlock) Entity.createEntity(Entity.FALLING_BLOCK, this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);

        if (fall != null) {
            fall.spawnToAll();
        }

        return fall;
    }
}
