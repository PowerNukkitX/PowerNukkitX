package org.powernukkitx.block;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityFallingBlock;
import org.powernukkitx.event.block.BlockFallEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.block.definition.BlockDefinition;


/**
 * @author rcsuperman (Nukkit Project)
 */
public abstract class BlockFallable extends BlockSolid {
    public static final BlockDefinition FALLABLE = SOLID.toBuilder()
            .build();

    public BlockFallable(BlockState blockstate) {
        super(blockstate, FALLABLE);
    }

    public BlockFallable(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
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
                return type;
            }
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
                .putCompound("Block", CompoundTag.fromNetwork(this.blockstate.getBlockStateTag()).copy());

        for (var e : customNbt.getEntrySet()) {
            nbt.put(e.getKey(), e.getValue().copy());
        }

        EntityFallingBlock fall = (EntityFallingBlock) Entity.createEntity(Entity.FALLING_BLOCK, this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);

        if (fall != null) {
            fall.spawnToAll();
        }

        return fall;
    }

    public Item toFallingItem() {
        return this.toItem();
    }
}