package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockTrapdoor;

@PowerNukkitOnly
public class BlockTrapdoorDarkOak extends BlockTrapdoor {
    @PowerNukkitOnly
    public BlockTrapdoorDarkOak() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockTrapdoorDarkOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARK_OAK_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Dark Oak Trapdoor";
    }
}
