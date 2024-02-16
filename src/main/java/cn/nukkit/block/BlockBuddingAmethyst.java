package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;


public class BlockBuddingAmethyst extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(BUDDING_AMETHYST);
    private static final NukkitRandom RANDOM = new NukkitRandom();

    public BlockBuddingAmethyst() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockBuddingAmethyst(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Budding Amethyst";
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (RANDOM.nextInt(5) == 1) {
                tryGrow(0);
            }
            return type;
        }
        return 0;
    }

    public void tryGrow(int time) {
        if (time > 6) {
            return;
        }
        final BlockFace face = BlockFace.fromIndex(RANDOM.nextInt(6));
        final Block side = this.getSide(face);
        BlockAmethystBud tmp;
        if (side.canBeReplaced()) {
            tmp = new BlockSmallAmethystBud();
            tmp.setBlockFace(face);
            this.getLevel().setBlock(side, tmp, true, true);
        } else if (side instanceof BlockSmallAmethystBud) {
            tmp = new BlockMediumAmethystBud();
            tmp.setBlockFace(face);
            this.getLevel().setBlock(side, tmp, true, true);
        } else if (side instanceof BlockMediumAmethystBud) {
            tmp = new BlockLargeAmethystBud();
            tmp.setBlockFace(face);
            this.getLevel().setBlock(side, tmp, true, true);
        } else if (side instanceof BlockLargeAmethystBud) {
            tmp = new BlockAmethystCluster();
            tmp.setBlockFace(face);
            this.getLevel().setBlock(side, tmp, true, true);
        } else {
            tryGrow(time + 1);
        }
    }
}
