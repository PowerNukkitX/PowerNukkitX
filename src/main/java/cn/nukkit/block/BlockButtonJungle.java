package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockButtonJungle extends BlockButtonWooden {
    @PowerNukkitOnly
    public BlockButtonJungle() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockButtonJungle(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return JUNGLE_BUTTON;
    }
    
    @Override
    public String getName() {
        return "Jungle Button";
    }
}
