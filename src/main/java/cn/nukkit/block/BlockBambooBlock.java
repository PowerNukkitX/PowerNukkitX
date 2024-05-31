package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.*;

public class BlockBambooBlock extends BlockLog {
    public static final BlockProperties $1 = new BlockProperties(BAMBOO_BLOCK, PILLAR_AXIS);
    /**
     * @deprecated 
     */
    

    public BlockBambooBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBambooBlock(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public  BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Bamboo Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedBambooBlock.PROPERTIES.getDefaultState();
    }
}