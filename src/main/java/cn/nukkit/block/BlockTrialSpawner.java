package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTrialSpawner extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(TRIAL_SPAWNER, CommonBlockProperties.OMINOUS, CommonBlockProperties.TRIAL_SPAWNER_STATE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTrialSpawner(BlockState blockstate) {
        super(blockstate);
    }
}