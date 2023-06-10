package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockButtonCherry extends BlockButtonWooden {
    @PowerNukkitOnly
    public BlockButtonCherry() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockButtonCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_BUTTON;
    }

    @Override
    public String getName() {
        return "Cherry Button";
    }
}