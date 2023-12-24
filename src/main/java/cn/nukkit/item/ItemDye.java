package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.utils.DyeColor;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemDye extends Item {
    public ItemDye() {
        this(0, 1);
    }

    public ItemDye(Integer meta) {
        this(meta, 1);
    }

    public ItemDye(DyeColor dyeColor) {
        this(dyeColor.getItemDyeMeta(), 1);
    }

    public ItemDye(DyeColor dyeColor, int amount) {
        this(dyeColor.getItemDyeMeta(), amount);
    }

    public ItemDye(Integer meta, int amount) {
        super(DYE, meta, amount, meta <= 15 ? DyeColor.getByDyeData(meta).getDyeName() : DyeColor.getByDyeData(meta).getName() + " Dye");

        if (this.aux == DyeColor.BROWN.getDyeData()) {
            this.block = Block.get(BlockID.COCOA);
        }
    }

    public ItemDye(String id) {
        super(id);
    }

    @Override
    public boolean isFertilizer() {
        return getDyeColor().equals(DyeColor.BONE_MEAL);
    }

    public boolean isLapisLazuli() {
        return getDyeColor().equals(DyeColor.BLUE);
    }

    public boolean isCocoaBeans() {
        return getDyeColor().equals(DyeColor.BROWN);
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByDyeData(aux);
    }
}
