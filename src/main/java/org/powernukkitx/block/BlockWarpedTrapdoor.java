package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.Sound;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedTrapdoor extends BlockTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);
    public static final BlockDefinition DEFINITION = BlockTrapdoor.DEFINITION.toBuilder()
            .burnChance(-1)
            .burnAbility(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedTrapdoor(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Warped Trapdoor";
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_NETHER_WOOD_TRAPDOOR);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_NETHER_WOOD_TRAPDOOR);
    }
}