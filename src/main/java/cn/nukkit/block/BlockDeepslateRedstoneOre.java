package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateRedstoneOre extends BlockRedstoneOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_REDSTONE_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Deepslate Redstone Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public Block getLitBlock() {
        return new BlockLitDeepslateRedstoneOre();
    }

    @Override
    public Block getUnlitBlock() {
        return new BlockDeepslateRedstoneOre();
    }
}