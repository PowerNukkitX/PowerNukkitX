package cn.nukkit.item;

import cn.nukkit.registry.Registries;

/**
 * @author Leonidius20
 * @since 22.03.17
 */
public class ItemNetherWart extends Item {

    public ItemNetherWart() {
        this(0, 1);
    }

    public ItemNetherWart(Integer meta) {
        this(meta, 1);
    }

    public ItemNetherWart(Integer meta, int count) {
        super(NETHER_WART, meta, count, "Nether Wart");
        this.block = Registries.BLOCK.get(getItemBlockState(NETHER_WART, meta));
    }

    @Override
    public void setAux(Integer aux) {
        this.block = Registries.BLOCK.get(getItemBlockState(NETHER_WART, aux));
        super.setAux(aux);
    }
}
