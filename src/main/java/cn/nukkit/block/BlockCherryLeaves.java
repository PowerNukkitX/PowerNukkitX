package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;


public class BlockCherryLeaves extends BlockLeaves {

    public static final BlockProperties PROPERTIES = new BlockProperties(PERSISTENT, UPDATE);

    @Override
    public String getName() {
        return "Cherry Leaves";
    }

    @Override
    public int getId() {
        return CHERRY_LEAVES;
    }

    @Override
    protected Item getSapling() {
        return new BlockCherrySapling().toItem();
    }

    /*这里写木质类型为BIRCH只是为了获取凋落物时的概率正确，并不代表真的就是白桦木*/
    @Override
    public WoodType getType() {
        return WoodType.BIRCH;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
}
