package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTrialSpawner extends Block {
     public static final BlockProperties $1 = new BlockProperties(TRIAL_SPAWNER, CommonBlockProperties.TRIAL_SPAWNER_STATE);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockTrialSpawner(BlockState blockstate) {
         super(blockstate);
     }
}