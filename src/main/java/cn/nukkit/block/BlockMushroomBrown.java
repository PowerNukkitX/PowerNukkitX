package cn.nukkit.block;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Nukkit Project Team
 */
public class BlockMushroomBrown extends BlockMushroom {

    public BlockMushroomBrown() {
        super();
    }

    public BlockMushroomBrown(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Brown Mushroom";
    }

    @Override
    public int getId() {
        return BROWN_MUSHROOM;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    protected int getType() {
        return 0;
    }

    @Override
    public CompoundTag getPlantBlockTag() {
        var plantBlock = new CompoundTag("PlantBlock");
        plantBlock.putString("name", "minecraft:brown_mushroom");
        plantBlock.putCompound("states", new CompoundTag("states"));
        plantBlock.putInt("version", VERSION);
        var item = this.toItem();
        //only exist in PNX
        plantBlock.putInt("itemId", item.getId());
        plantBlock.putInt("itemMeta", item.getDamage());
        return plantBlock;
    }
}
