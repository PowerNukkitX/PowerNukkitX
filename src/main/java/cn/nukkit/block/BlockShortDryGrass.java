package cn.nukkit.block;

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
}