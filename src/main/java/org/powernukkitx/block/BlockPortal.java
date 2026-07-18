package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The alias is NetherPortal.
 */
public class BlockPortal extends BlockFlowable implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(PORTAL, CommonBlockProperties.PORTAL_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPortal() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPortal(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Nether Portal Block";
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return player != null && player.isCreative();
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public int getLightLevel() {
        return 11;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        boolean result = super.onBreak(item);
        for (BlockFace face : BlockFace.values()) {
            Block b = this.getSide(face);
            if (b != null) {
                if (b instanceof BlockPortal) {
                    result &= b.onBreak(item);
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.blockstate.specialValue() & 0x07);
    }
}