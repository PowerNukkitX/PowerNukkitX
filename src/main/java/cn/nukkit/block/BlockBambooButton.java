package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;


public class BlockBambooButton extends BlockButtonWooden {
    public BlockBambooButton() {
    }

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