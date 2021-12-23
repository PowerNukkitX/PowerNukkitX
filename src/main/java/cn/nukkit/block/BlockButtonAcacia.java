package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockButtonAcacia extends BlockButtonWooden {
    @PowerNukkitOnly
    public BlockButtonAcacia() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockButtonAcacia(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return ACACIA_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Acacia Button";
    }
}
