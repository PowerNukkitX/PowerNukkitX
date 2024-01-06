package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonButton extends BlockWoodenButton {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_BUTTON, CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonButton(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Crimson Button";
    }
}