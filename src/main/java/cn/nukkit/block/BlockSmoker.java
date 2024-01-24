package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmoker extends BlockLitSmoker {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMOKER, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoker() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoker(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Smoker";
    }

    @Override
    public int getLightLevel() {
        return 0;
    }
}
