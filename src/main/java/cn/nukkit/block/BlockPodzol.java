package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.DirtType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;

/**
 * @author xtypr
 * @since 2015/11/22
 */
public class BlockPodzol extends BlockDirt {
    public static final BlockProperties $1 = new BlockProperties(PODZOL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPodzol() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPodzol(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Podzol";
    }

    @Override
    @NotNull public DirtType getDirtType() {
        return DirtType.NORMAL;
    }

    @Override
    public void setDirtType(@Nullable DirtType dirtType) throws Exception {
        if (dirtType != null) {
            throw new Exception(getName() + "don't support DirtType!");
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (!this.up().canBeReplaced()) {
            return false;
        }

        if (item.isShovel()) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(BlockID.GRASS_PATH));
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        }
        return false;
    }

}
