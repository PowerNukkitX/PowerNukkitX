package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.enums.DripstoneThickness;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.ATTACHED_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.DRIPSTONE_THICKNESS;
import static org.powernukkitx.block.property.CommonBlockProperties.HANGING;

/**
 * The ice counterpart of pointed dripstone from the 1.26.60 ice caves. It shares the dripstone
 * shape states, hangs from a ceiling or stands on a floor, and breaks when that support goes.
 * <p>
 * Placing one always leaves a tip. What {@code attached_bit} does is not known yet, so it is kept
 * as stored.
 *
 * @author xRookieFight
 * @since 15/10/2026
 */
public class BlockIcicle extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(ICICLE, ATTACHED_BIT, DRIPSTONE_THICKNESS, HANGING);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockIcicle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockIcicle(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Icicle";
    }

    public boolean isHanging() {
        return getPropertyValue(HANGING);
    }

    public void setHanging(boolean value) {
        setPropertyValue(HANGING, value);
    }

    public DripstoneThickness getThickness() {
        return getPropertyValue(DRIPSTONE_THICKNESS);
    }

    public void setThickness(DripstoneThickness value) {
        setPropertyValue(DRIPSTONE_THICKNESS, value);
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
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        final boolean hanging;
        if (face == BlockFace.DOWN) {
            hanging = true;
        } else if (face == BlockFace.UP) {
            hanging = false;
        } else {
            return false;
        }
        if (!this.isSupported(block, hanging)) {
            return false;
        }
        setHanging(hanging);
        setThickness(DripstoneThickness.TIP);
        return this.getLevel().setBlock(block, this, true, true);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL && !this.isSupported(this, this.isHanging())) {
            this.getLevel().useBreakOn(this);
            return type;
        }
        return 0;
    }

    // An icicle holds on to a solid face or to another icicle running the same way
    private boolean isSupported(Block at, boolean hanging) {
        final Block support = hanging ? at.up() : at.down();
        if (support instanceof BlockIcicle icicle) {
            return icicle.isHanging() == hanging;
        }
        return support.isSolid(hanging ? BlockFace.DOWN : BlockFace.UP);
    }
}
