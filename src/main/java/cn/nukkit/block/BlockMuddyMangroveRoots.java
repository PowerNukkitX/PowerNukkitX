package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockMuddyMangroveRoots extends BlockSolid implements Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(MUDDY_MANGROVE_ROOTS, CommonBlockProperties.PILLAR_AXIS);

    public BlockMuddyMangroveRoots() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMuddyMangroveRoots(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Muddy Mangrove Roots";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 0.7;
    }

    @Override
    public double getResistance() {
        return 0.7;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(CommonBlockProperties.PILLAR_AXIS);
    }

    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(CommonBlockProperties.PILLAR_AXIS, axis);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setPillarAxis(face.getAxis());
        getLevel().setBlock(block, this, true, true);
        return true;
    }
}
