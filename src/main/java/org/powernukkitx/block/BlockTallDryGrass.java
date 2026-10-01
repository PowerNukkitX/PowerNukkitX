package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockTallDryGrass extends BlockFlowable implements Supportable {
    public static final BlockProperties PROPERTIES = new BlockProperties(TALL_DRY_GRASS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTallDryGrass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTallDryGrass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public int getSnowloggingLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return "Tall dry grass";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (isSupportDirtSandClay(down())) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportDirtSandClay(down(1, 0))) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canHarvest(Item item) {
        return item.isShears();
    }
}
