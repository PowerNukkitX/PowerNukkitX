package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateIronOre extends BlockIronOre {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_iron_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateIronOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateIronOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Deepslate Iron Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }
}