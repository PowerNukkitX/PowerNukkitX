package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;

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
}
