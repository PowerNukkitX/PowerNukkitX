package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockStemStripped;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockHyphaeStrippedWarped extends BlockStemStripped {

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockHyphaeStrippedWarped() {
        this(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockHyphaeStrippedWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_WARPED_HYPHAE;
    }

    @Override
    public String getName() {
        return "Warped Stripped Hyphae";
    }

    @Override
    public double getHardness() {
        return 0.4;
    }
}
