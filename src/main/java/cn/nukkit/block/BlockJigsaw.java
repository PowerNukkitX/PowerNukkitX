package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockJigsaw extends BlockSolid implements Faceable {
    public static final BlockProperties $1 = new BlockProperties(JIGSAW, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.ROTATION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockJigsaw() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockJigsaw(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Jigsaw";
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
    
    public double getResistance() {
        return 18000000;
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
    
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
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
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
            double $2 = player.y + player.getEyeHeight();

            if (y - this.y > 2) {
                this.setBlockFace(BlockFace.UP);
            } else if (this.y - y > 0) {
                this.setBlockFace(BlockFace.DOWN);
            } else {
                this.setBlockFace(player.getHorizontalFacing().getOpposite());
            }
        } else {
            this.setBlockFace(player.getHorizontalFacing().getOpposite());
        }
        this.level.setBlock(block, this, true, false);

        return super.place(item, block, target, face, fx, fy, fz, player);
    }
}
