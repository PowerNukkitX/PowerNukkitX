package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockMelonStem extends BlockCropsStem {
    public static final BlockProperties PROPERTIES = new BlockProperties(MELON_STEM, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROWTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMelonStem() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMelonStem(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Melon Stem";
    }

    @Override
    public String getFruitId() {
        return MELON_BLOCK;
    }

    @Override
    public String getSeedsId() {
        return ItemID.MELON_SEEDS;
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