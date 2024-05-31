package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOxeyeDaisy extends BlockFlower {
    public static final BlockProperties $1 = new BlockProperties(OXEYE_DAISY);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockOxeyeDaisy() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockOxeyeDaisy(BlockState blockstate) {
        super(blockstate);
    }
}