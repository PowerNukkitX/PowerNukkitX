package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;


public class BlockTorchflowerCrop extends BlockCrops {
    public static final IntBlockProperty GROWTH = new IntBlockProperty("growth", false, 1);
    public static final BlockProperties PROPERTIES = new BlockProperties(GROWTH);

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTorchflowerCrop() {
        super(0);
    }

    public BlockTorchflowerCrop(BlockState blockstate) {
        super(blockstate);
    }

    public int getId() {
        return TORCHFLOWER_CROP;
    }

    public String getName() {
        return "Torchflower Crop";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.TORCHFLOWER_SEEDS);
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        //Bone meal
        if (item.isFertilizer()) {
            int max = getMaxGrowth();
            int growth = getGrowth();

            if(growth == 1) {
                BlockTorchflower block = new BlockTorchflower();
                BlockGrowEvent ev = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);


                this.getLevel().setBlock(this, ev.getNewState(), false, true);
                this.level.addParticle(new BoneMealParticle(this));

                if (player != null && !player.isCreative()) {
                    item.count--;
                }
                return true;
            }
            if (growth < max) {
                BlockTorchflowerCrop block = (BlockTorchflowerCrop) this.clone();
                growth += 1;
                block.setGrowth(Math.min(growth, max));
                BlockGrowEvent ev = new BlockGrowEvent(this, block);
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
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != FARMLAND) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) == 1 && getLevel().getFullLight(this) >= getMinimumLightLevel()) {
                int growth = getGrowth();
                if(growth == 1) {
                    BlockTorchflower block = new BlockTorchflower();
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);

                    if (ev.isCancelled()) {
                        return 0;
                    } else {
                        this.getLevel().setBlock(this, ev.getNewState(), false, true);
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
                if (growth < getMaxGrowth()) {
                    BlockTorchflowerCrop block = (BlockTorchflowerCrop) this.clone();
                    block.setGrowth(growth + 1);
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
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