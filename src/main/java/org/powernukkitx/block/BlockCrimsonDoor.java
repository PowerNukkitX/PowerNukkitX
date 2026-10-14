package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.Sound;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_DOOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT, CommonBlockProperties.DOOR_HINGE_BIT);
    public static final BlockDefinition DEFINITION = BlockWoodenDoor.DEFINITION.toBuilder()
            .burnChance(-1)
            .burnAbility(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonDoor(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Crimson Door Block";
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