package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.inventory.BlockInventoryHolder;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.LoomInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @implNote Faceable since FUTURE
 */

public class BlockLoom extends BlockSolid implements Faceable, BlockInventoryHolder {
    public static final BlockProperties $1 = new BlockProperties(LOOM, CommonBlockProperties.DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLoom() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLoom(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Loom";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockLoom());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 12.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 2.5;
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
            Item $2 = player.getInventory().getItemInHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
                return false;
            }
            player.addWindow(getOrCreateInventory());
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            setBlockFace(player.getDirection().getOpposite());
        }
        this.level.setBlock(this, this, true, true);
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.getHorizontals()[getPropertyValue(CommonBlockProperties.DIRECTION)];
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, face.getHorizontalIndex());
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new LoomInventory(this);
    }
}
