package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.particle.BoneMealParticle;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static org.powernukkitx.block.property.CommonBlockProperties.GROWTH;
import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

/**
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/7/15
 */
public class BlockWildflowers extends BlockFlower {

    public static final BlockProperties PROPERTIES = new BlockProperties(WILDFLOWERS, MINECRAFT_CARDINAL_DIRECTION, GROWTH);

    public BlockWildflowers() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWildflowers(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Wildflowers";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!isSupportDirt(block.down())) {
            return false;
        }

        if (player != null) {
            setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(player.getHorizontalFacing().getOpposite()));
        }

        return this.getLevel().setBlock(this, this);
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

        if (Objects.equals(item.getBlockId(), getId()) && getPropertyValue(GROWTH) < 3) {
            setPropertyValue(GROWTH, getPropertyValue(GROWTH) + 1);
            getLevel().setBlock(this, this);
            item.count--;
            return true;
        }

        return false;
    }

    @Override
    public int getSnowloggingLevel() {
        return 0;
    }
}
