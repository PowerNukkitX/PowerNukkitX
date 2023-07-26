package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockTrapdoorCrimson extends BlockTrapdoor {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockTrapdoorCrimson() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockTrapdoorCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Crimson Trapdoor";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }
}
