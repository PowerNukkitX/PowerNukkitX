package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowFlower extends BlockFlower {
    public static final BlockProperties $1 = new BlockProperties(YELLOW_FLOWER);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowFlower() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowFlower(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block getUncommonFlower() {
        return get(RED_TULIP);
    }
}