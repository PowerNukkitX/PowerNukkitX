package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;
import org.jetbrains.annotations.NotNull;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockStemStripped extends BlockStem {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStemStripped(int meta) {
        super(meta);
    }

    @PowerNukkitOnly
    @Override
    public BlockState getStrippedState() {
        return getCurrentState();
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        return false;
    }
}
