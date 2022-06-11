package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorMangrove;
import cn.nukkit.utils.BlockColor;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockDoorMangrove extends BlockDoorWood{
    public BlockDoorMangrove() {
        this(0);
    }

    public BlockDoorMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Door Block";
    }

    @Override
    public int getId() {
        return MANGROVE_DOOR;
    }

    @Override
    public Item toItem() {
        return new ItemDoorMangrove();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWNISH_RED;
    }
}
