package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenButton extends BlockButton {
    public static final BlockProperties PROPERTIES = new BlockProperties(WOODEN_BUTTON, CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockButton.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_AXE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWoodenButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWoodenButton(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Oak Button";
    }
}