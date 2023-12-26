package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleButton extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:jungle_button", CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleButton(BlockState blockstate) {
        super(blockstate);
    }
}