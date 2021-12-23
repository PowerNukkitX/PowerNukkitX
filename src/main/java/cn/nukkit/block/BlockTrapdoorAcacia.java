package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
public class BlockTrapdoorAcacia extends BlockTrapdoor {
    @PowerNukkitOnly
    public BlockTrapdoorAcacia() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockTrapdoorAcacia(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return ACACIA_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Acacia Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
