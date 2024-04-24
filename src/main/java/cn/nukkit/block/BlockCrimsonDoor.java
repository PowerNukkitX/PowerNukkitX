package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.Sound;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonDoor(BlockState blockstate) {
        super(blockstate);
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