package cn.nukkit.block;

import cn.nukkit.level.generator.object.ObjectBigMushroom;
import org.jetbrains.annotations.NotNull;

public class BlockRedMushroom extends BlockMushroom {
    public static final BlockProperties $1 = new BlockProperties(RED_MUSHROOM);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockRedMushroom() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockRedMushroom(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Red Mushroom";
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
        return ObjectBigMushroom.MushroomType.RED;
    }
}