package cn.nukkit.block;

import cn.nukkit.api.Since;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.item.ItemTool;

import static cn.nukkit.block.BlockID.BAMBOO_BUTTON;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooButton extends BlockButtonWooden {
    public BlockBambooButton() {
    }

    public int getId() {
        return BAMBOO_BUTTON;
    }

    public String getName() {
        return "Bamboo Button";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}