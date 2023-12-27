package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBoneBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:bone_block", CommonBlockProperties.DEPRECATED, CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBoneBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBoneBlock(BlockState blockstate) {
        super(blockstate);
    }
}