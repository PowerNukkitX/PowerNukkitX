package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockStem;
import cn.nukkit.block.state.BlockState;

@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockHyphaeWarped extends BlockStem {

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockHyphaeWarped() {
        this(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockHyphaeWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_HYPHAE;
    }

    @Override
    public String getName() {
        return "Warped Hyphae";
    }

    @PowerNukkitOnly
    @Override
    public BlockState getStrippedState() {
        return getCurrentState().withBlockId(STRIPPED_WARPED_HYPHAE);
    }
}
