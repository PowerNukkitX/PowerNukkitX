package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockButtonBirch extends BlockButtonWooden {
    @PowerNukkitOnly
    public BlockButtonBirch() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockButtonBirch(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return BIRCH_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Birch Button";
    }
}
