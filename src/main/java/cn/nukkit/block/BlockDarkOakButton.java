package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakButton extends BlockWoodenButton {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_BUTTON, CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakButton(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Dark Oak Button";
    }
}