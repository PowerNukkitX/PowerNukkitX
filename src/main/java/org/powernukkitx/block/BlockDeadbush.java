package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemStick;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.tags.BlockTags;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockDeadbush extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEADBUSH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadbush() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeadbush(BlockState blockState) {
        // Dead bushes can't have meta. Also stops the server from throwing an exception with the block palette.
        super(blockState);
    }

    @Override
    public String getName() {
        return "Dead Bush";
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
    
    @Override
    public boolean canBeReplaced() {
        return true;
    }

    
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }
    
    public static boolean isSupportValid(Block down) {
        if(down instanceof BlockHardenedClay) return true;
        return down.hasTag(BlockTags.DIRT) || down.hasTag(BlockTags.SAND);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid(down())) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[]{
                    new ItemStick(0, new Random().nextInt(3))
            };
        }
    }

}
