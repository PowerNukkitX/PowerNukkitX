package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.level.ListChunkManager;
import cn.nukkit.level.generator.object.tree.ObjectCrimsonTree;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockFungusCrimson extends BlockFungus {

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockFungusCrimson() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CRIMSON_FUNGUS;
    }

    @Override
    public String getName() {
        return "Crimson Fungus";
    }

    @Override
    protected boolean canGrowOn(Block support) {
        return support.getId() == CRIMSON_NYLIUM;
    }

    @Override
    public boolean grow(@Nullable Player cause) {
        ObjectCrimsonTree crimsonTree = new ObjectCrimsonTree();
        StructureGrowEvent event = new StructureGrowEvent(this, new ListChunkManager(level).getBlocks());
        level.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return false;
        }
        crimsonTree.placeObject(level, getFloorX(), getFloorY(), getFloorZ(), new NukkitRandom());
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }
}
