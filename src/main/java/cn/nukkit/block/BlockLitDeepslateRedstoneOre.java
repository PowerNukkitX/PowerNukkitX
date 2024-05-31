package cn.nukkit.block;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockLitDeepslateRedstoneOre extends BlockDeepslateRedstoneOre implements IBlockOreRedstoneGlowing {
    public static final BlockProperties $1 = new BlockProperties(LIT_DEEPSLATE_REDSTONE_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLitDeepslateRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLitDeepslateRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Glowing Deepslate Redstone Ore";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 9;
    }

    @Override
    public Item toItem() {
        return IBlockOreRedstoneGlowing.super.toItem();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        return IBlockOreRedstoneGlowing.super.onUpdate(this, type);
    }
}