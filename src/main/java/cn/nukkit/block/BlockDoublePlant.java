package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.DoublePlantType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.DOUBLE_PLANT_TYPE;
import static cn.nukkit.block.property.CommonBlockProperties.UPPER_BLOCK_BIT;

/**
 * @author xtypr
 * @since 2015/11/23
 */
public class BlockDoublePlant extends BlockFlowable {
    public static final BlockProperties $1 = new BlockProperties(DOUBLE_PLANT, DOUBLE_PLANT_TYPE, UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDoublePlant() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDoublePlant(BlockState blockstate) {
        super(blockstate);
    }

    @NotNull public DoublePlantType getDoublePlantType() {
        return getPropertyValue(DOUBLE_PLANT_TYPE);
    }
    /**
     * @deprecated 
     */
    

    public void setDoublePlantType(@NotNull DoublePlantType type) {
        setPropertyValue(DOUBLE_PLANT_TYPE, type);
    }
    /**
     * @deprecated 
     */
    

    public boolean isTopHalf() {
        return getPropertyValue(UPPER_BLOCK_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setTopHalf(boolean topHalf) {
        setPropertyValue(UPPER_BLOCK_BIT, topHalf);
    }

    @Override
    public Item toItem() {
        int $2 = getDoublePlantType().ordinal();
        return new ItemBlock(this, aux);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeReplaced() {
        return getDoublePlantType() == DoublePlantType.GRASS || getDoublePlantType() == DoublePlantType.FERN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return getDoublePlantType().name();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isTopHalf()) {
                // Top
                if (!this.down().getId().equals(DOUBLE_PLANT)) {
                    this.getLevel().setBlock(this, Block.get(BlockID.AIR), false, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else {
                // Bottom
                if (!this.up().getId().equals(DOUBLE_PLANT) || !isSupportValid(down())) {
                    this.getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block $3 = up();

        if (up.isAir() && isSupportValid(down())) {
            setTopHalf(false);
            this.getLevel().setBlock(block, this, true, false); // If we update the bottom half, it will drop the item because there isn't a flower block above

            setTopHalf(true);
            this.getLevel().setBlock(up, this, true, true);
            this.getLevel().updateAround(this);
            return true;
        }

        return false;
    }

    
    /**
     * @deprecated 
     */
    private boolean isSupportValid(Block support) {
        return switch (support.getId()) {
            case GRASS_BLOCK, DIRT, PODZOL, FARMLAND, MYCELIUM, DIRT_WITH_ROOTS, MOSS_BLOCK -> true;
            default -> false;
        };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        Block $4 = down();

        if (isTopHalf()) { // Top half
            this.getLevel().useBreakOn(down);
        } else {
            this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        }

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (isTopHalf()) {
            return Item.EMPTY_ARRAY;
        }

        if (getDoublePlantType() == DoublePlantType.GRASS || getDoublePlantType() == DoublePlantType.FERN) {
            boolean $5 = ThreadLocalRandom.current().nextInt(10) == 0;
            if (item.isShears()) {
                //todo enchantment
                if (dropSeeds) {
                    return new Item[]{
                            Item.get(ItemID.WHEAT_SEEDS),
                            toItem()
                    };
                } else {
                    return new Item[]{
                            toItem()
                    };
                }
            }
            if (dropSeeds) {
                return new Item[]{
                        Item.get(ItemID.WHEAT_SEEDS)
                };
            } else {
                return Item.EMPTY_ARRAY;
            }
        }

        return new Item[]{toItem()};
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
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) { //Bone meal
            switch (getDoublePlantType()) {
                case SUNFLOWER:
                case SYRINGA:
                case ROSE:
                case PAEONIA:
                    if (player != null && (player.gamemode & 0x01) == 0) {
                        item.count--;
                    }
                    this.level.addParticle(new BoneMealParticle(this));
                    this.level.dropItem(this, this.toItem());
            }

            return true;
        }

        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isFertilizable() {
        return true;
    }
}
