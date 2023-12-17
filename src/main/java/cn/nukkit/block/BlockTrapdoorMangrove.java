package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockTrapdoorMangrove extends BlockTrapdoor {

    public BlockTrapdoorMangrove() {
        this(0);
    }


    public BlockTrapdoorMangrove(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Mangrove Trapdoor";
    }

}
