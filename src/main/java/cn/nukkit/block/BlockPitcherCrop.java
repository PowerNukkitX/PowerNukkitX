package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPitcherPod;
import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockPitcherCrop extends BlockCrops {

    public static final BlockProperties PROPERTIES = new BlockProperties(PITCHER_CROP, CommonBlockProperties.GROWTH, CommonBlockProperties.UPPER_BLOCK_BIT);

    public BlockPitcherCrop() { this(PROPERTIES.getDefaultState()); }

    public BlockPitcherCrop(BlockState state) { super(state); }

    @Override
    public @NotNull BlockProperties getProperties() { return PROPERTIES; }

    @Override
    public String getName() { return "Pitcher Crop"; }

    public boolean isUpper() { return this.getPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT); }

    private int nextGrowth(int current) {
        return switch (current) {
            case 0 -> 1;
            case 1 -> 3;
            case 3 -> 5;
            case 5 -> 7;
            default -> current;
        };
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if(!isUpper()) {
                if (!isFullyGrown()) {
                    if (ThreadLocalRandom.current().nextInt(5) == 0) {
                        setGrowth(nextGrowth(getGrowth()));
                        level.setBlock(getPosition(), this, true, true);
                    }
                } else {
                    Block above = up();
                    if (above.getId().equals(AIR)) {
                        BlockPitcherCrop upper = new BlockPitcherCrop();
                        upper.setPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT, true);
                        upper.setGrowth(7);
                        level.setBlock(above.getPosition(), upper, true, true);
                    }
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (isUpper()) return Item.EMPTY_ARRAY;

        if (!isFullyGrown()) {
            return new Item[]{new ItemPitcherPod()};
        }
        return new Item[]{ new ItemPitcherPod(), Item.get(PITCHER_PLANT, 0, 1)};
    }

    @Override
    public boolean onBreak(Item item) {
        if (!isUpper()) {
            Block above = up();
            if (above instanceof BlockPitcherCrop && ((BlockPitcherCrop) above).isUpper()) {
                level.setBlock(above.getPosition(), Block.get(AIR), true, true);
                level.setBlock(above.up().getPosition(), Block.get(AIR), true, true);
            }
        } else {
            Block below = down();
            if (below instanceof BlockPitcherCrop && !((BlockPitcherCrop) below).isUpper()) {
                level.setBlock(below.getPosition(), Block.get(AIR), true, true);
                level.setBlock(below.up().getPosition(), Block.get(AIR), true, true);
            }
        }
        super.onBreak(item);
        return true;
    }
}