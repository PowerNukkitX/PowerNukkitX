package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCherryButton extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cherry_button", CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryButton(BlockState blockstate) {
        super(blockstate);
    }
}