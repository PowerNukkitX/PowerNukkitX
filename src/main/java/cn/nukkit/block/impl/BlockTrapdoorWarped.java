package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockTrapdoor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockTrapdoorWarped extends BlockTrapdoor {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockTrapdoorWarped() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockTrapdoorWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Warped Trapdoor";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }
}
