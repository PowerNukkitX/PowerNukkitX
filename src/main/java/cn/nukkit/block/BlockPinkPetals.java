package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.*;

/**
 * PowerNukkitX Project 2023/7/15
 *
 * @author daoge_cmd
 */
public class BlockPinkPetals extends BlockFlowable {

    public static final BlockProperties PROPERTIES = new BlockProperties(PINK_PETALS,
            GROWTH, MINECRAFT_CARDINAL_DIRECTION);

    public BlockPinkPetals() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkPetals(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Pink Petals";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!isSupportValid(block.down())) {
            return false;
        }

        if (player != null) {
            setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(player.getHorizontalFacing().getOpposite()));
        }

        return this.getLevel().setBlock(this, this);
    }

    private static boolean isSupportValid(Block block) {
        return switch (block.getId()) {
            case GRASS_BLOCK, DIRT, FARMLAND, PODZOL, DIRT_WITH_ROOTS, MOSS_BLOCK -> true;
            default -> false;
        };
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) {
            if (getPropertyValue(GROWTH) < 3) {
                setPropertyValue(GROWTH, getPropertyValue(GROWTH) + 1);
                getLevel().setBlock(this, this);
            } else {
                getLevel().dropItem(this, this.toItem());
            }

            this.level.addParticle(new BoneMealParticle(this));
            item.count--;
            return true;
        }

        if (Objects.equals(item.getBlockId(), PINK_PETALS) && getPropertyValue(GROWTH) < 3) {
            setPropertyValue(GROWTH, getPropertyValue(GROWTH) + 1);
            getLevel().setBlock(this, this);
            item.count--;
            return true;
        }

        return false;
    }
}
