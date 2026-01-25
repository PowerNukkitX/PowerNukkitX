package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * The alias is Still water.
 */
public class BlockWater extends BlockFlowingWater {
    public static final BlockProperties PROPERTIES = new BlockProperties(WATER, CommonBlockProperties.LIQUID_DEPTH);

    public BlockWater() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWater(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        return this.getLevel().setBlock(this, this, true, false);
    }

    @Override
    public String getName() {
        return "Still Water";
    }

    @Override
    public BlockLiquid getLiquidWithNewDepth(int depth) {
        return new BlockWater(this.blockstate.setPropertyValue(PROPERTIES, CommonBlockProperties.LIQUID_DEPTH.createValue(depth)));
    }
}
