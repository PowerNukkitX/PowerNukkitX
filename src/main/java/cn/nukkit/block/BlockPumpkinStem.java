package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockPumpkinStem extends BlockCropsStem {
    public static final BlockProperties $1 = new BlockProperties(PUMPKIN_STEM, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROWTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPumpkinStem() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPumpkinStem(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Pumpkin Stem";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getFruitId() {
        return PUMPKIN;
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }
}