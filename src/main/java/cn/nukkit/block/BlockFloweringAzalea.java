package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

public class BlockFloweringAzalea extends BlockAzalea{


    @PowerNukkitOnly
    public BlockFloweringAzalea() {
        this(0);
    }


    @PowerNukkitOnly
    public BlockFloweringAzalea(int meta) {
        super(meta);
    }


    @Override
    public String getName() {
        return "FloweringAzalea";
    }

    @Override
    public int getId() {
        return FLOWERING_AZALEA;
    }
}
