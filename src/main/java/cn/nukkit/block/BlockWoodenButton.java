package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenButton extends BlockButton {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:wooden_button", CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWoodenButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWoodenButton(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Oak Button";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}