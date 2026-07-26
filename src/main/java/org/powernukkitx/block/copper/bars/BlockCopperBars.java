package org.powernukkitx.block.copper.bars;

import org.powernukkitx.block.*;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.utils.LevelException;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.math.VectorMath.calculateFace;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockCopperBars extends BlockCopperBarBase implements BlockConnectable {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_BARS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperBars() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperBars(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Copper Bars";
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    /**
     * @see BlockThin
     */

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        final double offNW = 7.0 / 16.0;
        final double offSE = 9.0 / 16.0;
        final double onNW = 0.0;
        final double onSE = 1.0;
        double w = offNW;
        double e = offSE;
        double n = offNW;
        double s = offSE;
        try {
            boolean north = this.canConnect(this.north());
            boolean south = this.canConnect(this.south());
            boolean west = this.canConnect(this.west());
            boolean east = this.canConnect(this.east());
            w = west ? onNW : offNW;
            e = east ? onSE : offSE;
            n = north ? onNW : offNW;
            s = south ? onSE : offSE;
        } catch (LevelException ignore) {
            //null sucks
        }
        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1,
                this.z + s
        );
    }

    @Override
    public boolean canConnect(Block block) {
        return switch (block.getId()) {
            case GLASS_PANE, BLACK_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, BROWN_STAINED_GLASS_PANE,
                 CYAN_STAINED_GLASS_PANE, GRAY_STAINED_GLASS_PANE, GREEN_STAINED_GLASS_PANE,
                 LIGHT_BLUE_STAINED_GLASS_PANE, LIGHT_GRAY_STAINED_GLASS_PANE, LIME_STAINED_GLASS_PANE,
                 MAGENTA_STAINED_GLASS_PANE, ORANGE_STAINED_GLASS_PANE, PINK_STAINED_GLASS_PANE, PURPLE_STAINED_GLASS_PANE,
                 RED_STAINED_GLASS_PANE, WHITE_STAINED_GLASS_PANE, YELLOW_STAINED_GLASS_PANE, IRON_BARS, COBBLESTONE_WALL, COBBLED_DEEPSLATE_WALL ->
                    true;
            default -> {
                if (block instanceof BlockTrapdoor trapdoor) {
                    yield trapdoor.isOpen() && trapdoor.getBlockFace() == calculateFace(this, trapdoor);
                }
                yield block.isSolid();
            }
        };
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
