package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.Sound;
import org.jetbrains.annotations.NotNull;


public class BlockBambooDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Bamboo Door Block";
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_BAMBOO_WOOD_DOOR);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_BAMBOO_WOOD_DOOR);
    }
}