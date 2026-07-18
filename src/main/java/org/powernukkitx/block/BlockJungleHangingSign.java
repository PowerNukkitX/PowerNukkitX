package org.powernukkitx.block;


import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.ATTACHED_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.GROUND_SIGN_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.HANGING;

public class BlockJungleHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_HANGING_SIGN, ATTACHED_BIT, FACING_DIRECTION, GROUND_SIGN_DIRECTION, HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleHangingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleHangingSign(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Jungle Hanging Sign";
    }
}