package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockBambooButton extends BlockWoodenButton {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:bamboo_button", CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooButton(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Bamboo Button";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}