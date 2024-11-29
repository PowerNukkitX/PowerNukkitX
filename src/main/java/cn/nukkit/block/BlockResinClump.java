package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockResinClump extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(RESIN_CLUMP, CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockResinClump() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockResinClump(BlockState blockstate) {
        super(blockstate);
    }
}