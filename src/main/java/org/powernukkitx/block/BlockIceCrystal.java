package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_BLOCK_FACE;

/**
 * A crystal growing out of the side of a block in the 1.26.60 ice caves. It points away from the
 * block it grows on and breaks once that block is gone, like an amethyst cluster.
 *
 * @author xRookieFight
 * @since 15/10/2026
 */
public class BlockIceCrystal extends BlockTransparent implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(ICE_CRYSTAL, MINECRAFT_BLOCK_FACE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockIceCrystal() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockIceCrystal(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Ice Crystal";
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(MINECRAFT_BLOCK_FACE);
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(MINECRAFT_BLOCK_FACE, face);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!target.isSolid(face)) {
            return false;
        }
        setBlockFace(face);
        return this.getLevel().setBlock(block, this, true, true);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL && !this.getSide(this.getBlockFace().getOpposite()).isSolid(this.getBlockFace())) {
            this.getLevel().useBreakOn(this);
            return type;
        }
        return 0;
    }
}
