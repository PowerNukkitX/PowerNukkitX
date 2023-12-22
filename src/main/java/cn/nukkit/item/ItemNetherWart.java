package cn.nukkit.item;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;

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
        this.block = BlockState.of(BlockID.NETHER_WART_BLOCK, meta).getBlock();
    }

    @Override
    public void setAux(Integer aux) {
        block.setDataStorageFromInt(aux != null ? aux : 0);
        super.setAux(aux);
    }
}
