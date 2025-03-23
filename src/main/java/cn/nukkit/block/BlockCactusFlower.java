package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.tags.BlockTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

/**
 * PowerNukkitX Project 2023/7/15
 *
 * @author daoge_cmd
 */
public class BlockCactusFlower extends BlockFlowable {

    public static final BlockProperties PROPERTIES = new BlockProperties(CACTUS_FLOWER);

    public BlockCactusFlower() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCactusFlower(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Cactus Flower";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!isSupportValid(block.down())) {
            return false;
        }
        return this.getLevel().setBlock(this, this);
    }

    private static boolean isSupportValid(Block block) {
        return block instanceof BlockCactus;
    }
}
