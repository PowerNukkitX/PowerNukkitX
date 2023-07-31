package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockStairs;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockMudBrickStairs extends BlockStairs {

    @Override
    public String getName() {
        return "Mud Brick Stair";
    }

    @Override
    public int getId() {
        return MUD_BRICK_STAIRS;
    }
}
