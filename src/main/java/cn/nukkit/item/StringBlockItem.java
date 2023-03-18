package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.block.Block;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
public final class StringBlockItem extends StringItemBase {
    public StringBlockItem(@NotNull Block block) {
        super(block.getPersistenceName(), block.getName());
        this.block = block;
    }
}
