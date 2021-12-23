package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockSpruceSignPost;

@PowerNukkitOnly
public class ItemSpruceSign extends ItemSign {
    @PowerNukkitOnly
    public ItemSpruceSign() {
        this(0, 1);
    }

    @PowerNukkitOnly
    public ItemSpruceSign(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    public ItemSpruceSign(Integer meta, int count) {
        super(SPRUCE_SIGN, meta, count, "Spruce Sign", new BlockSpruceSignPost());
    }
}
