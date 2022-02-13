package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

@Since("1.6.0.0-PNX")
@PowerNukkitOnly
public class BlockBuddingAmethyst extends BlockSolid {
    private static final NukkitRandom RANDOM = new NukkitRandom();

    @Override
    public String getName() {
        return "Budding Amethyst";
    }

    @Override
    public int getId() {
        return BUDDING_AMETHYST;
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
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @PowerNukkitOnly
    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @PowerNukkitOnly
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
            if (RANDOM.nextBoundedInt(5) == 1) {
                final Block side = this.getSide(BlockFace.fromIndex(RANDOM.nextRange(6)));
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
        final BlockFace face = BlockFace.fromIndex(RANDOM.nextRange(6));
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
