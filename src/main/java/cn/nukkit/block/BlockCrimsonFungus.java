package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.legacytree.LegacyCrimsonTree;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockCrimsonFungus extends BlockFungus {
    private final LegacyCrimsonTree feature = new LegacyCrimsonTree();

    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_FUNGUS);

    @Override
    @NotNull public BlockProperties getProperties() {
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
        NukkitRandom nukkitRandom = new NukkitRandom();
        BlockManager blockManager = new BlockManager(this.getLevel());
        this.feature.placeObject(blockManager, this.getFloorX(), this.getFloorY(), this.getFloorZ(), nukkitRandom);
        StructureGrowEvent ev = new StructureGrowEvent(this, blockManager.getBlocks());
        this.level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        blockManager.applySubChunkUpdate(ev.getBlockList());
        return true;
    }
}