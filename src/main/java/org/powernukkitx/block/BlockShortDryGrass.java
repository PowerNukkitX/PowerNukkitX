package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.particle.BoneMealParticle;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockShortDryGrass extends BlockFlowable implements Supportable {
    public static final BlockProperties PROPERTIES = new BlockProperties(SHORT_DRY_GRASS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockShortDryGrass() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockShortDryGrass(BlockState blockstate) {
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
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (isSupportDirt(down())) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) {
            if (player != null && !player.isCreative()) {
                item.count--;
            }

            BlockTallDryGrass tallDryGrass = new BlockTallDryGrass();

            this.level.addParticle(new BoneMealParticle(this));
            this.level.setBlock(this, tallDryGrass, true, false);
            return true;
        }

        return false;
    }
}