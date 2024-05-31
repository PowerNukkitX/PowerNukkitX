package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.Sound;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonFenceGate extends BlockFenceGate {
    public static final BlockProperties $1 = new BlockProperties(CRIMSON_FENCE_GATE, CommonBlockProperties.DIRECTION, CommonBlockProperties.IN_WALL_BIT, CommonBlockProperties.OPEN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonFenceGate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonFenceGate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Crimson Fence Gate";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_NETHER_WOOD_FENCE_GATE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_NETHER_WOOD_FENCE_GATE);
    }
}