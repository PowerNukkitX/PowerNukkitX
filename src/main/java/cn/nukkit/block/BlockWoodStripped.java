package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.impl.BlockWood;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.exception.InvalidBlockPropertyValueException;
import cn.nukkit.block.property.value.WoodType;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;
import org.jetbrains.annotations.NotNull;

@PowerNukkitOnly
public abstract class BlockWoodStripped extends BlockWood {
    @PowerNukkitOnly
    public BlockWoodStripped() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockWoodStripped(int meta) {
        super(meta);
    }

    @Override
    public abstract int getId();

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull @Override
    public BlockProperties getProperties() {
        return PILLAR_PROPERTIES;
    }

    @PowerNukkitOnly
    @Override
    public BlockState getStrippedState() {
        return getCurrentState();
    }

    @Override
    public String getName() {
        return "Stripped " + super.getName();
    }

    @PowerNukkitOnly
    @Override
    public void setWoodType(WoodType woodType) {
        if (!woodType.equals(getWoodType())) {
            throw new InvalidBlockPropertyValueException(
                    WoodType.PROPERTY, getWoodType(), woodType, "Only the current value is supported for this block");
        }
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
