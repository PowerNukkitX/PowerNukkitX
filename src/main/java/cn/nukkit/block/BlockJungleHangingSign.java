package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.*;

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