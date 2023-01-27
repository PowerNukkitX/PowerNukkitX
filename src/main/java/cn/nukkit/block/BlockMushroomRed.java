package cn.nukkit.block;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Pub4Game
 * @since 03.01.2015
 */
public class BlockMushroomRed extends BlockMushroom {

    public BlockMushroomRed() {
        super();
    }

    public BlockMushroomRed(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Red Mushroom";
    }

    @Override
    public int getId() {
        return RED_MUSHROOM;
    }

    @Override
    protected int getType() {
        return 1;
    }

    @Override
    public CompoundTag getPlantBlockTag() {
        var plantBlock = new CompoundTag("PlantBlock");
        plantBlock.putString("name", "minecraft:red_mushroom");
        plantBlock.putCompound("states", new CompoundTag("states"));
        plantBlock.putInt("version", VERSION);
        var item = this.toItem();
        //only exist in PNX
        plantBlock.putInt("itemId", item.getId());
        plantBlock.putInt("itemMeta", item.getDamage());
        return plantBlock;
    }
}
