package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.generator.object.tree.ObjectCrimsonTree;
import cn.nukkit.math.NukkitRandom;

import javax.annotation.Nullable;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockFungusCrimson extends BlockFungus {
    private final ObjectCrimsonTree feature = new ObjectCrimsonTree();

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

    @PowerNukkitOnly
    @Override
    protected boolean canGrowOn(Block support) {
        if (support.getId() == CRIMSON_NYLIUM) {
            for (int i = 1; i <= this.feature.getTreeHeight(); i++) {
                if (this.up(i).getId() != 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @PowerNukkitOnly
    @Override
    public boolean grow(@Nullable Player cause) {
        NukkitRandom nukkitRandom = new NukkitRandom();
        this.feature.placeObject(this.getLevel(), this.getFloorX(), this.getFloorY(), this.getFloorZ(), nukkitRandom);
        return true;
    }

}
