package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;


public class BlockDoorWarped extends BlockDoorWood {


    public BlockDoorWarped() {
        this(0);
    }


    public BlockDoorWarped(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Warped Door Block";
    }

    @Override
    public int getId() {
        return WARPED_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.WARPED_DOOR);
    }

    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }
}
