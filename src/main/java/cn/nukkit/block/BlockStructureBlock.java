package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.StructureBlockType;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityStructBlock;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.STRUCTURE_BLOCK_TYPE;

public class BlockStructureBlock extends BlockSolid implements BlockEntityHolder<BlockEntityStructBlock> {
    public static final BlockProperties $1 = new BlockProperties(STRUCTURE_BLOCK, STRUCTURE_BLOCK_TYPE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockStructureBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockStructureBlock(BlockState blockstate) {
        super(blockstate);
    }

    public StructureBlockType getStructureBlockType() {
        return getPropertyValue(STRUCTURE_BLOCK_TYPE);
    }
    /**
     * @deprecated 
     */
    

    public void setStructureBlockType(StructureBlockType type) {
        setPropertyValue(STRUCTURE_BLOCK_TYPE, type);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            if (player.isCreative() && player.isOp()) {
                BlockEntityStructBlock $2 = this.getOrCreateBlockEntity();
                tile.spawnTo(player);
                player.addWindow(tile.getInventory());
            }
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false;
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true) != null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return getStructureBlockType().name() + "Structure Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return -1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 18000000;
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
    
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return player != null && player.isCreative();
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
    
    public boolean canBePulled() {
        return false;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityStructBlock> getBlockEntityClass() {
        return BlockEntityStructBlock.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.STRUCTURE_BLOCK;
    }
}