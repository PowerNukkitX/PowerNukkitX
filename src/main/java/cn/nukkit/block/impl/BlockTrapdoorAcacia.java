package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockTrapdoor;

@PowerNukkitOnly
public class BlockTrapdoorAcacia extends BlockTrapdoor {
    @PowerNukkitOnly
    public BlockTrapdoorAcacia() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockTrapdoorAcacia(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ACACIA_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Acacia Trapdoor";
    }
}
