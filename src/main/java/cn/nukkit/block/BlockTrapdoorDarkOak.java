package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

@PowerNukkitOnly
public class BlockTrapdoorDarkOak extends BlockTrapdoor {
    @PowerNukkitOnly
    public BlockTrapdoorDarkOak() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockTrapdoorDarkOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARK_OAK_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Dark Oak Trapdoor";
    }
}
