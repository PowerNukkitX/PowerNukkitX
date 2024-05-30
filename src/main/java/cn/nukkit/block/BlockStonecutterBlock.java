package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.inventory.BlockInventoryHolder;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.StonecutterInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class BlockStonecutterBlock extends BlockTransparent implements Faceable, BlockInventoryHolder {

    public static final BlockProperties PROPERTIES = new BlockProperties(STONECUTTER_BLOCK, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStonecutterBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStonecutterBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Stonecutter";
    }

    @Override
    public void setBlockFace(BlockFace face) {
        int horizontalIndex = face.getHorizontalIndex();
        if (horizontalIndex > -1) {
            this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION,
                    CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(BlockFace.fromHorizontalIndex(horizontalIndex)));
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getHorizontalIndex()) : BlockFace.SOUTH);

        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            player.addWindow(getOrCreateInventory());
        }
        return true;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 17.5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockStonecutterBlock());
    }

    @Override
    public double getMaxY() {
        return y + 9 / 16.0;
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new StonecutterInventory(this);
    }
}
