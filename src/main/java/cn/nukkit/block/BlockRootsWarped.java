package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockRootsWarped extends BlockRoots implements BlockFlowerPot.FlowerPotBlock {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockRootsWarped() {
        // Does nothing
    }

    @Override
    public int getId() {
        return WARPED_ROOTS;
    }

    @Override
    public String getName() {
        return "Warped Roots";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public CompoundTag getPlantBlockTag() {
        var plantBlock = new CompoundTag("PlantBlock");
        plantBlock.putString("name", "minecraft:warped_roots");
        plantBlock.putCompound("states", new CompoundTag("states"));
        plantBlock.putInt("version", VERSION);
        var item = this.toItem();
        //only exist in PNX
        plantBlock.putInt("itemId", item.getId());
        plantBlock.putInt("itemMeta", item.getDamage());
        return plantBlock;
    }
}
