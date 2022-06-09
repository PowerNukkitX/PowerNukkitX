package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorAcacia;
import cn.nukkit.utils.BlockColor;

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
        return new ItemDoorAcacia();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWNISH_RED;
    }
}
