package cn.nukkit.block;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/6
 */
public class BlockGlassPane extends BlockThin {

    public static final BlockProperties PROPERTIES = new BlockProperties(GLASS_PANE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGlassPane() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockGlassPane(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Glass Pane";
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
