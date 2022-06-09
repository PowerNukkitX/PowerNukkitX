package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

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
