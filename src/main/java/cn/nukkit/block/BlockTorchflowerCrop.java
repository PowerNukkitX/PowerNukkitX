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
    public static final BlockProperties PROPERTIES = new BlockProperties(TORCHFLOWER_CROP, CommonBlockProperties.GROWTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTorchflowerCrop() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTorchflowerCrop(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Torchflower Crop";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.TORCHFLOWER_SEEDS);
    }

    @Override
    @NotNull public String getItemId() {
        return ItemID.TORCHFLOWER_SEEDS;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        //Bone meal
        if (item.isFertilizer()) {
            int max = getMaxGrowth();
            int growth = getGrowth();

            if (growth == 1) {
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
            if (!this.down().getId().equals(FARMLAND)) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) == 1 && getLevel().getFullLight(this) >= getMinimumLightLevel()) {
                int growth = getGrowth();
                if (growth == 1) {
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