package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinitions;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenButton extends BlockButton {
    public static final BlockProperties PROPERTIES = new BlockProperties(WOODEN_BUTTON, CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWoodenButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWoodenButton(BlockState blockstate) {
        super(blockstate, BlockDefinitions.WOODEN_BUTTON);
    }

    @Override
    public String getName() {
        return "Oak Button";
    }
}