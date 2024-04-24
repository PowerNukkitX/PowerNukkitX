package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.Sound;
import org.jetbrains.annotations.NotNull;

public class BlockCherryDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cherry Door Block";
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_CHERRY_WOOD_DOOR);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_CHERRY_WOOD_DOOR);
    }
}