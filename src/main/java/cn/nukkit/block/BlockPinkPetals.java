package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PowerNukkitX Project 2023/7/15
 *
 * @author daoge_cmd
 */


public class BlockPinkPetals extends BlockFlowable {

    public static final IntBlockProperty GROWTH = new IntBlockProperty("growth", false, 7, 0);
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.CARDINAL_DIRECTION, GROWTH);

    @Override
    public String getName() {
        return "Pink Petals";
    }

    @Override
    public int getId() {
        return BlockID.PINK_PETALS;
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!isSupportValid(block.down()))
            return false;
        if (player != null)
            setPropertyValue(CommonBlockProperties.CARDINAL_DIRECTION, player.getHorizontalFacing().getOpposite());
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
            this.level.addParticle(new BoneMealParticle(this));
            item.count--;
            return true;
        }
        if (item.getId() == Item.fromString("minecraft:pink_petals").getId() && getPropertyValue(GROWTH) < 3) {
            setPropertyValue(GROWTH, getPropertyValue(GROWTH) + 1);
            getLevel().setBlock(this, this);
            item.count--;
            return true;
        }
        return false;
    }
}
