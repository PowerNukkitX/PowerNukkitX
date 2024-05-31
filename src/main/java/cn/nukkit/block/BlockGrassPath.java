package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/11/22
 */
public class BlockGrassPath extends BlockGrassBlock {
    public static final BlockProperties $1 = new BlockProperties(GRASS_PATH);
    /**
     * @deprecated 
     */
    

    public BlockGrassPath() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGrassPath(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Dirt Path";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.65;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.65;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.up().isSolid()) {
                this.level.setBlock(this, Block.get(BlockID.DIRT), false, true);
            }

            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this, get(FARMLAND), true);
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        }

        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTransparent() {
        return true;
    }
}
