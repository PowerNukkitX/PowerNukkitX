package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockTallDryGrass extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(TALL_DRY_GRASS);
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .canBeReplaced(true)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTallDryGrass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTallDryGrass(BlockState blockstate) {
        super(blockstate, DEFINITION);
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
        if (BlockDeadbush.isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }
}
