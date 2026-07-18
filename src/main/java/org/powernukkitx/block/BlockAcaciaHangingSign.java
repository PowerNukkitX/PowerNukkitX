package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.ATTACHED_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.GROUND_SIGN_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.HANGING;

public class BlockAcaciaHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(ACACIA_HANGING_SIGN, ATTACHED_BIT, FACING_DIRECTION, GROUND_SIGN_DIRECTION, HANGING);
    public BlockAcaciaHangingSign() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public String getName() {
        return "Acacia Hanging Sign";
    }
}