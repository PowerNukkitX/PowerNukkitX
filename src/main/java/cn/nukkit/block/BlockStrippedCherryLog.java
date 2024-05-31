package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedCherryLog extends BlockWoodStripped {
    public static final BlockProperties $1 = new BlockProperties(STRIPPED_CHERRY_LOG, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockStrippedCherryLog() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockStrippedCherryLog(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Stripped Cherry Log";
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedAcaciaLog.PROPERTIES.getDefaultState();
    }

    @Override
    public WoodType getWoodType() {
        throw new UnsupportedOperationException();
    }
}