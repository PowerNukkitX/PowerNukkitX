package cn.nukkit.block;

import cn.nukkit.level.generator.object.ObjectBigMushroom;
import org.jetbrains.annotations.NotNull;

public class BlockBrownMushroom extends BlockMushroom {
    public static final BlockProperties $1 = new BlockProperties(BROWN_MUSHROOM);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownMushroom() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownMushroom(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Brown Mushroom";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 1;
    }

    @Override
    protected ObjectBigMushroom.MushroomType getType() {
        return ObjectBigMushroom.MushroomType.BROWN;
    }
}