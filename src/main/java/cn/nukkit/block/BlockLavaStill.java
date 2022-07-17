package cn.nukkit.block;

import cn.nukkit.api.Since;
import cn.nukkit.level.Level;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockLavaStill extends BlockLava {

    public BlockLavaStill() {
        super(0);
    }

    public BlockLavaStill(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STILL_LAVA;
    }

    @Override
    public String getName() {
        return "Still Lava";
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return (BlockLiquid) Block.get(BlockID.STILL_LAVA, meta);
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_SCHEDULED) {
            return super.onUpdate(type);
        }
        return 0;
    }

    @Since("1.6.0.0-PNX")
    @Override
    public float getDrag() {
        return 0.2f;
    }
}
