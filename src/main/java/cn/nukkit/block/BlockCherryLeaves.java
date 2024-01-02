package cn.nukkit.block;

import cn.nukkit.block.property.enums.OldLeafType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PERSISTENT_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.UPDATE_BIT;


public class BlockCherryLeaves extends BlockLeaves {

    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_LEAVES,PERSISTENT_BIT, UPDATE_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Cherry Leaves";
    }

    @Override
    protected Item getSapling() {
        return new BlockCherrySapling().toItem();
    }
    /*这里写木质类型为BIRCH只是为了获取凋落物时的概率正确，并不代表真的就是白桦木*/

    @Override
    public OldLeafType getType() {
        return OldLeafType.BIRCH;
    }
}
