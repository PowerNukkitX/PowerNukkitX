package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public class BlockSporeBlossom extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(SPORE_BLOSSOM);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSporeBlossom() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSporeBlossom(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Spore Blossom";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (target.isSolid() && face == BlockFace.DOWN) {
            return super.place(item, block, target, face, fx, fy, fz, player);
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
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
