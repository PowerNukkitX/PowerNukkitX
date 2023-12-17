package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


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
}
