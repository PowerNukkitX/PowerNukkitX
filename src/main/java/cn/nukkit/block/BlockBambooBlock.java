package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooBlock extends BlockLog {
    public BlockBambooBlock() {
        this(0);
    }

    public BlockBambooBlock(int meta) {
        super(meta);
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PILLAR_PROPERTIES;
    }

    public int getId() {
        return BAMBOO_BLOCK;
    }

    public String getName() {
        return "Bamboo Block";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockState getStrippedState() {
        return getCurrentState().withBlockId(STRIPPED_BAMBOO_BLOCK);
    }
}