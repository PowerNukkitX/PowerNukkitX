package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockCandleCake extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(CANDLE_CAKE, CommonBlockProperties.LIT);
    /**
     * @deprecated 
     */
    

    public BlockCandleCake(BlockState blockState) {
        super(blockState);
    }
    /**
     * @deprecated 
     */
    

    public BlockCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Cake Block With " + getColorName() + " Candle";
    }

    
    /**
     * @deprecated 
     */
    protected String getColorName() {
        return "Simple";
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinX() {
        return this.x + (1 + blockstate.specialValue() * 2) / 16d;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinY() {
        return this.y;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        return this.x - 0.0625 + 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return this.y + 0.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        return this.z - 0.0625 + 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!down().isAir()) {
            getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().isAir()) {
                getLevel().setBlock(this, Block.get(BlockID.AIR), true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    public BlockCandle toCandleForm() {
        return new BlockCandle();
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toCandleForm().toItem()};
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (getPropertyValue(CommonBlockProperties.LIT) && !Objects.equals(item.getId(), ItemID.FLINT_AND_STEEL)) {
            setPropertyValue(CommonBlockProperties.LIT, false);
            getLevel().addSound(this, Sound.RANDOM_FIZZ);
            getLevel().setBlock(this, this, true, true);
            return true;
        } else if (!getPropertyValue(CommonBlockProperties.LIT) && Objects.equals(item.getId(), ItemID.FLINT_AND_STEEL)) {
            setPropertyValue(CommonBlockProperties.LIT, true);
            getLevel().addSound(this, Sound.FIRE_IGNITE);
            getLevel().setBlock(this, this, true, true);
            return true;
        } else if (player != null && (player.getFoodData().isHungry() || player.isCreative())) {
            final Block $2 = new BlockCake();
            this.getLevel().setBlock(this, cake, true, true);
            this.getLevel().dropItem(this.add(0.5, 0.5, 0.5), getDrops(null)[0]);
            return this.getLevel().getBlock(this).onActivate(Item.get(AIR), player, blockFace, fx, fy, fz);
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getComparatorInputOverride() {
        return 14;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasComparatorInputOverride() {
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
    
    public boolean sticksToPiston() {
        return false;
    }
}