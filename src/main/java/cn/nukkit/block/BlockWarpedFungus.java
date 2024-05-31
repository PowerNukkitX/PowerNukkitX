package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.legacytree.LegacyWarpedTree;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockWarpedFungus extends BlockFungus {
    public static final BlockProperties $1 = new BlockProperties(WARPED_FUNGUS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedFungus() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedFungus(BlockState blockstate) {
        super(blockstate);
    }

    private final LegacyWarpedTree $2 = new LegacyWarpedTree();

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Warped Fungus";
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected boolean canGrowOn(Block support) {
        if (support.getId().equals(WARPED_NYLIUM)) {
            for ($3nt $1 = 1; i <= this.feature.getTreeHeight(); i++) {
                if (!this.up(i).isAir()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean grow(@Nullable Player cause) {
        NukkitRandom $4 = new NukkitRandom();
        BlockManager $5 = new BlockManager(this.getLevel());
        this.feature.placeObject(blockManager, this.getFloorX(), this.getFloorY(), this.getFloorZ(), nukkitRandom);
        StructureGrowEvent $6 = new StructureGrowEvent(this, blockManager.getBlocks());
        this.level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        blockManager.applySubChunkUpdate(ev.getBlockList());
        return true;
    }
}