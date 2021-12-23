package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
public class BlockTrapdoorJungle extends BlockTrapdoor {
    @PowerNukkitOnly
    public BlockTrapdoorJungle() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockTrapdoorJungle(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return JUNGLE_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Jungle Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
