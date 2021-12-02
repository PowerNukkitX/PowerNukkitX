package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockButtonSpruce extends BlockButtonWooden {
    @PowerNukkitOnly
    public BlockButtonSpruce() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockButtonSpruce(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return SPRUCE_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Spruce Button";
    }
}
