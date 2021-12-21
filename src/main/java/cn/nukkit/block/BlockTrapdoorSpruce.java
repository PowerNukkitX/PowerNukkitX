package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
public class BlockTrapdoorSpruce extends BlockTrapdoor {
    @PowerNukkitOnly
    public BlockTrapdoorSpruce() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockTrapdoorSpruce(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return SPRUCE_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Spruce Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
