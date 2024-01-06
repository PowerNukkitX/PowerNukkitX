package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneButton extends BlockStoneButton {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE_BUTTON, CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneButton(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Polished Blackstone Button";
    }
}