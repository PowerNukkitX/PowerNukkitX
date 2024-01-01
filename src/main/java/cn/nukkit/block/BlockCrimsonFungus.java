package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.level.generator.object.tree.ObjectCrimsonTree;
import cn.nukkit.utils.random.NukkitRandomSource;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockCrimsonFungus extends BlockFungus {
    private final ObjectCrimsonTree feature = new ObjectCrimsonTree();

    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_fungus");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonFungus() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonFungus(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getName() {
        return "Crimson Fungus";
    }


    @Override
    protected boolean canGrowOn(Block support) {
        if (support.getId().equals(CRIMSON_NYLIUM)) {
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
        NukkitRandomSource nukkitRandom = new NukkitRandomSource();
        this.feature.placeObject(this.getLevel(), this.getFloorX(), this.getFloorY(), this.getFloorZ(), nukkitRandom);
        return true;
    }
}