package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleHangingSign extends BlockHangingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_HANGING_SIGN,
            CommonBlockProperties.ATTACHED_BIT,
            CommonBlockProperties.FACING_DIRECTION,
            CommonBlockProperties.GROUND_SIGN_DIRECTION,
            CommonBlockProperties.HANGING);

    @Override
    public @NotNull BlockProperties getProperties() {
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