package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockTrapdoorBirch extends BlockTrapdoor {
    @PowerNukkitOnly
    public BlockTrapdoorBirch() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockTrapdoorBirch(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BIRCH_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Birch Trapdoor";
    }
}
