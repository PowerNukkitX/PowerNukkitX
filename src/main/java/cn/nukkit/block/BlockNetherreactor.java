package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityNetherReactor;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author good777LUCKY
 */
public class BlockNetherreactor extends BlockSolid implements BlockEntityHolder<BlockEntityNetherReactor> {
    public static final BlockProperties $1 = new BlockProperties(NETHERREACTOR);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockNetherreactor() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockNetherreactor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getBlockEntityType() {
        return BlockEntity.NETHER_REACTOR;
    }

    @Override
    @NotNull public Class<? extends BlockEntityNetherReactor> getBlockEntityClass() {
        return BlockEntityNetherReactor.class;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Nether Reactor Core";
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 10;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 6;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    Item.get(ItemID.DIAMOND, 0, 3),
                    Item.get(ItemID.IRON_INGOT, 0, 6)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

}
