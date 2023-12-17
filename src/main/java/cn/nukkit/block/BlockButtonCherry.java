package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockButtonCherry extends BlockButtonWooden {

    public BlockButtonCherry() {
        this(0);
    }


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