package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBlastFurnace;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

public class BlockLitBlastFurnace extends BlockLitFurnace {
    public static final BlockProperties $1 = new BlockProperties(LIT_BLAST_FURNACE, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLitBlastFurnace() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLitBlastFurnace(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Burning Blast Furnace";
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getBlockEntityType() {
        return BlockEntity.BLAST_FURNACE;
    }

    @Override
    @NotNull public Class<? extends BlockEntityBlastFurnace> getBlockEntityClass() {
        return BlockEntityBlastFurnace.class;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBlastFurnace());
    }
}