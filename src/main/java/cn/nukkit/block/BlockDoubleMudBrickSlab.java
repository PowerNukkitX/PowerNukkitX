package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

public class BlockDoubleMudBrickSlab extends BlockDoubleSlabBase{
    @Override
    public int getId() {
        return MUD_BRICK_DOUBLE_SLAB;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public String getSlabName() {
        return "Double Mud Brick Slab";
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getSingleSlabId() {
        return MUD_BRICK_SLAB;
    }
}
