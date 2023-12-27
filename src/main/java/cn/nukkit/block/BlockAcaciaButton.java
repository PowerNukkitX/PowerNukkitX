package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaButton extends BlockWoodenButton {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:acacia_button", CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaButton(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Acacia Button";
    }
}