package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.GROWTH;
import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

/**
 * PowerNukkitX Project 2023/7/15
 *
 * @author daoge_cmd
 */
public class BlockWildflowers extends BlockSegmented {

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
    public boolean isSupportValid(Block block) {
        return BlockSweetBerryBush.isSupportValid(block);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) { //Bone meal
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }
            this.level.addParticle(new BoneMealParticle(this));
            this.level.dropItem(this, this.toItem());

            return true;
        }

        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }
}
