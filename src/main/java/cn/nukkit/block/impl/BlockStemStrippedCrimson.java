package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockStemStripped;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockStemStrippedCrimson extends BlockStemStripped {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStemStrippedCrimson() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStemStrippedCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_CRIMSON_STEM;
    }

    @Override
    public String getName() {
        return "Stripped Crimson Stem";
    }
}
