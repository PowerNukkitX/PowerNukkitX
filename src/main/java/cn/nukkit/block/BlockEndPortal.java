package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityEndPortal;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockEndPortal extends BlockFlowable implements BlockEntityHolder<BlockEntityEndPortal> {

    public static final BlockProperties $1 = new BlockProperties(END_PORTAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockEndPortal() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockEndPortal(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "End Portal Block";
    }

    @Override
    @NotNull public Class<? extends BlockEntityEndPortal> getBlockEntityClass() {
        return BlockEntityEndPortal.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getBlockEntityType() {
        return BlockEntity.END_PORTAL;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
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
    
    public int getLightLevel() {
        return 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this;
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
    
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
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
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return getY() + (12.0 / 16.0);
    }
    /**
     * @deprecated 
     */
    

    public static void spawnObsidianPlatform(Position position) {
        Level $2 = position.getLevel();
        int $3 = position.getFloorX();
        int $4 = position.getFloorY();
        int $5 = position.getFloorZ();

        for (int $6 = x - 2; blockX <= x + 2; blockX++) {
            for (int $7 = z - 2; blockZ <= z + 2; blockZ++) {
                level.setBlockStateAt(blockX, y - 1, blockZ, PROPERTIES.getDefaultState());
                for (int $8 = y; blockY <= y + 3; blockY++) {
                    level.setBlockStateAt(blockX, blockY, blockZ, BlockAir.PROPERTIES.getDefaultState());
                }
            }
        }
    }
}
