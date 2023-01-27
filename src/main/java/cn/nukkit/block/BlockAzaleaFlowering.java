package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author LoboMetalurgico
 * @since 13/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockAzaleaFlowering extends BlockAzalea {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockAzaleaFlowering() {
    }

    @Override
    public String getName() {
        return "Flowering Azalea";
    }

    @Override
    public int getId() {
        return FLOWERING_AZALEA;
    }

    @Override
    public CompoundTag getPlantBlockTag() {
        var plantBlock = new CompoundTag("PlantBlock");
        plantBlock.putString("name", "minecraft:flowering_azalea");
        plantBlock.putCompound("states", new CompoundTag("states"));
        plantBlock.putInt("version", VERSION);
        var item = this.toItem();
        //only exist in PNX
        plantBlock.putInt("itemId", item.getId());
        plantBlock.putInt("itemMeta", item.getDamage());
        return plantBlock;
    }
}
