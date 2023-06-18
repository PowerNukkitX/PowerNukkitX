package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockLogStrippedCherry extends BlockLog {
    public BlockLogStrippedCherry() {
        super(0);
    }

    public BlockLogStrippedCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_CHERRY_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Cherry Log";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 10;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PILLAR_PROPERTIES;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    protected BlockState getStrippedState() {
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
