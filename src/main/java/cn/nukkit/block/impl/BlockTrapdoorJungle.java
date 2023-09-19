package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockTrapdoor;

@PowerNukkitOnly
public class BlockTrapdoorJungle extends BlockTrapdoor {
    @PowerNukkitOnly
    public BlockTrapdoorJungle() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockTrapdoorJungle(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return JUNGLE_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Jungle Trapdoor";
    }
}
