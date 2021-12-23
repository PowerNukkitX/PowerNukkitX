package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockButtonDarkOak extends BlockButtonWooden {
    @PowerNukkitOnly
    public BlockButtonDarkOak() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockButtonDarkOak(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return DARK_OAK_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Dark Oak Button";
    }
}
