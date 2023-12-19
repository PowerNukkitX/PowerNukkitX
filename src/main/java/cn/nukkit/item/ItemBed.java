package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.utils.DyeColor;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBed extends Item {

    public ItemBed() {
        this(0, 1);
    }

    public ItemBed(Integer meta) {
        this(meta, 1);
    }

    public ItemBed(Integer meta, int count) {
        super(BED, meta, count, "Bed");
        this.block = Block.get(BlockID.BED_BLOCK);
        updateName();
    }

    @Override
    public void setMeta(Integer meta) {
        super.setMeta(meta);
        updateName();
    }

    private void updateName() {
        name = DyeColor.getByWoolData(meta).getName() + " Bed";
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
