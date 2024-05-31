package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockTorchflowerCrop extends BlockCrops {
    public static final BlockProperties $1 = new BlockProperties(TORCHFLOWER_CROP, CommonBlockProperties.GROWTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockTorchflowerCrop() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockTorchflowerCrop(BlockState blockstate) {
        super(blockstate);
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return "Torchflower Crop";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.TORCHFLOWER_SEEDS);
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getItemId() {
        return ItemID.TORCHFLOWER_SEEDS;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        //Bone meal
        if (item.isFertilizer()) {
            int $2 = getMaxGrowth();
            int $3 = getGrowth();

            if (growth == 1) {
                BlockTorchflower $4 = new BlockTorchflower();
                BlockGrowEvent $5 = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);


                this.getLevel().setBlock(this, ev.getNewState(), false, true);
                this.level.addParticle(new BoneMealParticle(this));

                if (player != null && !player.isCreative()) {
                    item.count--;
                }
                return true;
            }
            if (growth < max) {
                BlockTorchflowerCrop $6 = (BlockTorchflowerCrop) this.clone();
                growth += 1;
                block.setGrowth(Math.min(growth, max));
                BlockGrowEvent $7 = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    return false;
                }

                this.getLevel().setBlock(this, ev.getNewState(), false, true);
                this.level.addParticle(new BoneMealParticle(this));

                if (player != null && !player.isCreative()) {
                    item.count--;
                }
            }

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
            if (!this.down().getId().equals(FARMLAND)) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) == 1 && getLevel().getFullLight(this) >= getMinimumLightLevel()) {
                int $8 = getGrowth();
                if (growth == 1) {
                    BlockTorchflower $9 = new BlockTorchflower();
                    BlockGrowEvent $10 = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);

                    if (ev.isCancelled()) {
                        return 0;
                    } else {
                        this.getLevel().setBlock(this, ev.getNewState(), false, true);
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
                if (growth < getMaxGrowth()) {
                    BlockTorchflowerCrop $11 = (BlockTorchflowerCrop) this.clone();
                    block.setGrowth(growth + 1);
                    BlockGrowEvent $12 = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), false, true);
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }
}