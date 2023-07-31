package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockTrapdoor;

@PowerNukkitOnly
public class BlockTrapdoorSpruce extends BlockTrapdoor {
    @PowerNukkitOnly
    public BlockTrapdoorSpruce() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockTrapdoorSpruce(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SPRUCE_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Spruce Trapdoor";
    }
}
