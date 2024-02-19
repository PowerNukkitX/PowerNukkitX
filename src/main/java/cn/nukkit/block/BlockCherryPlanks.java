package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCherryPlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryPlanks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cherry Planks";
    }

}