package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkShrieker;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockSculkShrieker extends BlockSolid implements BlockEntityHolder<BlockEntitySculkShrieker> {

    public static final BlockProperties PROPERTIES = new BlockProperties(SCULK_SHRIEKER, CommonBlockProperties.ACTIVE, CommonBlockProperties.CAN_SUMMON);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSculkShrieker() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSculkShrieker(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Sculk Shrieker";
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 3.0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @NotNull
    @Override
    public Class<? extends BlockEntitySculkShrieker> getBlockEntityClass() {
        return BlockEntitySculkShrieker.class;
    }

    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.SCULK_SHRIEKER;
    }

}
