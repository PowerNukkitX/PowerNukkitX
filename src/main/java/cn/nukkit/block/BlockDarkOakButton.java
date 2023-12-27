package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakButton extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dark_oak_button", CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakButton(BlockState blockstate) {
        super(blockstate);
    }
}