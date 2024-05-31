package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemGlowstoneDust;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * @author xtypr
 * @since 2015/12/6
 */
public class BlockGlowstone extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(GLOWSTONE);
    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGlowstone() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGlowstone(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Glowstone";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 1.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 15;
    }

    @Override
    public Item[] getDrops(Item item) {
        Random $2 = new Random();
        int $3 = 2 + random.nextInt(3);

        Enchantment $4 = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        if (fortune != null && fortune.getLevel() >= 1) {
            count += random.nextInt(fortune.getLevel() + 1);
        }

        return new Item[]{
                new ItemGlowstoneDust(0, MathHelper.clamp(count, 1, 4))
        };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }
}
