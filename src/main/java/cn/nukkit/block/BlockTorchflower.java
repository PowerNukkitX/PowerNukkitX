package cn.nukkit.block;
import cn.nukkit.Player;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.value.SmallFlowerType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;


public class BlockTorchflower extends BlockFlower {
    public BlockTorchflower() {
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        return false;
    }

    public int getId() {
        return TORCHFLOWER;
    }

    public String getName() {
        return "Torchflower";
    }

    @Override
    public void setFlowerType(SmallFlowerType flowerType) {
        setOnSingleFlowerType(SmallFlowerType.TORCHFLOWER, flowerType);
    }

    @Override
    public SmallFlowerType getFlowerType() {
        return SmallFlowerType.TORCHFLOWER;
    }
}