package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockFenceMangrove extends BlockFenceBase{
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockFenceMangrove() {
        this(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockFenceMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Fence";
    }

    @Override
    public int getId() {
        return MANGROVE_FENCE;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWNISH_RED;
    }
}
