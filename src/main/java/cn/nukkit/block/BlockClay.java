package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemClayBall;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author Nukkit Project Team
 */
public class BlockClay extends BlockSolid implements Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(CLAY);

    public BlockClay() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockClay(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return "Clay Block";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public Item[] getDrops(Item item) {
        Item clayBall = new ItemClayBall();
        clayBall.setCount(4);
        return new Item[]{
                clayBall
        };
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
