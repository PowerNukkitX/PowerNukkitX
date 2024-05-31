package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySculkCatalyst;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSculkCatalyst extends BlockSolid implements BlockEntityHolder<BlockEntitySculkCatalyst> {
    public static final BlockProperties $1 = new BlockProperties(SCULK_CATALYST, CommonBlockProperties.BLOOM);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSculkCatalyst() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSculkCatalyst(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Sculk Catalyst";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePulled() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePushed() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 3.0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    @NotNull public Class<? extends BlockEntitySculkCatalyst> getBlockEntityClass() {
        return BlockEntitySculkCatalyst.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getBlockEntityType() {
        return BlockEntity.SCULK_CATALYST;
    }

}
