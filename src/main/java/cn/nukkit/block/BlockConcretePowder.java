package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */

public abstract class BlockConcretePowder extends BlockFallable {
    public BlockConcretePowder(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    public abstract BlockConcrete getConcrete();

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            super.onUpdate(Level.BLOCK_UPDATE_NORMAL);

            for (int side = 1; side <= 5; side++) {
                Block block = this.getSide(BlockFace.fromIndex(side));
                if (block.getId().equals(Block.FLOWING_WATER) || block.getId().equals(Block.WATER)) {
                    this.level.setBlock(this, getConcrete(), true, true);
                }
            }

            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block b, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        boolean concrete = false;

        for (int side = 1; side <= 5; side++) {
            Block block = this.getSide(BlockFace.fromIndex(side));
            if (block.getId().equals(Block.FLOWING_WATER) || block.getId().equals(Block.WATER)) {
                concrete = true;
                break;
            }
        }

        if (concrete) {
            this.level.setBlock(this, getConcrete(), true, true);
        } else {
            this.level.setBlock(this, this, true, true);
        }

        return true;
    }
}
