package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooButton extends BlockButtonWooden {
    public BlockBambooButton() {}

    @Override
    public int getId() {
        return BAMBOO_BUTTON;
    }

    @Override
    public String getName() {
        return "Bamboo Button";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}
