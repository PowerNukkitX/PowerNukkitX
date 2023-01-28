package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.value.SmallFlowerType;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nonnull;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockDandelion extends BlockFlower {
    public BlockDandelion() {
        this(0);
    }

    public BlockDandelion(int meta) {
        super(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    public int getId() {
        return DANDELION;
    }

    @Override
    protected Block getUncommonFlower() {
        return get(RED_FLOWER);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setFlowerType(SmallFlowerType flowerType) {
        setOnSingleFlowerType(SmallFlowerType.DANDELION, flowerType);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public SmallFlowerType getFlowerType() {
        return SmallFlowerType.DANDELION;
    }

    @Override
    public CompoundTag getPlantBlockTag() {
        var plantBlock = new CompoundTag("PlantBlock");
        plantBlock.putString("name", "minecraft:yellow_flower");
        plantBlock.putCompound("states", new CompoundTag("states"));
        plantBlock.putInt("version", VERSION);
        var item = this.toItem();
        //only exist in PNX
        plantBlock.putInt("itemId", item.getId());
        plantBlock.putInt("itemMeta", item.getDamage());
        return plantBlock;
    }
}
