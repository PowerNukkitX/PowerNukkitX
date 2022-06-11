package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockButtonMangrove extends BlockButtonWooden{
    @PowerNukkitOnly
    public BlockButtonMangrove() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockButtonMangrove(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_BUTTON;
    }

    @Override
    public String getName() {
        return "Mangrove Button";
    }
}
