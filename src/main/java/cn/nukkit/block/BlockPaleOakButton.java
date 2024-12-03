package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPaleOakButton extends BlockWoodenButton {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_BUTTON, CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleOakButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleOakButton(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Pale Oak Button";
    }
}