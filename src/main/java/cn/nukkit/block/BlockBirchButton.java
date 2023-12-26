package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchButton extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:birch_button", CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchButton(BlockState blockstate) {
        super(blockstate);
    }
}