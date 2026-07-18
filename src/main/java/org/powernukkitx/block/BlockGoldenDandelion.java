package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kaooot
 */
public class BlockGoldenDandelion extends BlockFlower {

    public static final BlockProperties PROPERTIES = new BlockProperties(GOLDEN_DANDELION);
    public static final BlockDefinition DEFINITION = BlockFlower.DEFINITION.toBuilder()
            .canBeActivated(false)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGoldenDandelion() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockGoldenDandelion(BlockState blockstate) {
         super(blockstate, DEFINITION);
     }

    }