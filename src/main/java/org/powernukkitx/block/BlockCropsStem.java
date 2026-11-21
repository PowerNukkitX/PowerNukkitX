package org.powernukkitx.block;

import org.powernukkitx.Server;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.event.block.BlockGrowEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author joserobjr
 * @since 2020-09-15
 */


public abstract class BlockCropsStem extends BlockCrops implements Faceable {

    public abstract BlockState getStrippedState();

    public BlockCropsStem(BlockState blockstate) {
        super(blockstate);
    }

    public abstract String getFruitId();

    public abstract String getSeedsId();

    @Override
    public BlockFace getBlockFace() {
        return getFacing();
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != FARMLAND) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            BlockFace blockFace = getBlockFace();
            if (blockFace.getAxis().isHorizontal() && getSide(blockFace).getId() != getFruitId()) {
                setBlockFace(BlockFace.DOWN);
                getLevel().setBlock(this, this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            return 0;
        }
        
        if (type != Level.BLOCK_UPDATE_RANDOM) {
            return 0;
        }
        
        if (ThreadLocalRandom.current().nextInt(1, 3) != 1 
                || getLevel().getFullLight(this) < MINIMUM_LIGHT_LEVEL) {
            return Level.BLOCK_UPDATE_RANDOM;
        }
        
        int growth = getGrowth();
        if (growth < CommonBlockProperties.GROWTH.getMax()) {
            BlockCropsStem block = this.clone();
            block.setGrowth(growth + 1);
            BlockGrowEvent ev = new BlockGrowEvent(this, block);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                this.getLevel().setBlock(this, ev.getNewState(), true);
            }
            return Level.BLOCK_UPDATE_RANDOM;
        }

        growFruit();
        return Level.BLOCK_UPDATE_RANDOM;
    }

    public boolean growFruit() {
        String fruitId = getFruitId();
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block b = this.getSide(face);
            if (b.getId().equals(fruitId)) {
                return false;
            }
        }
        
        BlockFace sideFace = BlockFace.Plane.HORIZONTAL.random();
        Block side = this.getSide(sideFace);
        Block d = side.down();
        if (side.isAir() && (d.getId().equals(FARMLAND) || d.getId().equals(GRASS_BLOCK) || d.getId().equals(DIRT))) {
            BlockGrowEvent ev = new BlockGrowEvent(side, Block.get(fruitId));
            Server.getInstance().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                this.getLevel().setBlock(side, ev.getNewState(), true);
                setBlockFace(sideFace);
                this.getLevel().setBlock(this, this, true);
            }
        }
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(getSeedsId());
    }

    @Override
    public Item[] getDrops(Item item) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int growth = getGrowth();
        int count = 0;
        for (int i = 0; i < 3; i++) {
            if (random.nextInt(15) <= growth) {
                count++;
            }
        }
        
        if (count == 0) {
            return Item.EMPTY_ARRAY;
        }
        
        return new Item[]{
                Item.get(getSeedsId(), 0, count)
        };
    }

    @Override
    public BlockCropsStem clone() {
        return (BlockCropsStem) super.clone();
    }


}
