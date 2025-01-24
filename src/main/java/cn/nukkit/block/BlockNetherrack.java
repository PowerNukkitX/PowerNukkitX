package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Pub4Game
 * @since 2015/12/26
 */
public class BlockNetherrack extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHERRACK);

    public BlockNetherrack() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockNetherrack(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getResistance() {
        return 0.4;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Netherrack";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isNull() || !item.isFertilizer() || up().getId() != AIR) {
            return false;
        }

        List<String> options = new ArrayList<String>();
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            String id = getSide(face).getId();
            if ((id.equals(CRIMSON_NYLIUM) || id.equals(WARPED_NYLIUM)) && !options.contains(id)) {
                options.add(id);
            }
        }
        
        String nylium;
        int size = options.size();
        if (size == 0) {
            return false;
        } else if (size == 1) {
            nylium = options.get(0);
        } else {
            nylium = options.get(ThreadLocalRandom.current().nextInt(size));
        }
        
        if (level.setBlock(this, Block.get(nylium), true)) {
            if (player == null || !player.isCreative()) {
                item.count--;
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }
}
