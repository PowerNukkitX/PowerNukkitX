package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.legacytree.LegacyCrimsonTree;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockCrimsonFungus extends BlockFungus {
    private final LegacyCrimsonTree $1 = new LegacyCrimsonTree();

    public static final BlockProperties $2 = new BlockProperties(CRIMSON_FUNGUS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonFungus() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonFungus(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Crimson Fungus";
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected boolean canGrowOn(Block support) {
        if (support.getId().equals(CRIMSON_NYLIUM)) {
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