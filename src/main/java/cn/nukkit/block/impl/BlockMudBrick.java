package cn.nukkit.block.impl;

import cn.nukkit.block.BlockSolid;

public class BlockMudBrick extends BlockSolid {
    @Override
    public String getName() {
        return "Mud Brick";
    }

    @Override
    public int getId() {
        return MUD_BRICKS;
    }
}
