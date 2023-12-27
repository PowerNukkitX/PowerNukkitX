package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveButton extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_button", CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveButton(BlockState blockstate) {
        super(blockstate);
    }
}