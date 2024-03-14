package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemStick;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
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
        if (isSupportValid()) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }
    
    private boolean isSupportValid() {
        Block down = down();
        if(down instanceof BlockHardenedClay)  return true;
        return switch (down.getId()) {
            case SAND, DIRT, PODZOL, GRASS_BLOCK, MOSS_BLOCK, MYCELIUM -> true;
            default -> false;
        };
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid()) {
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
