package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockWallBase;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockMudBrickWall extends BlockWallBase {
    @Override
    public String getName() {
        return "Mud Brick Wall";
    }

    @Override
    public int getId() {
        return MUD_BRICK_WALL;
    }
}
