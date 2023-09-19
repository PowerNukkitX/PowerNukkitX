package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockID;

@PowerNukkitOnly
public class BlockBlastFurnace extends BlockBlastFurnaceBurning {
    @PowerNukkitOnly
    public BlockBlastFurnace() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockBlastFurnace(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Blast Furnace";
    }

    @Override
    public int getId() {
        return BlockID.BLAST_FURNACE;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }
}
