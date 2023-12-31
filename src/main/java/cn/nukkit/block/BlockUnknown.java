package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockUnknown extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(UNKNOWN);

    public BlockUnknown() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnknown(BlockState blockstate) {
        super(blockstate);
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Unknown";
    }
}
