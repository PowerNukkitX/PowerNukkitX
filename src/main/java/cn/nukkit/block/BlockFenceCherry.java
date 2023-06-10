package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.0-r1")
public class BlockFenceCherry extends BlockFenceBase {
    public BlockFenceCherry() {
        this(0);
    }

    public BlockFenceCherry(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cherry Fence";
    }

    @Override
    public int getId() {
        return CHERRY_FENCE;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

}
