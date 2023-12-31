package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.level.generator.object.tree.ObjectWarpedTree;
import cn.nukkit.math.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockWarpedFungus extends BlockFungus {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_fungus");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedFungus() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedFungus(BlockState blockstate) {
        super(blockstate);
    }

    private final ObjectWarpedTree feature = new ObjectWarpedTree();

    @Override
    public String getName() {
        return "Warped Fungus";
    }


    @Override
    protected boolean canGrowOn(Block support) {
        if (support.getId().equals(WARPED_NYLIUM)) {
            for (int i = 1; i <= this.feature.getTreeHeight(); i++) {
                if (!this.up(i).isAir()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean grow(@Nullable Player cause) {
        NukkitRandom nukkitRandom = new NukkitRandom();
        this.feature.placeObject(this.getLevel(), this.getFloorX(), this.getFloorY(), this.getFloorZ(), nukkitRandom);
        return true;
    }
}