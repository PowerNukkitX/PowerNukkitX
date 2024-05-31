package cn.nukkit.block;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockLitRedstoneOre extends BlockRedstoneOre implements IBlockOreRedstoneGlowing {
    public static final BlockProperties $1 = new BlockProperties(LIT_REDSTONE_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLitRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLitRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Glowing Redstone Ore";
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