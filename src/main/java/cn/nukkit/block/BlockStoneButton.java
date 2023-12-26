package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStoneButton extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stone_button", CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStoneButton(BlockState blockstate) {
        super(blockstate);
    }
}