package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockBoneBlock extends BlockSolid{
    public static final BlockProperties PROPERTIES = new BlockProperties(BONE_BLOCK, CommonBlockProperties.DEPRECATED, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBoneBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBoneBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public String getName() {
        return "Bone Block";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (face.getIndex() == 2 || face.getIndex() == 3) {
            setPropertyValue(CommonBlockProperties.PILLAR_AXIS, BlockFace.Axis.Z);
        } else if (face.getIndex() == 4 || face.getIndex() == 5) {
            setPropertyValue(CommonBlockProperties.PILLAR_AXIS, BlockFace.Axis.Y);
        }
        this.getLevel().setBlock(block, this, true);
        return true;
    }

}