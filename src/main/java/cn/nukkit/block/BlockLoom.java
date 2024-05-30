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
    public static final BlockProperties PROPERTIES = new BlockProperties(LOOM, CommonBlockProperties.DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLoom() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLoom(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Loom";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockLoom());
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            Item itemInHand = player.getInventory().getItemInHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
                return false;
            }
            player.addWindow(getOrCreateInventory());
        }
        return true;
    }

    @Override
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
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, face.getHorizontalIndex());
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new LoomInventory(this);
    }
}
