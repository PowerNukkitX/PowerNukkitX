package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.MinecraftVerticalHalf;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockSlab extends BlockTransparent {
    protected final BlockState doubleSlab;

    public BlockSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState);
        this.doubleSlab = doubleSlab;
    }

    public BlockSlab(BlockState blockState, String doubleSlab) {
        super(blockState);
        this.doubleSlab = Registries.BLOCK.get(doubleSlab).getBlockState();
    }

    public abstract String getSlabName();

    @Override
    public String getName() {
        return (isOnTop() ? "Upper " : "") + getSlabName() + " Slab";
    }

    @Override
    public double getMinY() {
        return isOnTop() ? this.y + 0.5 : this.y;
    }

    @Override
    public double getMaxY() {
        return isOnTop() ? this.y + 1 : this.y + 0.5;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return getToolType() < ItemTool.TYPE_AXE ? 30 : 15;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    public boolean isOnTop() {
        return getPropertyValue(CommonBlockProperties.MINECRAFT_VERTICAL_HALF) == MinecraftVerticalHalf.TOP;
    }

    public void setOnTop(boolean top) {
        setPropertyValue(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, top ? MinecraftVerticalHalf.TOP : MinecraftVerticalHalf.BOTTOM);
    }

    public abstract boolean isSameType(BlockSlab slab);

    @Override
    public boolean isSolid(BlockFace side) {
        return side == BlockFace.UP && isOnTop() || side == BlockFace.DOWN && !isOnTop();
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setOnTop(false);
        if (face == BlockFace.DOWN) {
            if (target instanceof BlockSlab slab && slab.isOnTop() && isSameType((BlockSlab) target)) {

                this.getLevel().setBlock(target, Block.get(doubleSlab), true);

                return true;
            } else if (block instanceof BlockSlab && isSameType((BlockSlab) block)) {
                this.getLevel().setBlock(block, Block.get(doubleSlab), true);

                return true;
            } else {
                setOnTop(true);
            }
        } else if (face == BlockFace.UP) {
            if (target instanceof BlockSlab slab && !slab.isOnTop() && isSameType((BlockSlab) target)) {
                this.getLevel().setBlock(target, Block.get(doubleSlab), true);

                return true;
            } else if (block instanceof BlockSlab && isSameType((BlockSlab) block)) {
                this.getLevel().setBlock(block, Block.get(doubleSlab), true);

                return true;
            }
            //TODO: check for collision
        } else {
            if (block instanceof BlockSlab) {
                if (isSameType((BlockSlab) block)) {
                    this.getLevel().setBlock(block, Block.get(doubleSlab), true);

                    return true;
                }

                return false;
            } else {
                if (fy > 0.5) {
                    setOnTop(true);
                }
            }
        }

        if (block instanceof BlockSlab && !isSameType((BlockSlab) block)) {
            return false;
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }
}
