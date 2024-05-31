package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMelonSlice;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlockMelonBlock extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(MELON_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMelonBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMelonBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Melon Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 5;
    }

    @Override
    public Item[] getDrops(Item item) {
        Random $2 = new Random();
        int $3 = 3 + random.nextInt(5);

        Enchantment $4 = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        if (fortune != null && fortune.getLevel() >= 1) {
            count += random.nextInt(fortune.getLevel() + 1);
        }

        return new Item[]{
                new ItemMelonSlice(0, Math.min(9, count))
        };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public  boolean sticksToPiston() {
        return false;
    }
}