package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockButtonMangrove extends BlockButtonWooden{

    public BlockButtonMangrove() {
        this(0);
    }


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
