package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.Sound;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonDoor extends BlockWoodenDoor {
    public static final BlockProperties $1 = new BlockProperties(CRIMSON_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonDoor() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Crimson Door Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_NETHER_WOOD_DOOR);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_NETHER_WOOD_DOOR);
    }
}