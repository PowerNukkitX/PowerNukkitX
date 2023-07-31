package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowable;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PowerNukkitX Project 2023/7/15
 *
 * @author daoge_cmd
 */
@PowerNukkitXOnly
@Since("1.20.10-r1")
public class BlockPinkPetals extends BlockFlowable {

    public static final IntBlockProperty GROWTH = new IntBlockProperty("growth", false, 7, 0);
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.DIRECTION, GROWTH);

    @Override
    public String getName() {
        return "Pink Petals";
    }

    @Override
    public int getId() {
        return BlockID.PINK_PETALS;
    }

    @NotNull @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean place(
            @NotNull Item item,
            @NotNull Block block,
            @NotNull Block target,
            @NotNull BlockFace face,
            double fx,
            double fy,
            double fz,
            @Nullable Player player) {
        if (!isSupportValid(block.down())) return false;
        if (player != null)
            setPropertyValue(
                    CommonBlockProperties.DIRECTION,
                    player.getHorizontalFacing().getOpposite());
        return this.getLevel().setBlock(this, this);
    }

    private static boolean isSupportValid(Block block) {
        switch (block.getId()) {
            case GRASS:
            case DIRT:
            case FARMLAND:
            case PODZOL:
            case DIRT_WITH_ROOTS:
            case MOSS_BLOCK:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player) {
        if (item.isFertilizer()) {
            if (getPropertyValue(GROWTH) < 3) {
                setPropertyValue(GROWTH, getPropertyValue(GROWTH) + 1);
                getLevel().setBlock(this, this);
            } else {
                getLevel().dropItem(this, Block.get(Item.PINK_PETALS).toItem());
            }
            item.count--;
            return true;
        } else if (item.getId() == Item.PINK_PETALS && getPropertyValue(GROWTH) < 3) {
            setPropertyValue(GROWTH, getPropertyValue(GROWTH) + 1);
            getLevel().setBlock(this, this);
            item.count--;
            return true;
        }
        return false;
    }
}
