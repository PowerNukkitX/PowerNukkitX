package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author LoboMetalurgico
 * @since 08/06/2021
 */


public class BlockRawIron extends BlockRaw {


    public BlockRawIron() {
        this(0);
    }


    public BlockRawIron(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Block of Raw Iron";
    }

    @Override
    public int getId() {
        return RAW_IRON_BLOCK;
    }

}
