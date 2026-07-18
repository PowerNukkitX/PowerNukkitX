package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static org.powernukkitx.block.property.CommonBlockProperties.GROWTH;
import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

/**
 * Class for the leaf litter block.
 *
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/7/15
 */
public class BlockLeafLitter extends BlockFlowable {

    public static final BlockProperties PROPERTIES = new BlockProperties(LEAF_LITTER, MINECRAFT_CARDINAL_DIRECTION, GROWTH);

    public BlockLeafLitter() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLeafLitter(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Leaf Litter";
    }

    public boolean isSupportValid(Block block) {
        return block.isFullBlock() && block.isSolid();
    }

    @Override
    public boolean canBeReplaced() {
        return true;
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

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (Objects.equals(item.getBlockId(), getId()) && getPropertyValue(GROWTH) < 3) {
            setPropertyValue(GROWTH, getPropertyValue(GROWTH) + 1);
            getLevel().setBlock(this, this);
            item.count--;
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid(getLevelBlock().down())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }
}
