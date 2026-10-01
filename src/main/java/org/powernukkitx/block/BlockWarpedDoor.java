package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.Sound;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_DOOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT, CommonBlockProperties.DOOR_HINGE_BIT);
    public static final BlockDefinition DEFINITION = BlockWoodenDoor.DEFINITION.toBuilder()
            .burnChance(-1)
            .burnAbility(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedDoor(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Warped Door Block";
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_NETHER_WOOD_DOOR);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_NETHER_WOOD_DOOR);
    }

    
    }