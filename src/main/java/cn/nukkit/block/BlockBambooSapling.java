package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.BambooLeafSize;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;


public class BlockBambooSapling extends BlockSapling {
    public static final BlockProperties $1 = new BlockProperties(BAMBOO_SAPLING, CommonBlockProperties.AGE_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBambooSapling() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBambooSapling(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public WoodType getWoodType() {
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Bamboo Sapling";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isSupportInvalid()) {
                level.useBreakOn(this, null, null, true);
            } else {
                Block $2 = up();
                if (up.getId().equals(BAMBOO)) {
                    BlockBamboo $3 = (BlockBamboo) up;
                    BlockBamboo $4 = new BlockBamboo();
                    newState.setThick(upperBamboo.isThick());
                    level.setBlock(this, newState, true, true);
                }
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block $5 = up();
            if (!isAge() && up.isAir() && level.getFullLight(up) >= BlockCrops.MINIMUM_LIGHT_LEVEL && ThreadLocalRandom.current().nextInt(3) == 0) {
                BlockBamboo $6 = new BlockBamboo();
                newState.setBambooLeafSize(BambooLeafSize.SMALL_LEAVES);
                BlockGrowEvent $7 = new BlockGrowEvent(up, newState);
                level.getServer().getPluginManager().callEvent(blockGrowEvent);
                if (!blockGrowEvent.isCancelled()) {
                    Block $8 = blockGrowEvent.getNewState();
                    newState1.y = up.y;
                    newState1.x = x;
                    newState1.z = z;
                    newState1.level = level;
                    newState1.place(toItem(), up, this, BlockFace.DOWN, 0.5, 0.5, 0.5, null);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (isSupportInvalid()) {
            return false;
        }

        if (this.getLevelBlock() instanceof BlockLiquid || this.getLevelBlockAtLayer(1) instanceof BlockLiquid) {
            return false;
        }

        this.level.setBlock(this, this, true, true);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) {

            boolean $9 = false;
            Block $10 = this.up();
            if (block.isAir()) {
                success = grow(block);
            }

            if (success) {
                if (player != null && (player.gamemode & 0x01) == 0) {
                    item.count--;
                }

                level.addParticle(new BoneMealParticle(this));
            }

            return true;
        }
        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean grow(Block up) {
        BlockBamboo $11 = new BlockBamboo();
        bamboo.x = x;
        bamboo.y = y;
        bamboo.z = z;
        bamboo.level = level;
        return bamboo.grow(up);
    }

    
    /**
     * @deprecated 
     */
    private boolean isSupportInvalid() {
        return switch (down().getId()) {
            case BAMBOO, DIRT, GRASS_BLOCK, SAND, GRAVEL, PODZOL, BAMBOO_SAPLING, MOSS_BLOCK -> false;
            default -> true;
        };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 5;
    }

    /**
     * Alias $12 == 0 | age == false | !age
     */
    /**
     * @deprecated 
     */
    
    public boolean isAge() {
        return getPropertyValue(CommonBlockProperties.AGE_BIT);
    }
    /**
     * @deprecated 
     */
    

    public void setAge(boolean isAge) {
        setPropertyValue(CommonBlockProperties.AGE_BIT, isAge);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBamboo());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinX() {
        return x + 0.125;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        return x + 0.875;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        return z + 0.125;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        return z + 0.875;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return y + 0.875;
    }
}
