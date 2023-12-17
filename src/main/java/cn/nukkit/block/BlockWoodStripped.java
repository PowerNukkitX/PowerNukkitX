package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;


public abstract class BlockWoodStripped extends BlockWood {

    public BlockWoodStripped() {
        this(0);
    }


    public BlockWoodStripped(int meta) {
        super(meta);
    }
    
    @Override
    public abstract int getId();


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PILLAR_PROPERTIES;
    }


    @Override
    public BlockState getStrippedState() {
        return getCurrentState();
    }

    @Override
    public String getName() {
        return "Stripped " + super.getName();
    }


    @Override
    public void setWoodType(WoodType woodType) {
        if (!woodType.equals(getWoodType())) {
            throw new InvalidBlockPropertyValueException(WoodType.PROPERTY, getWoodType(), woodType, 
                    "Only the current value is supported for this block");
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
