package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateRedstoneOre extends BlockRedstoneOre {
    public static final BlockProperties $1 = new BlockProperties(DEEPSLATE_REDSTONE_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Deepslate Redstone Ore";
    }

    @Override
    /**
     * @deprecated 
     */
    
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