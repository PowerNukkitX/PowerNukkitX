package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockDoubleMudBrickSlab extends BlockDoubleSlabBase{
    @Override
    public int getId() {
        return MUD_BRICK_DOUBLE_SLAB;
    }


    @Override
    public String getSlabName() {
        return "Double Mud Brick Slab";
    }


    @Override
    public int getSingleSlabId() {
        return MUD_BRICK_SLAB;
    }
}
