package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.level.ListChunkManager;
import cn.nukkit.level.generator.object.tree.ObjectWarpedTree;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockFungusWarped extends BlockFungus {

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockFungusWarped() {
        // Does nothing
    }

    @Override
    public int getId() {
        return WARPED_FUNGUS;
    }

    @Override
    public String getName() {
        return "Warped Fungus";
    }

    @Override
    protected boolean canGrowOn(Block support) {
        return support.getId() == WARPED_NYLIUM;
    }

    @Override
    public boolean grow(@Nullable Player cause) {
        ObjectWarpedTree warpedTree = new ObjectWarpedTree();
        StructureGrowEvent event = new StructureGrowEvent(this, new ListChunkManager(level).getBlocks());
        level.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return false;
        }
        warpedTree.placeObject(level, getFloorX(), getFloorY(), getFloorZ(), new NukkitRandom());
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
