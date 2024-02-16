package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockPumpkinStem extends BlockCropsStem {
    public static final BlockProperties PROPERTIES = new BlockProperties(PUMPKIN_STEM, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROWTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPumpkinStem() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPumpkinStem(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Pumpkin Stem";
    }

    @Override
    public String getFruitId() {
        return PUMPKIN;
    }

    @Override
    public String getSeedsId() {
        return ItemID.PUMPKIN_SEEDS;
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedAcaciaLog.PROPERTIES.getDefaultState();
    }

    @Override
    public BlockFace getBlockFace() {
        return getFacing();
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }
}