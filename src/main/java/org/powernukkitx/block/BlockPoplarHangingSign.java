package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.ATTACHED_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.GROUND_SIGN_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.HANGING;

public class BlockPoplarHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_HANGING_SIGN, ATTACHED_BIT, FACING_DIRECTION, GROUND_SIGN_DIRECTION, HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoplarHangingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoplarHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Poplar Hanging Sign";
    }
}
