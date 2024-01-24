package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBambooButton extends BlockWoodenButton {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_BUTTON, CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooButton(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Bamboo Button";
    }

}