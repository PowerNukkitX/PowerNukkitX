package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockShortDryGrass extends BlockFlowable {
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
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (BlockSweetBerryBush.isSupportValid(down())) {
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