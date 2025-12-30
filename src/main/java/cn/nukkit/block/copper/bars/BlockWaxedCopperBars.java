package cn.nukkit.block.copper.bars;

import cn.nukkit.block.*;
import cn.nukkit.block.property.enums.OxidizationLevel;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.LevelException;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.math.VectorMath.calculateFace;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWaxedCopperBars extends BlockCopperBars implements BlockConnectable {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_COPPER_BARS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopperBars() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopperBars(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Copper Bars";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
