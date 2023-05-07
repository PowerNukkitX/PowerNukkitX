package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.generator.object.tree.ObjectWarpedTree;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockFungusWarped extends BlockFungus {
    private final ObjectWarpedTree feature = new ObjectWarpedTree();

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

    @PowerNukkitOnly
    @Override
    protected boolean canGrowOn(Block support) {
        return support.getId() == WARPED_NYLIUM;
    }

    @PowerNukkitOnly
    @Override
    public boolean grow(@Nullable Player cause) {
        NukkitRandom nukkitRandom = new NukkitRandom();
        this.feature.placeObject(this.getLevel(), this.getFloorX(), this.getFloorY(), this.getFloorZ(), nukkitRandom);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
