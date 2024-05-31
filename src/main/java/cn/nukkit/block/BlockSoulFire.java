package cn.nukkit.block;

import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.AGE_16;

public class BlockSoulFire extends BlockFire {
    public static final BlockProperties $1 = new BlockProperties(SOUL_FIRE, AGE_16);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSoulFire() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSoulFire(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Soul Fire Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            String $2 = down().getId();
            if (!downId.equals(Block.SOUL_SAND) && !downId.equals(Block.SOUL_SOIL)) {
                this.getLevel().setBlock(this, Block.get(FIRE).setPropertyValue(AGE_16, this.getAge()));
            }
            return type;
        }
        return 0;
    }
}